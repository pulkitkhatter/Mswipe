package com.example.mswipe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AadhaarDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aadhaar_details)

        // Setting padding for the main view to handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Schedule a 10-second delay before transitioning to the SymptomsActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SymptomsActivity::class.java)
            startActivity(intent)
        }, 10000) // 10000 milliseconds = 10 seconds
    }
}
