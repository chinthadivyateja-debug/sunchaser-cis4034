// =============================================================
// MainActivity.kt
//
// This is the only Activity in the whole app (modern Jetpack Compose pattern).
// It holds the NavHost that swaps the four screens:
//   splash  ->  today  <->  history  <->  forecast
// =============================================================

package uk.ac.tees.mad.sunchaser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.sunchaser.screens.ForecastScreen
import uk.ac.tees.mad.sunchaser.screens.HistoryScreen
import uk.ac.tees.mad.sunchaser.screens.SplashScreen
import uk.ac.tees.mad.sunchaser.screens.TodayScreen
import uk.ac.tees.mad.sunchaser.ui.theme.SunChaserTheme
import uk.ac.tees.mad.sunchaser.view_models.LightSensorViewModel

// -------------------------------------------------------------
// Route names used by NavHost. Keeping them in one place avoids typos.
// -------------------------------------------------------------
object Routes {
    const val SPLASH = "splash"
    const val TODAY = "today"
    const val HISTORY = "history"
    const val FORECAST = "forecast"
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunChaserTheme {
                SunChaserApp()
            }
        }
    }
}

// -------------------------------------------------------------
// The root composable of the app.
//   - Creates the NavHostController
//   - Shows a bottom navigation bar on Today / History / Forecast
//   - Hides the bottom bar on the Splash screen
// -------------------------------------------------------------
@Composable
fun SunChaserApp() {

    // The nav controller remembers the current screen and the back stack.
    val navController = rememberNavController()

    // Watch the current destination so we know when to hide the bottom bar.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: String? = backStackEntry?.destination?.route

    // One shared ViewModel for the light sensor — created at app level so the
    // same instance survives when we switch tabs.
    val lightSensorViewModel: LightSensorViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // The splash screen gets no bottom bar.
            val routeNow = currentRoute
            if (routeNow != null && routeNow != Routes.SPLASH) {
                SunChaserBottomBar(navController = navController, currentRoute = routeNow)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {

            // Splash screen — shown only once, when the app opens.
            composable(route = Routes.SPLASH) {
                SplashScreen(
                    onFinished = {
                        // popUpTo removes splash from the back stack so pressing
                        // Back on the Today screen closes the app (expected behaviour).
                        navController.navigate(Routes.TODAY) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Routes.TODAY) {
                TodayScreen(viewModel = lightSensorViewModel)
            }

            composable(route = Routes.HISTORY) {
                HistoryScreen()
            }

            composable(route = Routes.FORECAST) {
                ForecastScreen()
            }
        }
    }
}

// -------------------------------------------------------------
// The bottom navigation bar — three tabs.
// -------------------------------------------------------------
@Composable
fun SunChaserBottomBar(
    navController: NavHostController,
    currentRoute: String
) {
    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == Routes.TODAY,
            onClick = {
                if (currentRoute != Routes.TODAY) {
                    navController.navigate(Routes.TODAY) {
                        popUpTo(Routes.TODAY) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Today") },
            label = { Text("Today") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.HISTORY,
            onClick = {
                if (currentRoute != Routes.HISTORY) {
                    navController.navigate(Routes.HISTORY)
                }
            },
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "History") },
            label = { Text("History") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.FORECAST,
            onClick = {
                if (currentRoute != Routes.FORECAST) {
                    navController.navigate(Routes.FORECAST)
                }
            },
            icon = { Icon(Icons.Filled.Star, contentDescription = "Forecast") },
            label = { Text("Forecast") }
        )
    }
}
