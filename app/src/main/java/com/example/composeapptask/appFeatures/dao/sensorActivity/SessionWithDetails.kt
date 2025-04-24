package com.example.composeapptask.appFeatures.dao.sensorActivity

import androidx.room.Embedded
import androidx.room.Relation

// Data class to hold a Session and its related records
data class SessionWithDetails(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val activityTransitions: List<ActivityTransition>,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val sensorReadings: List<SensorReading>,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val locationPoints: List<LocationPoint>
)
