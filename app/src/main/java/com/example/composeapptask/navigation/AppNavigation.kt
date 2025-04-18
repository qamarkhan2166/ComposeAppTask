package com.example.composeapptask.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.composeapptask.appFeatures.common.customComposableViews.AllChatScreen
import com.example.composeapptask.appFeatures.common.customComposableViews.ProfileScreen
import com.example.composeapptask.appFeatures.dao.ScreenInfo
import com.example.composeapptask.appFeatures.details.MainDetailScreen
import com.example.composeapptask.appFeatures.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    data object LoginScreen : Routes()

    // Bottom Navigation Screens
    @Serializable data object DashboardScreen : Routes()
    @Serializable data object AllChatScreen : Routes()
    @Serializable data object ProfileScreen : Routes()

    @Serializable
    data class AppMainScreen(val screenInfo: ScreenInfo) : Routes()

}

fun NavGraphBuilder.mainGraph(navController: NavController) {

    composable<Routes.LoginScreen> {
        LoginScreen(
            onNavigateForward = {
                navController.navigate(Routes.DashboardScreen)
            }
        )
    }

    composable<Routes.AllChatScreen> {
        AllChatScreen()
    }

    composable<Routes.ProfileScreen> {
        ProfileScreen()
    }

    composable<Routes.DashboardScreen> {
        MainDetailScreen(
            onNavigateBack = {
                navController.navigate(Routes.LoginScreen)
            }
        )
    }

    // @keep this code for now
    /*composable<Routes.AppMainScreen>(
        typeMap = mapOf(
            typeOf<ScreenInfo>() to CustomNavType(
                ScreenInfo::class.java,
                ScreenInfo.serializer()
            )
        )
    ) { navBackStackEntry ->
        val parameters = navBackStackEntry.toRoute<Routes.AppMainScreen>()
        Log.i("AppMainScreen", parameters.toString())
        MainDetailScreen(
            onNavigateBack = {
                navController.navigate(Routes.LoginScreen)
            }
        )
    }*/
}
