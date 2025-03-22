package com.example.composeapptask.feature.taskify.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composeapptask.R
import com.example.composeapptask.feature.common.CustomChipsGroup
import com.example.composeapptask.feature.common.customComposableViews.BouncingFAB
import com.example.composeapptask.feature.common.customComposableViews.TopBarWithLeftAndRightIcon
import com.example.composeapptask.feature.common.customComposableViews.animations.DragDropSwipeColumn
import com.example.composeapptask.feature.common.customComposableViews.animations.NoContentFound
import com.example.composeapptask.feature.common.customComposableViews.animations.ShimmerAnimatedList
import com.example.composeapptask.feature.common.utils.LaunchedEffectOneTime
import com.example.composeapptask.feature.dao.taskFeature.TaskEntity
import com.example.composeapptask.navigation.TaskFeatureNavigationRoutes
import com.example.composeapptask.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
internal fun TaskHomeScreen(
    navController: NavController,
) {
    val viewModel: TaskHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffectOneTime(useNonPersistentRemember = true) {
        viewModel.initialSetup()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.customError)
    ) {
        TaskHomeScreenContent(
            taskEntity = uiState.taskEntity,
            onBackAction = { navController.navigateUp() },
            rightAction = { navController.navigate(TaskFeatureNavigationRoutes.SettingScreenRoute) },
            onDetailsClick = {
                navController.navigate(TaskFeatureNavigationRoutes.DetailTaskScreenRoute(id = it.id))
            },
            fabAction = {
                navController.navigate(TaskFeatureNavigationRoutes.CreateTaskScreenRoute)
            },
            isLoading = uiState.isLoading,
            selectedOptionSortBy = uiState.selectedSortBy,
            onOptionSelectedSortBy = viewModel::onOptionSelectedSortBy,
            selectedOptionFilterBy = uiState.selectedFilterBy,
            onOptionSelectedFilterBy = viewModel::onOptionSelectedFilterBy,
            onSwap = viewModel::onSwapItems,
            onDelete = { task ->
                viewModel.onDeleteItem(task)
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Task Delete: ${task.taskTitle}",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onUndoDelete(task)
                    }
                }
            },
            onComplete = { task ->
                viewModel.onCompleteItem(task)
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Task completed: ${task.taskTitle}",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onUndoCompleteTask(task)
                    }
                }
            },
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
private fun TaskHomeScreenContent(
    taskEntity: List<TaskEntity>? = null,
    onBackAction: () -> Unit = {},
    rightAction: () -> Unit = {},
    fabAction: () -> Unit = {},
    onDetailsClick: (TaskEntity) -> Unit = {},
    isLoading: Boolean,
    onOptionSelectedSortBy: (String) -> Unit,
    selectedOptionSortBy: TaskFilter?,
    onOptionSelectedFilterBy: (String) -> Unit,
    selectedOptionFilterBy: TaskStatusFilter?,
    onSwap: (Int, Int) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    onComplete: (TaskEntity) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        containerColor = AppTheme.colors.mediumGray,
        topBar = {
            TopBarWithLeftAndRightIcon(
                title = stringResource(R.string.app_name),
                onNavigationUp = onBackAction,
                rightIcon = R.drawable.baseline_settings_24,
                rightAction = rightAction
            )
        },
        floatingActionButton = {
            BouncingFAB(onClick = fabAction)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            if (isLoading || taskEntity?.isEmpty() == true) {
                if (isLoading) {
                    ShimmerAnimatedList()
                } else {
                    NoContentFound(text = stringResource(R.string.no_data_found),)
                }
            } else {
                Spacer(Modifier.height(12.dp))
                SortByChipGroup(
                    onOptionSelected = onOptionSelectedSortBy,
                    selectedOption = selectedOptionSortBy
                )
                Spacer(Modifier.height(12.dp))
                FilterChipGroup(
                    onOptionSelected = onOptionSelectedFilterBy,
                    selectedOption = selectedOptionFilterBy
                )
                Spacer(Modifier.height(12.dp))
                TaskListComponent(
                    taskEntity = taskEntity,
                    onDetailsClick = onDetailsClick,
                    onSwap = onSwap,
                    onDelete = onDelete,
                    onComplete = onComplete
                )
            }

        }
    }
}

@Composable
private fun SortByChipGroup(
    onOptionSelected: (String) -> Unit,
    selectedOption: TaskFilter?
) {
    val context = LocalContext.current
    val selectedValue = remember(selectedOption) {
        selectedOption?.let {
            context.getString(it.stringResId)
        }.orEmpty()
    }
    val filters = remember {
        TaskFilter.getTaskSortByList().map { context.getString(it.stringResId) }
    }

    CustomChipsGroup(
        title = stringResource(R.string.sort_by),
        options = filters,
        selectedOption = selectedValue,
        onOptionSelected = onOptionSelected
    )
}

@Composable
fun FilterChipGroup(
    onOptionSelected: (String) -> Unit,
    selectedOption: TaskStatusFilter?
) {
    val context = LocalContext.current
    val filters = remember {
        TaskStatusFilter.getTaskFiltersList().map { context.getString(it.stringResId) }
    }

    val selectedValue = remember(selectedOption) {
        selectedOption?.let {
            context.getString(it.stringResId)
        }.orEmpty()
    }
    CustomChipsGroup(
        title = stringResource(R.string.filter_by_status),
        options = filters,
        selectedOption = selectedValue,
        onOptionSelected = onOptionSelected
    )
}

@Composable
private fun TaskItemContent(
    title: String,
    dueDate: String,
    onDetailsClick: () -> Unit,
    isCompleted: Boolean = false
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(top = 12.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, AppTheme.colors.mediumGray),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.white),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stringResource(R.string.due_date)}: $dueDate",
                    style = AppTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.status)}: ",
                        style = AppTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Text(
                        text = remember(isCompleted) { if (isCompleted) context.getString(R.string.filter_completed) else context.getString(R.string.filter_pending) },
                        style = AppTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isCompleted) AppTheme.colors.baseColorScheme.secondary else AppTheme.colors.grayColor
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDetailsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.customPrimary,
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.details_btn),
                        style = AppTheme.typography.bodySmall.copy(color = AppTheme.colors.white),
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskListComponent(
    taskEntity: List<TaskEntity>? = null,
    onDetailsClick: (TaskEntity) -> Unit,
    onSwap: (Int, Int) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    onComplete: (TaskEntity) -> Unit
) {
    val tasks = remember(taskEntity) { taskEntity?.toMutableStateList() ?: mutableStateListOf() }
    DragDropSwipeColumn(
        items = tasks,
        onSwap = onSwap,
        onDelete = onDelete,
        onComplete = onComplete
    ) { task ->
        TaskItemContent(
            title = task.taskTitle,
            dueDate = task.dueDate,
            onDetailsClick = { onDetailsClick(task) },
            isCompleted = task.isCompleted
        )
    }
}

@Preview
@Composable
private fun TaskHomeScreenContentPreview() {
    TaskHomeScreenContent(
        isLoading = false,
        selectedOptionSortBy = null,
        onOptionSelectedSortBy = {},
        selectedOptionFilterBy = null,
        onOptionSelectedFilterBy = {},
        onSwap = { _, _ -> },
        onDelete = {},
        onComplete = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}