package com.example.composeapptask.appFeatures.sensorActivity.trackerScreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composeapptask.R
import com.example.composeapptask.appFeatures.common.customComposableViews.BouncingFAB
import com.example.composeapptask.appFeatures.common.customComposableViews.TopBarWithLeftAndRightIcon
import com.example.composeapptask.appFeatures.common.utils.LaunchedEffectOneTime
import com.example.composeapptask.navigation.ActivitySensorNavigationRoutes
import com.example.composeapptask.ui.theme.AppTheme

@Composable
fun ActivityTrackerScreen(
    navController: NavController
) {

    val viewModel: ActivityTrackerViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    ActivityRecognitionPermissionRequester {
        viewModel.testBroadcastSystem()
    }

    ActivityTrackerScreenContent(
        currentActivity = uiState.currentActivity,
        activityDuration = uiState.activityDuration,
        onStart = {
            if (!uiState.isActivityStarted) {
                viewModel.startAndBindService()
            } else {
                viewModel.stopTracking()
            }
        },
        onBack = { navController.navigateUp() },
        fabAction = {
            navController.navigate(ActivitySensorNavigationRoutes.AddReminderScreen)
        },
        isActivityStarted = uiState.isActivityStarted
    )
}

@Composable
fun ActivityTrackerScreenContent(
    currentActivity: ActivityType = ActivityType.STATIONARY,
    activityDuration: Long = 0L,
    onBack: () -> Unit = {},
    onStart: () -> Unit = {},
    fabAction: () -> Unit = {},
    isActivityStarted: Boolean = false
) {
    Scaffold(
        containerColor = AppTheme.colors.white,
        topBar = {
            TopBarWithLeftAndRightIcon(
                title = stringResource(R.string.app_name),
                onNavigationUp = onBack,
                rightIcon = null
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.padding(bottom = 65.dp)) {
                BouncingFAB(onClick = fabAction)
            }
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.85f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val resourceID = remember(currentActivity) {
                        when (currentActivity) {
                            ActivityType.WALKING -> R.drawable.walking
                            ActivityType.RUNNING -> R.drawable.running
                            ActivityType.STATIONARY -> R.drawable.standing
                            ActivityType.UNKNOWN -> R.drawable.standing
                        }
                    }

                    Image(
                        painter = painterResource(resourceID),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(end = 8.dp, bottom = 16.dp)
                            .clickable { }
                    )

                    val color = when (currentActivity) {
                        ActivityType.WALKING -> AppTheme.colors.greenColor
                        ActivityType.RUNNING -> AppTheme.colors.baseColorScheme.error
                        ActivityType.STATIONARY -> AppTheme.colors.black
                        ActivityType.UNKNOWN -> AppTheme.colors.black
                    }
                    Text(
                        text = currentActivity.name,
                        style = MaterialTheme.typography.headlineMedium.copy(color = color),
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Duration display
                    val duration = remember(activityDuration) {
                        "Duration: ${activityDuration.toFormattedTime()}"
                    }

                    Text(
                        text = duration,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            BottomAction(
                onClickNextButton = onStart,
                isContinueEnabled = true,
                isActivityStarted = isActivityStarted
            )
        }
    }

}

@Composable
fun ActivityRecognitionPermissionRequester(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current

    var permissionRequested by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        }
    }

    LaunchedEffectOneTime(useNonPersistentRemember = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Only request if on Android 10+
            val permission = Manifest.permission.ACTIVITY_RECOGNITION
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (!permissionRequested) {
                    permissionLauncher.launch(permission)
                    permissionRequested = true
                }
            } else {
                onPermissionGranted()
            }
        } else {
            // Older versions: permission not required
            onPermissionGranted()
        }
    }
}

@Composable
private fun ColumnScope.BottomAction(
    onClickNextButton: () -> Unit = {},
    isContinueEnabled: Boolean,
    isActivityStarted: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.15f),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Button(
                onClick = onClickNextButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.baseColorScheme.secondary,
                    contentColor = if (isContinueEnabled) AppTheme.colors.white else AppTheme.colors.black
                ),
                shape = RoundedCornerShape(6.dp),
                enabled = isContinueEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isActivityStarted) "Stop Activity" else "Start Activity",
                    style = AppTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewActivityTrackerScreen() {
    ActivityTrackerScreenContent()
}

@SuppressLint("DefaultLocale")
fun Long.toFormattedTime(): String {
    val seconds = (this / 1000) % 60
    val minutes = (this / (1000 * 60)) % 60
    val hours = (this / (1000 * 60 * 60))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}