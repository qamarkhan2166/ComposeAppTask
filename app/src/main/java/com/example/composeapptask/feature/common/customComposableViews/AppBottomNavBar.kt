package com.example.composeapptask.feature.common.customComposableViews

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.composeapptask.R
import com.example.composeapptask.Routes
import com.example.composeapptask.ui.theme.AppTheme
import kotlinx.serialization.Serializable

data class NavigationBarColors(
    val containerColor: Color,
    val selectedIconColor: Color,
    val unselectedIconColor: Color,
    val selectedTextColor: Color,
    val unselectedTextColor: Color,
)

@Composable
fun navigationBarColors(): NavigationBarColors {
    return NavigationBarColors(
        containerColor = AppTheme.colors.baseColorScheme.secondaryContainer,
        selectedIconColor = AppTheme.colors.accentPurple,
        unselectedIconColor = AppTheme.colors.whiteColor.copy(alpha = 0.5f),
        selectedTextColor = AppTheme.colors.accentPurple,
        unselectedTextColor = AppTheme.colors.whiteColor.copy(alpha = 0.5f),
    )
}


@Serializable
sealed class BottomScreens<T>(
    @StringRes val titleResourceValue: Int,
    @DrawableRes val iconResourceValue: Int,
    val route: T
) {
    @Serializable
    data object Dashboard : BottomScreens<Routes.DashboardScreen>(
        titleResourceValue = R.string.discover_tab_title,
        iconResourceValue = R.drawable.baseline_home_24,
        route = Routes.DashboardScreen)

    @Serializable
    data object AllChats : BottomScreens<Routes.AllChatScreen>(
        titleResourceValue = R.string.all_chats_tab_title,
        iconResourceValue = R.drawable.baseline_chat_24,
        route = Routes.AllChatScreen
    )

    @Serializable
    data object Profile : BottomScreens<Routes.ProfileScreen>(
        titleResourceValue = R.string.profile_tab_title,
        iconResourceValue = R.drawable.baseline_account_circle_24,
        route = Routes.ProfileScreen
    )

}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    colors: NavigationBarColors = navigationBarColors(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    val bottomNavItems = remember {
        listOf(
            BottomScreens.Dashboard,
            BottomScreens.AllChats,
            BottomScreens.Profile,
        )
    }

    val isBottomBarVisible = remember(currentRoute) {
        currentRoute.isNotBlank() && bottomNavItems.any { it.route::class.qualifiedName == currentRoute }
    }

    if (!isBottomBarVisible) return

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = colors.containerColor,
        contentColor = Color.Unspecified
    ) {
        bottomNavItems.forEach { screen ->
            val isSelected = remember(currentRoute) {
                currentRoute == screen.route::class.qualifiedName
            }

            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = colors.selectedIconColor,
                    unselectedIconColor = colors.unselectedIconColor,
                    selectedTextColor = colors.selectedTextColor,
                    unselectedTextColor = colors.unselectedTextColor,
                ),
                icon = {
                    Icon(
                        painter = painterResource(screen.iconResourceValue),
                        contentDescription = screen.titleResourceValue.toString(),
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) colors.selectedIconColor else colors.unselectedIconColor,
                    )
                },
                label = {
                    Text(
                        text = stringResource(screen.titleResourceValue),
                        style = AppTheme.typography.bodySmall.copy(
                            color = if (isSelected) colors.selectedTextColor else colors.unselectedTextColor,
                        ),
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
