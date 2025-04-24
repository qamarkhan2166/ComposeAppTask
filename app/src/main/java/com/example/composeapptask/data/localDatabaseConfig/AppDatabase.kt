package com.example.composeapptask.data.localDatabaseConfig

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.composeapptask.appFeatures.dao.sensorActivity.ActivityTransition
import com.example.composeapptask.appFeatures.dao.sensorActivity.ActivityTransitionDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.Converters
import com.example.composeapptask.appFeatures.dao.sensorActivity.LocationPoint
import com.example.composeapptask.appFeatures.dao.sensorActivity.LocationPointDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.MedicineReminder
import com.example.composeapptask.appFeatures.dao.sensorActivity.MedicineReminderDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.SensorReading
import com.example.composeapptask.appFeatures.dao.sensorActivity.SensorReadingDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.Session
import com.example.composeapptask.appFeatures.dao.sensorActivity.SessionDao
import com.example.composeapptask.appFeatures.dao.taskFeature.TaskEntity
import com.example.composeapptask.appFeatures.dao.taskFeature.TaskEntityDao

@Database(
    entities = [
        TaskEntity::class, MedicineReminder::class,
        Session::class, ActivityTransition::class, SensorReading::class, LocationPoint::class
               ],
    version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskEntityDao(): TaskEntityDao
    abstract fun medicineReminderDao(): MedicineReminderDao
    abstract fun sessionDao(): SessionDao
    abstract fun activityTransitionDao(): ActivityTransitionDao
    abstract fun sensorReadingDao(): SensorReadingDao
    abstract fun locationPointDao(): LocationPointDao
}
// @TypeConverters(DateConverter::class)
