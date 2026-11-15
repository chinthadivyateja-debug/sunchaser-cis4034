// =============================================================
// SplashScreen.kt
//
// Shows the app name and tagline for 2 seconds, then calls
// onFinished so MainActivity can navigate to the Today screen.
// =============================================================

package uk.ac.tees.mad.sunchaser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // LaunchedEffect runs once when this screen first appears.
    // After 2 seconds we tell MainActivity we are done.
    LaunchedEffect(key1 = Unit) {
        delay(2000L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // A simple warm colour — the sun!
            .background(Color(0xFFFFC107)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SunChaser",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chase the light.",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
