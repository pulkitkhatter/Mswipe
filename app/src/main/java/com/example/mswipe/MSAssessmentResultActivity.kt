package com.example.mswipe

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.random.Random

class MSAssessmentResultActivity : AppCompatActivity() {
    private val lastRunningActivityValues = listOf(75f, 55f, 60f, 80f, 90f, 70f, 30f, 30f, 26f, 22f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_msassessment_result)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the last four percentages from Intent extras
        val lastFourPercentages = intent.getIntegerArrayListExtra("LAST_FOUR_SYMPTOMS") ?: arrayListOf()

        // Initialize LineCharts
        val lineChart: LineChart = findViewById(R.id.chart1)
        val waveChart: LineChart = findViewById(R.id.waveChart)  // New chart for wave graph

        if (lastFourPercentages.isNotEmpty()) {
            // Pass a copy of the list to avoid modifying the original array
            setUpLineChart(lineChart, lastFourPercentages.toList())
        } else {
            // Handle the case where no data is available
            lineChart.setNoDataText("No data available for chart.")
        }

        // Setup the wave graph with random data points (initialize with default random values)
        setUpWaveChart(waveChart)

        // Set up button listeners
        val lastRunningButton = findViewById<Button>(R.id.lastRunningButton)
        val allTimeButton = findViewById<Button>(R.id.allTimeButton)
        lastRunningButton.setOnClickListener {
            // When "Last Running Activity" is clicked, show the fixed array
            setUpRunningProgressChart(waveChart, isAllTime = false)
        }
        allTimeButton.setOnClickListener {
            // When "All Time" is clicked, generate random values
            setUpRunningProgressChart(waveChart, isAllTime = true)
        }
    }

    private fun setUpLineChart(chart: LineChart, percentages: List<Int>) {
        // Convert percentages to Entries for the chart
        val entries = percentages.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }

        // Create a LineDataSet
        val dataSet = LineDataSet(entries, "Symptoms Progress").apply {
            color = resources.getColor(android.R.color.holo_blue_light, theme)
            setCircleColor(resources.getColor(android.R.color.holo_blue_dark, theme))
            valueTextColor = resources.getColor(android.R.color.black, theme)
            lineWidth = 2f
            setDrawCircles(true)
            setDrawValues(true)
        }

        // Create LineData
        val lineData = LineData(dataSet)
        chart.data = lineData

        // Customize the X-Axis (Days)
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            labelCount = percentages.size
            valueFormatter = XAxisFormatter() // Custom labels for X-axis
        }

        // Customize the Y-Axis (Percentages)
        chart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 100f
            granularity = 10f
        }
        chart.axisRight.isEnabled = false

        // Customize other chart properties
        chart.apply {
            description.isEnabled = false
            setPinchZoom(true)
            animateX(1500)
            invalidate() // Refresh chart
        }
    }

    private fun setUpWaveChart(chart: LineChart) {
        // Generate random wave-like data points
        val entries = mutableListOf<Entry>()
        for (i in 0..9) {
            val x = i.toFloat()
            val y = (50 + (Random.nextFloat() * 50)).toFloat() // Random Y-values between 50 and 100
            entries.add(Entry(x, y))
        }

        // Create a LineDataSet for the wave graph
        val waveDataSet = LineDataSet(entries, "Wave Progress").apply {
            color = resources.getColor(android.R.color.holo_green_light, theme)  // Different color for wave
            setCircleColor(resources.getColor(android.R.color.holo_green_dark, theme))
            valueTextColor = resources.getColor(android.R.color.black, theme)
            lineWidth = 3f
            setDrawCircles(true)
            setDrawValues(false)  // No values displayed on the wave chart
            setMode(LineDataSet.Mode.CUBIC_BEZIER) // Makes the graph appear smoother (wave-like)
        }

        // Create LineData for the wave graph
        val waveLineData = LineData(waveDataSet)
        chart.data = waveLineData

        // Customize the X-Axis (Days) for wave graph (just like the first chart)
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            labelCount = 4  // Make sure it matches the number of days
            valueFormatter = XAxisFormatter() // Custom labels for X-axis (same formatter as before)
        }

        // Customize the Y-Axis (Wave graph values)
        chart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 100f
            granularity = 10f
        }
        chart.axisRight.isEnabled = false

        // Customize other chart properties for wave
        chart.apply {
            description.isEnabled = false
            setPinchZoom(true)
            animateX(1500)
            invalidate() // Refresh chart
        }
    }
    private fun setUpRunningProgressChart(chart: LineChart, isAllTime: Boolean) {
        val entries = mutableListOf<Entry>()
        val dataSetLabel = if (isAllTime) "All Time Running Activity" else "Last Running Activity"

        if (isAllTime) {
            // Generate random values for "All Time" between 0 and 100
            for (i in 0..9) {
                val x = i.toFloat()
                val y = (Random.nextFloat() * 100).toFloat() // Random Y-values between 0 and 100
                entries.add(Entry(x, y))
            }
        } else {
            // Use fixed array for "Last Running Activity"
            lastRunningActivityValues.forEachIndexed { index, value ->
                entries.add(Entry(index.toFloat(), value))
            }
        }

        // Create a LineDataSet for the running progress graph
        val runningDataSet = LineDataSet(entries, dataSetLabel).apply {
            // Dynamically change the circle color based on the value of Y
            for (entry in entries) {
                entry.icon = if (entry.y < 50) {
                    resources.getDrawable(android.R.drawable.presence_busy, theme) // Red for values below 50
                } else {
                    resources.getDrawable(android.R.drawable.presence_online, theme) // Green for values above 50
                }
            }

            valueTextColor = resources.getColor(android.R.color.black, theme)
            lineWidth = 3f
            setDrawCircles(true)
            setDrawValues(true)  // Ensure values are drawn on the chart
            setMode(LineDataSet.Mode.CUBIC_BEZIER) // Makes the graph appear smoother (wave-like)

            // Dynamically change the line color based on whether any entry is below 50
            color = if (entries.any { it.y < 50 }) resources.getColor(android.R.color.holo_red_light, theme) else resources.getColor(android.R.color.holo_green_light, theme)
        }

        // Create LineData for the running progress graph
        val runningLineData = LineData(runningDataSet)
        chart.data = runningLineData

        // Customize the X-Axis (ensure that it reflects all entries)
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            labelCount = entries.size  // Ensure the number of X-axis labels matches the number of entries
            valueFormatter = XAxisFormatter() // Custom labels for X-axis (Day 1, Day 2, etc.)
        }

        // Customize the Y-Axis (ensure it's properly scaled for the data)
        chart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 100f  // Set maximum to 100 to ensure chart is scaled for expected range
            granularity = 10f
        }
        chart.axisRight.isEnabled = false  // Disable right axis

        // Ensure that the Y-axis scaling works correctly
        chart.invalidate() // Refresh chart
    }



    // Custom Formatter for X-Axis Labels (Day 1, Day 2, Day 3, Day 4)
    private class XAxisFormatter : ValueFormatter() {
        private val days = arrayOf("Day 1", "Day 2", "Day 3", "Day 4")
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in days.indices) days[index] else ""
        }
    }
}
