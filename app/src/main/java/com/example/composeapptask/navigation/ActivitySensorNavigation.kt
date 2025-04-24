package com.example.composeapptask.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.composeapptask.appFeatures.sensorActivity.medicineReminder.AddReminderScreen
import com.example.composeapptask.appFeatures.sensorActivity.trackerScreen.ActivityTrackerScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class ActivitySensorNavigationRoutes {

    @Serializable
    data object ActivityTractorScreen : ActivitySensorNavigationRoutes()

    @Serializable
    data object AddReminderScreen : ActivitySensorNavigationRoutes()

}

fun NavGraphBuilder.activitySensorMainNavigation(navController: NavController) {

    composable<ActivitySensorNavigationRoutes.ActivityTractorScreen> {
        ActivityTrackerScreen(
            navController = navController
        )
    }

    composable<ActivitySensorNavigationRoutes.AddReminderScreen> {
        AddReminderScreen(
            navController = navController,
            onBack = {
                navController.navigateUp()
            }
        )
    }

}