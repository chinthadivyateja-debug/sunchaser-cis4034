// =============================================================
// LightSensorViewModel.kt
//
// Reads the ambient light sensor (TYPE_LIGHT) and exposes the
// current lux value to the UI via StateFlow.
//
// Also supports a "simulate daylight" mode so the app can be
// demonstrated on an emulator (which has no real sensor data).
// =============================================================

package uk.ac.tees.mad.sunchaser.view_models

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// Possible states the sensor can be in.
// -------------------------------------------------------------
sealed class LightSensorState {
    // Phone has no ambient light sensor at all (rare — very old / cheap device).
    object Unavailable : LightSensorState()

    // Normal case — a lux reading is available.
    data class Reading(val lux: Float, val isSimulated: Boolean) : LightSensorState()
}

// -------------------------------------------------------------
// ViewModel extends AndroidViewModel because we need an Application
// context to reach SensorManager.
// -------------------------------------------------------------
class LightSensorViewModel(application: Application) : AndroidViewModel(application),
    SensorEventListener {

    // SensorManager is Android's door to all the hardware sensors.
    private val sensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // The actual light sensor, or null if the phone has none.
    private val lightSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    // Private, writable state. Starts in "Reading 0 lux".
    private val _state = MutableStateFlow<LightSensorState>(
        if (lightSensor == null) LightSensorState.Unavailable
        else LightSensorState.Reading(lux = 0f, isSimulated = false)
    )

    // Public, read-only state the UI collects with collectAsStateWithLifecycle().
    val state: StateFlow<LightSensorState> = _state

    // When the simulate button is pressed, this remembers for how many more seconds
    // we should keep pretending the sensor is reading 10,000 lux.
    private var simulatedSecondsLeft: Int = 0

    init {
        // Start listening to the sensor straight away. We stop in onCleared().
        if (lightSensor != null) {
            sensorManager.registerListener(
                this,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    // -------------------------------------------------------------
    // SensorEventListener callbacks
    // -------------------------------------------------------------

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_LIGHT) return

        val realLux = event.values[0]

        // If we are still inside a simulated 30-second window, keep showing the
        // simulated value. Otherwise show the true sensor reading.
        if (simulatedSecondsLeft > 0) {
            _state.value = LightSensorState.Reading(lux = 10_000f, isSimulated = true)
        } else {
            _state.value = LightSensorState.Reading(lux = realLux, isSimulated = false)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this app.
    }

    // -------------------------------------------------------------
    // Called by the UI when the user taps "Simulate Daylight".
    // -------------------------------------------------------------
    fun simulateDaylightForThirtySeconds() {
        simulatedSecondsLeft = 30

        // Launch a coroutine that decreases the counter every second and
        // updates the UI so the countdown is visible.
        viewModelScope.launch {
            while (simulatedSecondsLeft > 0) {
                _state.value = LightSensorState.Reading(lux = 10_000f, isSimulated = true)
                delay(1000L)
                simulatedSecondsLeft--
            }
            // When the window ends, fall back to the last real reading (0 on an emulator).
            _state.value = LightSensorState.Reading(lux = 0f, isSimulated = false)
        }
    }

    // Exposed so the UI can show the countdown number.
    fun secondsLeftInSimulation(): Int = simulatedSecondsLeft

    // -------------------------------------------------------------
    // Clean-up when the ViewModel is destroyed.
    // -------------------------------------------------------------
    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
