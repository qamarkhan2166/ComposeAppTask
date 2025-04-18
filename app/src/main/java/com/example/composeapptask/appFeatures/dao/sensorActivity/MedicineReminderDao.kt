package com.example.composeapptask.appFeatures.dao.sensorActivity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: MedicineReminder): Long

    @Update
    suspend fun update(reminder: MedicineReminder)

    @Delete
    suspend fun delete(reminder: MedicineReminder)

    @Query("SELECT * from medicineReminders")
    fun getAllReminders(): Flow<List<MedicineReminder>>

    @Query("SELECT * from medicineReminders WHERE isActive = 1")
    fun getActiveReminders(): Flow<List<MedicineReminder>>

    @Query("DELETE FROM medicineReminders WHERE id = :id")
    suspend fun deleteReminderById(id: Int)

}