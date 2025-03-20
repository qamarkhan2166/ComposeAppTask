package com.example.composeapptask.feature.taskify.taskDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composeapptask.R
import com.example.composeapptask.feature.common.customComposableViews.AppLoaderLayout
import com.example.composeapptask.feature.common.customComposableViews.KeyValueRow
import com.example.composeapptask.feature.common.customComposableViews.TopBarWithLeftAndRightIcon
import com.example.composeapptask.feature.common.customComposableViews.animations.AutoAnimatedCircularProgressBar
import com.example.composeapptask.feature.common.utils.LaunchedEffectOneTime
import com.example.composeapptask.ui.theme.AppTheme

@Composable
internal fun TaskDetailsScreen(
    navController: NavController,
    id: Int
) {
    val viewModel: TaskDetailsViewModel = hiltViewModel()
    val uiState: TaskDetailsUiState by viewModel.uiState.collectAsState()

    LaunchedEffectOneTime(useNonPersistentRemember = true) {
        viewModel.initialSetup(id = id)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TaskDetailsScreenContent(
            onBackAction = { navController.navigateUp() },
            taskDetails = uiState.taskDetails,
            isLoading = uiState.isLoading,
            onDelete = {
                viewModel.onDelete { navController.navigateUp() }
            },
            onMarkCompleted = viewModel::onMarkCompleted
        )
    }
}

@Composable
private fun TaskDetailsScreenContent(
    onBackAction: () -> Unit = {},
    isLoading: Boolean,
    taskDetails: TaskDetailsContractor? = null,
    onMarkCompleted: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var isShowCircularProgress by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        containerColor = AppTheme.colors.white,
        topBar = {
            TopBarWithLeftAndRightIcon(
                title = stringResource(R.string.app_name),
                onNavigationUp = onBackAction,
                rightIcon = null
            )
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start

                ) {
                    Spacer(Modifier.height(12.dp))
                    TaskDetailScreen(
                        title = taskDetails?.title.orEmpty(),
                        description = taskDetails?.description.orEmpty(),
                        priority = taskDetails?.priority.orEmpty(),
                        dueDate = taskDetails?.dueDate.orEmpty(),
                        isCompleted = taskDetails?.isCompleted ?: false
                    )

                }

                BottomAction(
                    onMarkCompleted = {
                        isShowCircularProgress = true
                        onMarkCompleted()
                    },
                    onDelete = onDelete,
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isShowCircularProgress) {
                    AutoAnimatedCircularProgressBar(
                        progressColor = AppTheme.colors.greenColor,
                        backgroundColor = AppTheme.colors.mediumGray,
                        strokeWidth = 12.dp,
                        animationDuration = 2000
                    )
                }

            }

        }

        AppLoaderLayout(showLoader = isLoading, isSemiTransparent = false)
    }
}

@Composable
private fun TaskDetailScreen(
    title: String,
    description: String,
    priority: String,
    dueDate: String,
    isCompleted: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = AppTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.black
        )

        Text(
            text = stringResource(R.string.task_description_label),
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = AppTheme.colors.black
        )

        Text(
            text = description,
            style = AppTheme.typography.bodyMedium,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )

        KeyValueRow(
            label = "${stringResource(R.string.task_priority)}:",
            value = priority
        )

        KeyValueRow(
            label = stringResource(R.string.task_due_date),
            value = dueDate
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.status),
                style = AppTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Icon(
                painter = painterResource(
                    if (isCompleted) R.drawable.completed_icon else R.drawable.baseline_pending_24
                ),
                contentDescription = stringResource(R.string.status),
                tint = if (isCompleted) Color.Unspecified else Color.Blue.copy(alpha = 0.6f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    onCancel: () -> Unit = {},
    cancelActionLabel: String = stringResource(id = R.string.delete_btn),
    isActionDoneEnable: Boolean = true,
    actionDoneLabel: String = stringResource(id = R.string.complete_btn),
    onActionDone: () -> Unit = {},
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 3.dp),
        ) {
            Button(
                onClick = onActionDone,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.baseColorScheme.secondary,
                    contentColor = if (isActionDoneEnable) AppTheme.colors.white else AppTheme.colors.black
                ),
                shape = RoundedCornerShape(6.dp),
                enabled = isActionDoneEnable,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = actionDoneLabel,
                    style = AppTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 3.dp),
        ) {
            OutlinedButton(
                onClick = onCancel,
                border = BorderStroke(1.dp, Color(0xFF2D6A4F)),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    cancelActionLabel,
                    style = AppTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.BottomAction(
    onMarkCompleted: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.2f),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, start = 16.dp, end = 16.dp)) {
            ActionButtonsRow(
                onCancel = onDelete,
                onActionDone = onMarkCompleted
            )
        }
    }
}

@Preview
@Composable
private fun TaskDetailsScreenContentPreview() {
    TaskDetailsScreenContent(
        isLoading = false
    )
}