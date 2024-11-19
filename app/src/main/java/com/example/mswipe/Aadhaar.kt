import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aadhaar_table")
data class Aadhaar(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val aadhaarNumber: String
)
