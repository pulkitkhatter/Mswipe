import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mswipe.AadhaarDatabase
import com.example.mswipe.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AadhaarDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database
        database = AadhaarDatabase.getDatabase(this)

        // Set up click listener for the submit button
        binding.submitButton.setOnClickListener {
            val aadhaarNumber = binding.aadhaarEt.text.toString()
            if (isValidAadhaarNumber(aadhaarNumber)) {
                // Save Aadhaar number to the database
                lifecycleScope.launch(Dispatchers.IO) {
                    database.aadhaarDao().insert(Aadhaar(aadhaarNumber = aadhaarNumber))
                }
                Toast.makeText(this, "Aadhaar saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid 12-digit Aadhaar number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidAadhaarNumber(aadhaarNumber: String): Boolean {
        return aadhaarNumber.matches(Regex("^[0-9]{12}$"))
    }
}
