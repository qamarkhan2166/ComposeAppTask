package com.example.composeapptask.appFeatures.sensorActivity.medicineReminder

import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composeapptask.R
import com.example.composeapptask.appFeatures.common.customComposableViews.AppLoaderLayout
import com.example.composeapptask.appFeatures.common.customComposableViews.TimePickerComponent
import com.example.composeapptask.appFeatures.common.customComposableViews.TopBarWithLeftAndRightIcon
import com.example.composeapptask.appFeatures.dao.sensorActivity.DayOfWeek
import com.example.composeapptask.appFeatures.dao.sensorActivity.MedicineReminder
import com.example.composeapptask.appFeatures.taskify.createTask.InputFieldType
import com.example.composeapptask.ui.theme.AppTheme
import com.jodhpurtechies.composelogin.ui.common.customComposableViews.DecoratedTextField

@Composable
fun AddReminderScreen(
    onBack: () -> Unit,
    navController: NavController
) {
    val viewModel: MedicineViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    AddReminderScreenContent(
        onSaveReminder = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                viewModel.addReminder(
                    MedicineReminder(
                        medicineName = uiState.medicineName,
                        dosage = uiState.dosage,
                        time = uiState.selectedTime.orEmpty(),
                        days = uiState.selectedDays
                    ),
                    onSaved = {
                        Toast.makeText(context, "Reminder saved successfully", Toast.LENGTH_LONG)
                            .show()
                        viewModel.refreshStates()
                    }
                )
            }
        },
        onBack = onBack,
        medicineName = uiState.medicineName,
        onValueChangeMedicineName = viewModel::onValueChangeMedicineName,
        dosage = uiState.dosage,
        onValueChangedDosage = viewModel::onValueChangedDosage,
        selectedDueDate = uiState.selectedTime.orEmpty(),
        selectedDays = uiState.selectedDays,
        onValueChange = viewModel::onValueChange,
        isLoading = uiState.isLoading,
        onCheckedChange = viewModel::onReminderDayChange,
        onDeleteClick = viewModel::onDeleteReminder,
        isAddReminder = uiState.isAddReminder,
        onAddButton = viewModel::onSwapLayout,
        remindersList = uiState.remindersList
    )
}

@Composable
private fun AddReminderScreenContent(
    onSaveReminder: () -> Unit = {},
    onBack: () -> Unit = {},
    medicineName: String = "",
    onValueChangeMedicineName: (String) -> Unit = {},

    dosage: String = "",
    onValueChangedDosage: (String) -> Unit = {},

    selectedDueDate: String = "",
    onValueChange: (InputFieldType, String) -> Unit = { _, _ -> },
    selectedDays: MutableList<DayOfWeek> = mutableListOf(DayOfWeek.MONDAY),
    isLoading: Boolean = false,
    onCheckedChange: ((Boolean, DayOfWeek) -> Unit)?,
    onDeleteClick: (MedicineReminder) -> Unit = {},
    onAddButton: () -> Unit = {},
    isAddReminder: Boolean = true,
    remindersList: List<MedicineReminder> = emptyList(),
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
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = onAddButton,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.baseColorScheme.secondary,
                            contentColor = AppTheme.colors.white
                        ),
                        shape = RoundedCornerShape(6.dp),
                        enabled = true,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = if (isAddReminder) "View List" else "Add",
                            style = AppTheme.typography.bodySmall,
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (isAddReminder) {
                    Column(modifier = Modifier.padding(0.dp)) {
                        DecoratedTextField(
                            label = "Medicine Name",
                            modifier = Modifier.fillMaxWidth(),
                            value = medicineName,
                            onValueChange = onValueChangeMedicineName,
                            isError = false,
                            errorText = stringResource(R.string.task_title_error),
                            placeholderText = "please enter medicine Name",
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DecoratedTextField(
                            label = "Dosage",
                            modifier = Modifier.fillMaxWidth(),
                            value = dosage,
                            onValueChange = onValueChangedDosage,
                            isError = false,
                            errorText = stringResource(R.string.task_title_error),
                            placeholderText = "please enter medicine Dosage",
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            TimePickerComponent(
                                selectedTime = selectedDueDate,
                                onTimeSelected = { onValueChange(InputFieldType.Date, it) },
                                placeholderText = "please select time",
                                labelText = "Select Time",
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        DayOfWeek.entries.forEach { day ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedDays.contains(day),
                                    onCheckedChange = { checked ->
                                        if (checked) selectedDays.add(day)
                                        else selectedDays.remove(day)
                                        onCheckedChange?.invoke(checked, day)
                                    }
                                )
                                Text(text = day.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                } else {
                    ReminderList(remindersList = remindersList, onDeleteClick)
                }
            }
            if (isAddReminder) {
                BottomAction(
                    onClickNextButton = {
                        onSaveReminder()
                        onBack()
                    },
                    isContinueEnabled = medicineName.isNotBlank() && dosage.isNotBlank() && selectedDays.isNotEmpty()
                )
            }

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
                    text = "Save Reminder",
                    style = AppTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ReminderList(
    remindersList: List<MedicineReminder>,
    onDeleteClick: (MedicineReminder) -> Unit,
) {
    Column {
        remindersList.forEachIndexed { index, item ->
            ReminderItemContent(
                title = item.medicineName,
                dueDate = item.time,
                onDetailsClick = { onDeleteClick.invoke(item) },
                isCompleted = item.isActive
            )
        }
    }
}

@Composable
private fun ReminderItemContent(
    title: String,
    dueDate: String,
    onDetailsClick: () -> Unit,
    isCompleted: Boolean = false
) {
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
                    text = "${"Date"}: $dueDate",
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
                        text = remember(isCompleted) { if (isCompleted) "Active" else "Disabled" },
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
                        text = "Delete",
                        style = AppTheme.typography.bodySmall.copy(color = AppTheme.colors.white),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddReminderScreenContentPreview() {
    AddReminderScreenContent(
        onCheckedChange = { _, _ -> }
    )
}
