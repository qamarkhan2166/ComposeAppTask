package com.example.composeapptask.appFeatures.sensorActivity.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.composeapptask.R
import com.example.composeapptask.appFeatures.dao.sensorActivity.ActivityTransitionDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.LocationPoint
import com.example.composeapptask.appFeatures.dao.sensorActivity.LocationPointDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.SensorReading
import com.example.composeapptask.appFeatures.dao.sensorActivity.SensorReadingDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.Session
import com.example.composeapptask.appFeatures.dao.sensorActivity.SessionDao
import com.example.composeapptask.appFeatures.sensorActivity.receiver.ActivityTransitionsReceiver
import com.example.composeapptask.appFeatures.sensorActivity.trackerScreen.ActivityTrackerUiState
import com.example.composeapptask.appFeatures.sensorActivity.trackerScreen.ActivityType
import com.google.android.gms.location.*
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date
import javax.inject.Inject
import kotlin.math.sqrt

@AndroidEntryPoint
class ActivityTrackingService : Service(), SensorEventListener {

    @Inject
    lateinit var sensorManager: SensorManager

    @Inject
    lateinit var sessionDao: SessionDao
    @Inject
    lateinit var activityTransitionDao: ActivityTransitionDao
    @Inject
    lateinit var sensorReadingDao: SensorReadingDao
    @Inject
    lateinit var locationPointDao: LocationPointDao

    private val lightSensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    private val pressureSensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) }
    private val accelerometer by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    private val gyroscope by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) }
    private var lastUpdateTime = System.currentTimeMillis()

    private lateinit var activityRecognitionClient: ActivityRecognitionClient
    private lateinit var activityTransitionsPendingIntent: PendingIntent
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var lastLocation: Location? = null
    private var totalDistance = 0.0
    private var currentLight: Float = 0f
    private var currentPressure: Float = 0f
    private var lastActivityChangeTime = System.currentTimeMillis()
    private var currentActivity = ActivityType.STATIONARY
    private var activityDuration: Long = 0
    private var currentSessionId: Long? = null

    private val binder = ActivityTrackingBinder()
    private val CHANNEL_ID = "activity_tracking_channel"
    private val NOTIFICATION_ID = 1001
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _activityUpdates = MutableSharedFlow<ActivityTrackerUiState>(replay = 1)
    val activityUpdates = _activityUpdates.asSharedFlow()

    /**
     * Handles location updates and calculates distance traveled between points.
     * Logs location data to database when session is active.
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                lastLocation?.let {
                    totalDistance += it.distanceTo(location)
                }
                lastLocation = location

                currentSessionId?.let { sessionId ->
                    serviceScope.launch {
                        locationPointDao.insertLocationPoint(
                            LocationPoint(
                                sessionId = sessionId,
                                timestamp = Date(location.time),
                                latitude = location.latitude,
                                longitude = location.longitude,
                                altitude = location.altitude,
                                speed = location.speed,
                                accuracy = location.accuracy
                            )
                        )
                    }
                }
                emitActivityUpdate()
            }
        }
    }

    companion object {
        private const val ACTIVITY_TRANSITION_REQUEST_CODE = 1002
        private const val LOCATION_REQUEST_INTERVAL_MS: Long = 10000
        private const val LOCATION_REQUEST_FASTEST_INTERVAL_MS: Long = 5000
    }

    /**
     * Initializes service components including:
     * - Activity recognition client
     * - Location services
     * - Notification channel
     * - Sensor registration
     */
    override fun onCreate() {
        super.onCreate()
        activityRecognitionClient = ActivityRecognition.getClient(this)
        activityTransitionsPendingIntent = createActivityTransitionsPendingIntent()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        startActivityRecognition()
        registerSensors()
    }

    /**
     * Handles service start commands and initiates new tracking sessions.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_START_NEW_SESSION" -> startNewSession()
        }
        emitActivityUpdate()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        emitActivityUpdate()
        return binder
    }

    override fun onRebind(intent: Intent?) {
        emitActivityUpdate()
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return false
    }

    /**
     * Cleans up resources when service is destroyed:
     * - Finalizes current session
     * - Stops activity recognition
     * - Unregisters sensors
     * - Cancels coroutine scope
     */
    override fun onDestroy() {
        stopTrackingAndFinalizeSessionInternal()
        stopActivityRecognition()
        sensorManager.unregisterListener(this)
        stopLocationUpdates()
        serviceScope.cancel()
        super.onDestroy()
    }

    /**
     * Creates location request with high accuracy settings.
     */
    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_REQUEST_INTERVAL_MS)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(LOCATION_REQUEST_FASTEST_INTERVAL_MS)
            .build()
    }

    /**
     * Processes sensor data changes and routes to appropriate handlers.
     */
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> processAccelerometerDataFallback(event.values)
            Sensor.TYPE_GYROSCOPE -> processGyroscopeData()
            Sensor.TYPE_LIGHT -> processLightSensorData(event.values[0])
            Sensor.TYPE_PRESSURE -> processPressureSensorData(event.values[0])
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    /**
     * Handles light sensor data updates and logs to database.
     */
    private fun processLightSensorData(lightValue: Float) {
        currentLight = lightValue
        currentSessionId?.let { sessionId ->
            serviceScope.launch {
                sensorReadingDao.insertSensorReading(
                    SensorReading(
                        sessionId = sessionId,
                        timestamp = Date(),
                        sensorType = "LIGHT",
                        value = currentLight
                    )
                )
            }
        }
        emitActivityUpdate()
    }

    /**
     * Handles pressure sensor data updates and logs to database.
     */
    private fun processPressureSensorData(pressureValue: Float) {
        currentPressure = pressureValue
        currentSessionId?.let { sessionId ->
            serviceScope.launch {
                sensorReadingDao.insertSensorReading(
                    SensorReading(
                        sessionId = sessionId,
                        timestamp = Date(),
                        sensorType = "PRESSURE",
                        value = currentPressure
                    )
                )
            }
        }
        emitActivityUpdate()
    }

    /**
     * Fallback activity detection using accelerometer data.
     */
    private fun processAccelerometerDataFallback(values: FloatArray) {
        val (x, y, z) = values
        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastUpdateTime > 500) {
            lastUpdateTime = currentTime
            detectActivityFallback(acceleration)
        }
    }

    private fun processGyroscopeData() {}

    /**
     * Detects activity type based on accelerometer readings.
     */
    private fun detectActivityFallback(acceleration: Float) {
        val newActivity = when {
            acceleration > 2.5 -> ActivityType.RUNNING
            acceleration > 0.8 -> ActivityType.WALKING
            else -> ActivityType.STATIONARY
        }

        if (newActivity != currentActivity) {
            lastActivityChangeTime = System.currentTimeMillis()
            currentActivity = newActivity
            updateNotification()
            emitActivityUpdate()
        }
        activityDuration = System.currentTimeMillis() - lastActivityChangeTime
        emitActivityUpdate()
    }

    /**
     * Handles activity transition events from ActivityRecognition API.
     */
    fun handleActivityTransition(activityType: Int, transitionType: Int) {
        val newActivity = when (activityType) {
            DetectedActivity.STILL -> ActivityType.STATIONARY
            DetectedActivity.WALKING -> ActivityType.WALKING
            DetectedActivity.RUNNING -> ActivityType.RUNNING
            else -> ActivityType.UNKNOWN
        }

        currentSessionId?.let { sessionId ->
            serviceScope.launch {
                activityTransitionDao.insertActivityTransition(
                    com.example.composeapptask.appFeatures.dao.sensorActivity.ActivityTransition(
                        sessionId = sessionId,
                        timestamp = Date(),
                        activityType = newActivity.name,
                        transitionType = if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) "ENTER" else "EXIT"
                    )
                )
            }
        }

        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            if (newActivity != currentActivity) {
                currentActivity = newActivity
                lastActivityChangeTime = System.currentTimeMillis()
                updateNotification()
                emitActivityUpdate()
            }
        } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            activityDuration = System.currentTimeMillis() - lastActivityChangeTime
            updateNotification()
            emitActivityUpdate()
        }
        activityDuration = System.currentTimeMillis() - lastActivityChangeTime
        emitActivityUpdate()
    }

    /**
     * Starts activity recognition updates with configured transitions.
     */
    @SuppressLint("MissingPermission")
    private fun startActivityRecognition() {
        val transitions = listOf(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )

        val request = ActivityTransitionRequest(transitions)
        activityRecognitionClient.requestActivityTransitionUpdates(
            request,
            activityTransitionsPendingIntent
        ).addOnSuccessListener {
            Log.d("ActivityRecognition", "Activity transition updates requested successfully")
        }.addOnFailureListener { e ->
            Log.e("ActivityRecognition", "Error requesting activity transition updates", e)
            registerSensors()
        }
    }

    private fun registerSensors() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    @SuppressLint("MissingPermission")
    private fun stopActivityRecognition() {
        activityRecognitionClient.removeActivityTransitionUpdates(activityTransitionsPendingIntent)
            .addOnSuccessListener {
                Log.d("ActivityRecognition", "Activity transition updates removed successfully")
            }.addOnFailureListener { e ->
                Log.e("ActivityRecognition", "Error removing activity transition updates", e)
            }
    }

    /**
     * Emits current activity state to all subscribers.
     */
    private fun emitActivityUpdate() {
        serviceScope.launch {
            _activityUpdates.emit(
                ActivityTrackerUiState(
                    currentActivity = currentActivity,
                    activityDuration = activityDuration,
                    totalDistance = totalDistance,
                    currentLight = currentLight,
                    currentPressure = currentPressure,
                    isActivityStarted = true
                )
            )
        }
    }

    /**
     * Creates notification channel for foreground service.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                /* id = */ CHANNEL_ID,
                /* name = */ "Activity Tracking",
                /* importance = */ NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks your physical activity in the background"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(false)
                enableLights(false)
            }.let { channel ->
                getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
            }
        }
    }

    /**
     * Creates foreground service notification with current activity info.
     */
    @SuppressLint("DefaultLocale")
    private fun createNotification(): Notification {
        val activityText = when (currentActivity) {
            ActivityType.RUNNING -> "Running"
            ActivityType.WALKING -> "Walking"
            ActivityType.STATIONARY -> "Stationary"
            ActivityType.UNKNOWN -> "Detecting activity"
        }
        val contentText = "$activityText | ${activityDuration.toFormattedTime()} | Dist: ${String.format("%.2f km", totalDistance/1000.0)}"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Activity Tracker Active")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.jetpack_compose_logo)
            .setContentIntent(getPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        return intent?.let {
            PendingIntent.getActivity(
                /* context = */ this,
                /* requestCode = */ 0,
                /* intent = */ it.apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP },
                /* flags = */ PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private fun updateNotification() {
        try {
            getSystemService(NotificationManager::class.java)
                .notify(NOTIFICATION_ID, createNotification())
        } catch (e: SecurityException) {
            Log.e("ActivityService", "Failed to update notification", e)
        } catch (e: Exception) {
            Log.e("ActivityService", "Error updating notification", e)
        }
    }

    private fun createActivityTransitionsPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityTransitionsReceiver::class.java).apply {
            action = "com.example.composeapptask.ACTION_ACTIVITY_TRANSITION"
        }

        return PendingIntent.getBroadcast(
            this,
            ACTIVITY_TRANSITION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * Starts new tracking session with fresh database entry.
     */
    private fun startNewSession() {
        if (currentSessionId != null) return

        serviceScope.launch {
            try {
                val newSession = Session(startTime = Date())
                val id = sessionDao.insertSession(newSession)
                currentSessionId = id

                totalDistance = 0.0
                currentLight = 0f
                currentPressure = 0f
                currentActivity = ActivityType.STATIONARY
                activityDuration = 0L
                lastActivityChangeTime = System.currentTimeMillis()
                lastLocation = null

                registerSensors()
                startLocationUpdates()
                startActivityRecognition()
                emitActivityUpdate()

            } catch (e: Exception) {
                Log.e("ActivityService", "Error starting new session", e)
                stopSelf()
            }
        }
    }

    /**
     * Stops tracking and finalizes current session in database.
     */
    fun stopTrackingAndFinalizeSession() {
        stopTrackingAndFinalizeSessionInternal()
    }

    private fun stopTrackingAndFinalizeSessionInternal() {
        if (currentSessionId == null) {
            stopSelf(START_REDELIVER_INTENT)
            return
        }

        serviceScope.launch {
            try {
                stopActivityRecognition()
                sensorManager.unregisterListener(this@ActivityTrackingService)
                stopLocationUpdates()

                currentSessionId?.let { sessionId ->
                    val sessionToUpdate = sessionDao.getSessionById(sessionId).firstOrNull()
                    sessionToUpdate?.let { session ->
                        session.endTime = Date()
                        session.totalDistance = totalDistance
                        sessionDao.updateSession(session)
                    }
                }

                currentSessionId = null
                lastLocation = null
                totalDistance = 0.0
                currentLight = 0f
                currentPressure = 0f
                currentActivity = ActivityType.STATIONARY
                activityDuration = 0L
                lastActivityChangeTime = System.currentTimeMillis()

                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf(START_REDELIVER_INTENT)

            } catch (e: Exception) {
                Log.e("ActivityService", "Error stopping tracking", e)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf(START_REDELIVER_INTENT)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !checkPermissions()) return
        if (!isLocationEnabled()) return

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("ActivityService", "Location permission denied", e)
        } catch (e: Exception) {
            Log.e("ActivityService", "Error requesting location updates", e)
        }
    }

    private fun stopLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            Log.e("ActivityService", "Error removing location updates", e)
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    inner class ActivityTrackingBinder : Binder() {
        fun getService(): ActivityTrackingService = this@ActivityTrackingService
    }

    @SuppressLint("DefaultLocale")
    private fun Long.toFormattedTime(): String {
        val seconds = (this / 1000) % 60
        val minutes = (this / (1000 * 60)) % 60
        val hours = (this / (1000 * 60 * 60))
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}