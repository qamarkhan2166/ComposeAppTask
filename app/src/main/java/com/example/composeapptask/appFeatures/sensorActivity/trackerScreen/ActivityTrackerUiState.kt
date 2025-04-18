package com.example.composeapptask.appFeatures.sensorActivity.trackerScreen

data class ActivityTrackerUiState(
    val currentActivity: ActivityType = ActivityType.STATIONARY,
    val activityDuration: Long = 0L,
    val isActivityStarted: Boolean = false,
    val totalDistance: Double = 0.0,
    val currentLight: Float = 0f,
    val currentPressure: Float = 0f,
    val sessionId: Long = 0L
)
