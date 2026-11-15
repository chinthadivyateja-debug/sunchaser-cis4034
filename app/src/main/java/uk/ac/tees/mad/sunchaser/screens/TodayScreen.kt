// =============================================================
// TodayScreen.kt
//
// The main screen of Sprint 1.
//   - Shows the live lux reading inside a coloured circle (Canvas)
//   - Colour is RED below 500 lux, AMBER below 1000, GREEN above 1000
//     (same thresholds as Julien's Document,,.pdf practical exercise)
//   - A "Simulate Daylight" button is shown only in debug builds
// =============================================================

package uk.ac.tees.mad.sunchaser.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.ac.tees.mad.sunchaser.BuildConfig
import uk.ac.tees.mad.sunchaser.view_models.LightSensorState
import uk.ac.tees.mad.sunchaser.view_models.LightSensorViewModel

// Thresholds — same green/amber/red logic as Julien's sensor-colour-coding
// practical in Document,,.pdf.
private const val BRIGHT_THRESHOLD = 1000f   // lux — outdoors / near a window
private const val DIM_THRESHOLD = 500f       // lux — office lighting

@Composable
fun TodayScreen(viewModel: LightSensorViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Today",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (val s = state) {

            is LightSensorState.Unavailable -> {
                Text(
                    text = "This phone has no ambient light sensor.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is LightSensorState.Reading -> {
                LightMeterCircle(lux = s.lux)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${s.lux.toInt()} lux",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = brightnessLabel(s.lux),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (s.isSimulated) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "(Simulated — ${viewModel.secondsLeftInSimulation()} s left)",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (BuildConfig.DEBUG) {
                    Button(onClick = { viewModel.simulateDaylightForThirtySeconds() }) {
                        Text("Simulate Daylight (30 s)")
                    }
                }
            }
        }
    }
}

@Composable
fun LightMeterCircle(lux: Float) {

    val colour = when {
        lux < DIM_THRESHOLD -> Color(0xFFD32F2F)     // red
        lux < BRIGHT_THRESHOLD -> Color(0xFFFFA000)  // amber
        else -> Color(0xFF388E3C)                    // green
    }

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = colour)
        }
    }
}

private fun brightnessLabel(lux: Float): String {
    return when {
        lux < DIM_THRESHOLD -> "Too dim — step closer to a window"
        lux < BRIGHT_THRESHOLD -> "Indoor light — some sunlight would help"
        else -> "Bright light — this counts as real daylight!"
    }
}
