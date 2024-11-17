package com.example.mswipe

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MSTestsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mstests)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the last four percentages from Intent extras
        val lastFourPercentages = intent.getIntegerArrayListExtra("LAST_FOUR_SYMPTOMS") ?: arrayListOf()

        // ListView setup
        val testOptions = listOf(
            "Step Count in 6 Minutes",
            "Symptoms",
            "Test 3"
        )
        val listView = findViewById<ListView>(R.id.listViewTests)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, testOptions)
        listView.adapter = adapter

        // Handle item clicks
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> { // Step Count in 6 Minutes
                    val intent = Intent(this, StepCountActivity::class.java)
                    startActivity(intent)
                }
                1 -> { // Symptoms
                    val intent = Intent(this, SymptomsActivity::class.java).apply {
                        putExtra("LAST_FOUR_SYMPTOMS", lastFourPercentages)
                    }
                    startActivity(intent)
                }
                2 -> { // Test 3
                    val intent = Intent(this, Test3Activity::class.java) // Replace with your Test3Activity
                    startActivity(intent)
                }
            }
        }

        // My MS Assessment Button
        val buttonMSAssessment = findViewById<Button>(R.id.buttonMSAssessment)
        buttonMSAssessment.setOnClickListener {
            val intent = Intent(this, MSAssessmentResultActivity::class.java).apply {
                putExtra("LAST_FOUR_SYMPTOMS", lastFourPercentages)
            }
            startActivity(intent)
        }

        // Show last four percentages in a Toast
        if (lastFourPercentages.isNotEmpty()) {
            Toast.makeText(this, "Last 4 Symptoms: $lastFourPercentages", Toast.LENGTH_LONG).show()
        }
    }
}
