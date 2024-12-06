package com.example.composeapptask

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.composeapptask.feature.dao.CustomNavType
import com.example.composeapptask.feature.dao.ScreenInfo
import com.example.composeapptask.feature.details.MainDetailScreen
import com.example.composeapptask.feature.login.LoginScreen
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
sealed class Routes {
    @Serializable
    data object LoginScreen : Routes()

    @Serializable
    data class AppMainScreen(val screenInfo: ScreenInfo) : Routes()

}

fun NavGraphBuilder.mainGraph(navController: NavController) {

    composable<Routes.LoginScreen> {
        LoginScreen(
            onNavigateForward = {
                navController.navigate(
                    Routes.AppMainScreen(screenInfo = ScreenInfo(route = "AppMainScreen", id = 1))
                )
            }
        )
    }

    composable<Routes.AppMainScreen>(
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
    }
}
