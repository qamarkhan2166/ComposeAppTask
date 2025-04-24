package com.example.composeapptask.appFeatures.sensorActivity.trackerScreen

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapptask.appFeatures.sensorActivity.services.ActivityTrackingService
import com.example.composeapptask.repository.MedicineRepository
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class ActivityTrackerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: MedicineRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityTrackerUiState())
    val uiState = _uiState.asStateFlow()
    private var lastActivityChangeTime = System.currentTimeMillis()

    @SuppressLint("StaticFieldLeak")
    private var activityTrackingService: ActivityTrackingService? = null
    // We'll get the session ID from the service update
    private var currentSessionId: Long? = null

    private var bound = false
    private var serviceUpdateJob: Job? = null


    /**
     * Defines the service connection callbacks used to bind and communicate with the tracking service.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ActivityTrackingService.ActivityTrackingBinder
            activityTrackingService = binder.getService()
            bound = true
            startCollectingUpdates()
            Log.d("ViewModel", "Service connected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
            activityTrackingService = null
            currentSessionId = null
            Log.d("ViewModel", "Service disconnected")
        }
    }

    /**
     * Broadcast receiver that listens for activity updates and test signals.
     */
    private val activityUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("ViewModelReceiver", "Received broadcast: ${intent.action}")
            when (intent.action) {
                ACTIVITY_UPDATE_ACTION -> {
                    val activityType =
                        intent.getIntExtra(EXTRA_ACTIVITY_TYPE, DetectedActivity.UNKNOWN)
                    val transitionType = intent.getIntExtra(
                        EXTRA_TRANSITION_TYPE,
                        ActivityTransition.ACTIVITY_TRANSITION_ENTER
                    )
                    Log.d("ViewModelReceiver", "Processing: $activityType -> $transitionType")
                    handleActivityTransition(activityType, transitionType)
                }

                TEST_BROADCAST_ACTION -> {
                    Log.d("ViewModelReceiver", "Test broadcast received successfully!")
                }
            }
        }
    }

    init {
        registerReceivers()
        Log.d("ViewModel", "Receivers registered")
        getAllHistorySession()
    }

    /**
     * Registers broadcast receivers for activity updates and internal testing.
     */
    private fun registerReceivers() {
        val filter = IntentFilter().apply {
            addAction(ACTIVITY_UPDATE_ACTION)
            addAction(TEST_BROADCAST_ACTION)
            priority = 999
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(
                    activityUpdateReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.registerReceiver(
                    activityUpdateReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                context.registerReceiver(
                    activityUpdateReceiver,
                    filter
                )
            }
        } catch (e: Exception) {
            Log.e("ViewModel", "Receiver registration failed", e)
        }
    }

    /**
     * Sends a test broadcast to verify that the receiver is functioning as expected.
     */
    private fun sendTestBroadcast() {
        val intent = Intent(TEST_BROADCAST_ACTION).apply {
            `package` = context.packageName
        }
        context.sendBroadcast(intent)
    }

    /**
     * Launches two delayed test broadcasts for debugging or validation purposes.
     */
    fun testBroadcastSystem() {
        viewModelScope.launch {
            delay(1000)
            sendTestBroadcast()
            delay(1000)
            sendTestBroadcast()
        }
    }

    /**
     * Handles detected activity transition and updates the UI state accordingly.
     *
     * @param activityType The type of detected activity from the system.
     * @param transitionType The type of transition (enter or exit).
     */
    private fun handleActivityTransition(activityType: Int, transitionType: Int) {
        val newActivity = when (activityType) {
            DetectedActivity.STILL -> ActivityType.STATIONARY
            DetectedActivity.WALKING -> ActivityType.WALKING
            DetectedActivity.RUNNING -> ActivityType.RUNNING
            else -> ActivityType.UNKNOWN
        }

        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            _uiState.update { state ->
                state.copy(
                    currentActivity = newActivity,
                    activityDuration = 0L,
                )
            }
        } else {
            _uiState.update { state ->
                state.copy(
                    activityDuration = System.currentTimeMillis() - lastActivityChangeTime
                )
            }
        }
    }

    /**
     * Starts the activity tracking service and binds to it for receiving real-time updates.
     * If Android version >= O, starts it as a foreground service.
     * Also updates UI state upon successful binding.
     */
    fun startAndBindService() {
        serviceUpdateJob?.cancel()

        val serviceIntent = Intent(context, ActivityTrackingService::class.java).apply {
            putExtra("ACTION_START_NEW_SESSION", true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        val bindSuccess = context.bindService(
            serviceIntent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        if (bindSuccess) {
            _uiState.update { it.copy(isActivityStarted = true) }
            Log.d("ViewModel", "Service bind requested")
        } else {
            Log.e("ViewModel", "Failed to bind to service")
        }

        Log.d("ViewModel", "Service start requested")
    }

    /**
     * Collects activity updates from the bound service and updates UI state,
     * including session ID, activity type, duration, distance, light, and pressure.
     */
    private fun startCollectingUpdates() {
        serviceUpdateJob = viewModelScope.launch {
            activityTrackingService?.activityUpdates?.collect { updates ->
                Log.e("Update ViewModel", updates.toString())
                currentSessionId = updates.sessionId
                _uiState.update {
                    it.copy(
                        currentActivity = updates.currentActivity,
                        activityDuration = updates.activityDuration,
                        totalDistance = updates.totalDistance,
                        currentLight = updates.currentLight,
                        currentPressure = updates.currentPressure
                    )
                }
            }
        }
    }


    /**
     * Stops activity tracking by cancelling background jobs, unbinding from the service,
     * finalizing the session, resetting UI state, and clearing session data.
     */
    fun stopTracking() {
        viewModelScope.launch {
            try {
                serviceUpdateJob?.cancel()
                serviceUpdateJob = null

                activityTrackingService?.stopTrackingAndFinalizeSession()

                if (bound) {
                    context.unbindService(serviceConnection)
                    bound = false
                    activityTrackingService = null
                }

                _uiState.update {
                    it.copy(
                        isActivityStarted = false,
                        currentActivity = ActivityType.STATIONARY,
                        activityDuration = 0L,
                        totalDistance = 0.0,
                        currentLight = 0f,
                        currentPressure = 0f
                    )
                }

                currentSessionId = null

                Log.d("ViewModel", "Stop tracking requested, session finalized by service")
            } catch (e: Exception) {
                Log.e("ViewModel", "Error stopping tracking", e)
            }
        }
    }

    /**
     * Cleans up resources when the ViewModel is cleared by cancelling jobs,
     * unbinding services, and unregistering broadcast receivers.
     */
    override fun onCleared() {
        super.onCleared()
        serviceUpdateJob?.cancel()

        if (bound) {
            try {
                context.unbindService(serviceConnection)
                Log.d("ViewModel", "Service unbound in onCleared")
            } catch (e: IllegalArgumentException) {
                Log.e("ViewModel", "Service not bound or already unbound in onCleared", e)
            }
            bound = false
        }

        try {
            context.unregisterReceiver(activityUpdateReceiver)
            Log.d("ViewModel", "Receiver unregistered in onCleared")
        } catch (e: IllegalArgumentException) {
            Log.e("ViewModel", "Receiver already unregistered in onCleared", e)
        }

        Log.d("ViewModel", "ViewModel onCleared")
    }

    internal fun getAllHistorySession() {
        viewModelScope.launch {
            val sessionsList = withContext(Dispatchers.IO) {
                repository.getAllHistorySession().first()
            }

            println("===== Session History (${sessionsList.size} items) =====")

            sessionsList.forEachIndexed { index, session ->
                println(
                    """
                -------------------------------
                Session #${index + 1}
                ID           : ${session.sessionId}
                Start Time   : ${session.startTime}
                End Time     : ${session.endTime}
                Distance     : ${session.totalDistance} meters
                -------------------------------
                """.trimIndent()
                )
            }

            println("===== End of Session History =====")
        }
    }

    companion object {
        const val ACTIVITY_UPDATE_ACTION = "com.example.composeapptask.ACTIVITY_UPDATE"
        const val TEST_BROADCAST_ACTION = "com.example.composeapptask.TEST_BROADCAST"
        const val EXTRA_ACTIVITY_TYPE = "activity_type"
        const val EXTRA_TRANSITION_TYPE = "transition_type"
    }

}