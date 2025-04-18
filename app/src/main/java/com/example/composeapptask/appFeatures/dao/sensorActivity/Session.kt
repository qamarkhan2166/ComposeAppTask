package com.example.composeapptask.appFeatures.dao.sensorActivity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

// Represents a tracking session with start/end time and summary data
@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val startTime: Date,
    var endTime: Date? = null,
    var totalDistance: Double = 0.0
)

// Represents an activity transition (e.g., walking started or stopped) linked to a session
@Entity(
    tableName = "activity_transitions",
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = ["sessionId"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sessionId")]
)
data class ActivityTransition(
    @PrimaryKey(autoGenerate = true) val transitionId: Long = 0,
    val sessionId: Long,
    val timestamp: Date,
    val activityType: String,
    val transitionType: String
)

// Represents sensor data readings like light and pressure, linked to a session
@Entity(
    tableName = "sensor_readings",
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = ["sessionId"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sessionId")]
)
data class SensorReading(
    @PrimaryKey(autoGenerate = true) val readingId: Long = 0,
    val sessionId: Long,
    val timestamp: Date,
    val sensorType: String,
    val value: Float
)

// Represents a geographic location point logged during a session
@Entity(
    tableName = "location_points",
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = ["sessionId"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sessionId")]
)
data class LocationPoint(
    @PrimaryKey(autoGenerate = true) val pointId: Long = 0,
    val sessionId: Long,
    val timestamp: Date,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val accuracy: Float
)
