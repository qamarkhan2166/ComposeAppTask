package com.example.composeapptask.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.composeapptask.feature.taskify.createTask.TaskCreationScreen
import com.example.composeapptask.feature.taskify.home.TaskHomeScreen
import com.example.composeapptask.feature.taskify.setting.SettingScreen
import com.example.composeapptask.feature.taskify.taskDetails.TaskDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class TaskFeatureNavigationRoutes {

    @Serializable
    data object HomeScreenRoute : TaskFeatureNavigationRoutes()

    @Serializable
    data class DetailTaskScreenRoute(val id: Int) : TaskFeatureNavigationRoutes()

    @Serializable
    data object CreateTaskScreenRoute : TaskFeatureNavigationRoutes()

    @Serializable
    data object SettingScreenRoute : TaskFeatureNavigationRoutes()

}

fun NavGraphBuilder.taskFeatureMainNavigation(navController: NavController) {

    composable<TaskFeatureNavigationRoutes.HomeScreenRoute> {
        TaskHomeScreen(
            navController = navController
        )
    }

    composable<TaskFeatureNavigationRoutes.DetailTaskScreenRoute> { backStackEntry ->
        val profile: TaskFeatureNavigationRoutes.DetailTaskScreenRoute = backStackEntry.toRoute()
        TaskDetailsScreen(
            navController = navController,
            id = profile.id
        )
    }

    composable<TaskFeatureNavigationRoutes.CreateTaskScreenRoute> {
        TaskCreationScreen(
            navController = navController
        )
    }

    composable<TaskFeatureNavigationRoutes.SettingScreenRoute> {
        SettingScreen(
            navController = navController
        )
    }

}