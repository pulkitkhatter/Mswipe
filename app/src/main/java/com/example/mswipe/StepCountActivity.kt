package com.example.mswipe

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StepCountActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount = 0
    private lateinit var stepCountText: TextView
    private lateinit var startStopButton: Button
    private lateinit var endButton: Button
    private lateinit var timerText: TextView
    private val PERMISSION_REQUEST_CODE = 1001
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var isCounting = false
    private var remainingTimeInMillis: Long = 6 * 60 * 1000 // 6 minutes in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_step_count)

        // Initialize views
        stepCountText = findViewById(R.id.stepCountText)
        startStopButton = findViewById(R.id.startStopButton)
        endButton = findViewById(R.id.endButton)
        timerText = findViewById(R.id.timerText)

        // Set up SensorManager and Step Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        // Check and request permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), PERMISSION_REQUEST_CODE)
        }

        // Start/Stop button listener
        startStopButton.setOnClickListener {
            if (!isTimerRunning) {
                startStepCounting()
            } else {
                pauseStepCounting()
            }
        }

        // End button listener
        endButton.setOnClickListener {
            endStepCounting()
        }

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun startStepCounting() {
        isCounting = true
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
        startStopButton.text = "Pause"
        if (!isTimerRunning) {
            startTimer()
        } else {
            resumeTimer()
        }
    }

    private fun pauseStepCounting() {
        isTimerRunning = false
        sensorManager.unregisterListener(this)
        startStopButton.text = "Start"
        countDownTimer?.cancel()
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun startTimer() {
        isTimerRunning = true
        countDownTimer = object : CountDownTimer(remainingTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMillis = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerText.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                timerText.text = "00:00"
                startStopButton.text = "Save Data"
                stepCountText.text = "You walked $stepCount steps in 6 minutes!"
                startStopButton.isEnabled = false
                showSaveDataMessage()
            }
        }.start()
    }

    private fun endStepCounting() {
        if (isTimerRunning) {
            countDownTimer?.cancel()
        }
        startStopButton.text = "Start"
        stepCountText.text = "You walked $stepCount steps in ${6 - remainingTimeInMillis / 1000 / 60} minutes."
        startStopButton.isEnabled = false
        endButton.isEnabled = false
        showSaveDataMessage()
    }

    private fun showSaveDataMessage() {
        // You can add a Toast or Dialog here to inform the user that data is being saved.
        startStopButton.text = "Please rest while we register your data."
    }

    // SensorEventListener to count steps
    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let { event ->
            if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                stepCount++
                stepCountText.text = "Steps: $stepCount"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this case
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (isCounting) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can start using the sensor
            } else {
                // Permission denied, show a message or handle accordingly
                stepCountText.text = "Permission Denied"
            }
        }
    }
}
