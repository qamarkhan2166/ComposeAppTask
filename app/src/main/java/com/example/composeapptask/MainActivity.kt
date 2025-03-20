package com.example.composeapptask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.composeapptask.feature.common.customComposableViews.BottomNavigationBar
import com.example.composeapptask.feature.common.utils.LaunchedEffectOneTime
import com.example.composeapptask.feature.common.utils.REQUIRE_APP_BOTTOM_NAV
import com.example.composeapptask.navigation.TaskFeatureNavigationRoutes
import com.example.composeapptask.navigation.taskFeatureMainNavigation
import com.example.composeapptask.ui.theme.CustomAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val primaryColor by mainViewModel.primaryColor.collectAsState()
            LaunchedEffectOneTime(useNonPersistentRemember = true) {
                mainViewModel.initialSetup()
            }
            CustomAppTheme(primaryColor = primaryColor, content = {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        if (REQUIRE_APP_BOTTOM_NAV) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = TaskFeatureNavigationRoutes.HomeScreenRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        taskFeatureMainNavigation(navController)
                    }
                }
            })
        }
    }
}
