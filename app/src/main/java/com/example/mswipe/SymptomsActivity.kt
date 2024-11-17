package com.example.mswipe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SymptomsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_symptoms)

        // Apply edge-to-edge window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val submitButton = findViewById<Button>(R.id.submitSymptomsButton)
        submitButton.setOnClickListener {
            val percentage = calculateSelectedSymptomsPercentage()

            // Retrieve the last four percentages from Intent extras
            val lastFourPercentages = intent.getIntegerArrayListExtra("LAST_FOUR_SYMPTOMS") ?: arrayListOf()

            // Update the list to include the new percentage
            if (lastFourPercentages.size == 4) {
                lastFourPercentages.removeAt(0) // Keep only the last 4 percentages
            }
            lastFourPercentages.add(percentage)

            // Pass the updated list to MSTestsActivity
            val intent = Intent(this, MSTestsActivity::class.java).apply {
                putExtra("LAST_FOUR_SYMPTOMS", lastFourPercentages)
            }
            startActivity(intent)
            finish() // Close SymptomsActivity
        }
    }

    private fun calculateSelectedSymptomsPercentage(): Int {
        val symptomCheckboxIds = listOf(
            R.id.checkbox_fatigue,
            R.id.checkbox_vision_issues,
            R.id.checkbox_numbness,
            R.id.checkbox_muscle_spasms,
            R.id.checkbox_memory_issues,
            R.id.checkbox_walking_difficulty,
            R.id.checkbox_coordination_issues,
            R.id.checkbox_bladder_problems,
            R.id.checkbox_bowel_issues,
            R.id.checkbox_depression,
            R.id.checkbox_anxiety,
            R.id.checkbox_speech_issues,
            R.id.checkbox_sexual_dysfunction
        )
        val selectedCount = symptomCheckboxIds.count { id ->
            findViewById<CheckBox>(id).isChecked
        }
        val totalSymptoms = symptomCheckboxIds.size
        return (selectedCount * 100) / totalSymptoms
    }
}
