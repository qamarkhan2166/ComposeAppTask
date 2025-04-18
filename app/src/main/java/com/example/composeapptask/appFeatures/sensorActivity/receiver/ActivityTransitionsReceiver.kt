package com.example.composeapptask.appFeatures.sensorActivity.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.composeapptask.appFeatures.sensorActivity.services.ActivityTrackingService
import com.example.composeapptask.appFeatures.sensorActivity.trackerScreen.ActivityTrackerViewModel
import com.google.android.gms.location.ActivityTransitionResult
import javax.inject.Inject

class ActivityTransitionsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var activityTrackingService: ActivityTrackingService

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ActivityReceiver", "Received system intent: ${intent.action}")

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            Log.d("ActivityReceiver", "Transition result: $result")

            result?.transitionEvents?.forEach { event ->
                if (::activityTrackingService.isInitialized) {
                    activityTrackingService.handleActivityTransition(event.activityType, event.transitionType)

                    Log.d("ActivityReceiver", "Processing: ${event.activityType} -> ${event.transitionType}")

                    val vmIntent = Intent(ActivityTrackerViewModel.ACTIVITY_UPDATE_ACTION).apply {
                        putExtra(ActivityTrackerViewModel.EXTRA_ACTIVITY_TYPE, event.activityType)
                        putExtra(ActivityTrackerViewModel.EXTRA_TRANSITION_TYPE, event.transitionType)
                        `package` = context.packageName
                    }
                    context.sendBroadcast(vmIntent)
                }
            }
        }
    }
}
