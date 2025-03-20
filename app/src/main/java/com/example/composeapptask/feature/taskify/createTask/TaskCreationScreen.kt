package com.example.composeapptask.feature.taskify.createTask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composeapptask.R
import com.example.composeapptask.feature.common.customComposableViews.AppLoaderLayout
import com.example.composeapptask.feature.common.customComposableViews.HorizontalRadioButtonWithCardSection
import com.example.composeapptask.feature.common.customComposableViews.LabelTextInputField
import com.example.composeapptask.feature.common.customComposableViews.TopBarWithLeftAndRightIcon
import com.example.composeapptask.ui.theme.AppTheme
import com.jodhpurtechies.composelogin.ui.common.customComposableViews.CommonDateOfBirthTextInputComponent
import com.jodhpurtechies.composelogin.ui.common.customComposableViews.DecoratedTextField

@Composable
internal fun TaskCreationScreen(
    navController: NavController,
) {
    val viewModel: TaskCreationViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    TaskCreationScreenContent(
        onBackAction = { navController.navigateUp() },
        isLoading = uiState.isLoading,
        uiState = uiState,
        onSaveTask = viewModel::onSaveTask,
        savePriority = viewModel::savePriority,
        onValueChange = viewModel::onValueChange,
    )
}

@Composable
private fun TaskCreationScreenContent(
    onBackAction: () -> Unit = {},
    onSaveTask: () -> Unit = {},
    onValueChange: (InputFieldType, String) -> Unit = { _, _ -> },
    savePriority: (TaskPriority) -> Unit = {},
    isLoading: Boolean,
    uiState: TaskCreationUiState
) {
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
                Spacer(Modifier.height(12.dp))
                DecoratedTextField(
                    label = stringResource(R.string.task_title),
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.taskTitle.orEmpty(),
                    onValueChange = { onValueChange(InputFieldType.Title, it) },
                    isError = false,
                    errorText = stringResource(R.string.task_title_error),
                    placeholderText = stringResource(R.string.task_title_placeholder)
                )
                Spacer(Modifier.height(12.dp))
                DecoratedTextField(
                    label = stringResource(R.string.task_description),
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.taskDescription.orEmpty(),
                    onValueChange = { onValueChange(InputFieldType.Description, it) },
                    isError = false,
                    errorText = stringResource(R.string.task_title_error),
                    isLargeInputField = true,
                    placeholderText = stringResource(R.string.task_description_placeholder)
                )
                Spacer(Modifier.height(12.dp))
                LabelTextInputField(
                    text = stringResource(R.string.task_priority)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 0.dp)
                ) {
                    HorizontalRadioButtonWithCardSection(
                        items = TaskPriority.getTaskPriorityList(),
                        getSelectedItem = { uiState.selectedTaskPriority },
                        setSelectedItem = savePriority,
                        itemLabel = { item ->
                            LocalContext.current.getString(item.stringResId)
                        },
                        disabledItemsList = emptyList()
                    )

                }

                Spacer(Modifier.height(12.dp))
                CommonDateOfBirthTextInputComponent(
                    getSelectedValue = { uiState.selectedDueDate },
                    setSelectedValue = { onValueChange(InputFieldType.Date, it) },
                    placeholderText = stringResource(R.string.dob_placeholder),
                    labelText = stringResource(R.string.due_date)
                )
            }

            BottomAction(
                onClickNextButton = onSaveTask,
                isContinueEnabled = uiState.isEnableSaveTaskButton
            )
        }
        AppLoaderLayout(showLoader = isLoading, isSemiTransparent = false)
    }
}

@Composable
private fun ColumnScope.BottomAction(
    onClickNextButton: () -> Unit = {},
    isContinueEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.15f),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)) {
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
                    text = stringResource(R.string.save_task_btn),
                    style = AppTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun TaskCreationScreenContentPreview() {
    TaskCreationScreenContent(
        isLoading = false,
        uiState = TaskCreationUiState()
    )
}
