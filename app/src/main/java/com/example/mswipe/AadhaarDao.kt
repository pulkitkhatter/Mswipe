import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AadhaarDao {
    @Insert
    suspend fun insert(aadhaar: Aadhaar)

    @Query("SELECT * FROM aadhaar_table")
    suspend fun getAllAadhaar(): List<Aadhaar>
}
