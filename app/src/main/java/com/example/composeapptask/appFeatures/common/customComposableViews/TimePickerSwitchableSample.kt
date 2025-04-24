package com.example.composeapptask.appFeatures.common.customComposableViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapptask.ui.theme.AppTheme
import com.jodhpurtechies.composelogin.ui.common.customComposableViews.DecoratedTextField
import com.jodhpurtechies.composelogin.ui.common.customComposableViews.rememberTextFieldPressInteractionSource
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerM3(
    showDialog: Boolean,
    initialTime: LocalTime = LocalTime.NOON,
    format: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(java.time.format.FormatStyle.SHORT),
    onTimeSelected: (String) -> Unit,
    confirmButtonText: String = "OK",
    cancelButtonText: String = "Cancel",
    confirmButtonColor: Color? = null,
    cancelButtonColor: Color? = null,
    textColor: Color? = null,
    dialogTitle: String = "Select Time",
    is24Hour: Boolean = false,
    onDismissRequest: () -> Unit = {}
) {

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = is24Hour
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(dialogTitle) },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        val formattedTime = format.format(selectedTime)
                        onTimeSelected(formattedTime)
                        onDismissRequest()
                    },
                    colors = confirmButtonColor?.let {
                        ButtonDefaults.buttonColors(containerColor = it, contentColor = AppTheme.colors.whiteColor)
                    } ?: ButtonDefaults.buttonColors()
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissRequest,
                    colors = cancelButtonColor?.let {
                        ButtonDefaults.buttonColors(containerColor = it, contentColor = AppTheme.colors.whiteColor)
                    } ?: ButtonDefaults.buttonColors()
                ) {
                    Text(cancelButtonText)
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerComponent(
    selectedTime: String?,
    onTimeSelected: (String) -> Unit,
    labelText: String,
    placeholderText: String,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    DecoratedTextField(
        label = labelText,
        value = selectedTime.orEmpty(),
        interactionSource = rememberTextFieldPressInteractionSource {
            showPicker = true
        },
        onValueChange = { },
        placeholderText = placeholderText
    )

    TimePickerM3(
        showDialog = showPicker,
        onTimeSelected = { time ->
            onTimeSelected(time)
            showPicker = false
        },
        format = DateTimeFormatter.ofPattern("hh:mm", Locale.US),
        initialTime = LocalTime.of(14, 0),
        confirmButtonText = "Select",
        cancelButtonText = "Close",
        confirmButtonColor = AppTheme.colors.baseColorScheme.primary,
        cancelButtonColor = AppTheme.colors.baseColorScheme.secondary,
        dialogTitle = "Select Time",
        is24Hour = false,
        onDismissRequest = { showPicker = false }
    )
}

@Preview(showBackground = true)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ControlledTimePickerM3Example() {
    var selectedTime by remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selected Time: $selectedTime")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showPicker = true }) {
            Text("Show Time Picker")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TimePickerM3(
            showDialog = showPicker,
            onTimeSelected = { time ->
                selectedTime = time
                showPicker = false
            },
            format = DateTimeFormatter.ofPattern("hh:mm a", Locale.US),
            initialTime = LocalTime.of(14, 0),
            confirmButtonText = "Select",
            cancelButtonText = "Close",
            confirmButtonColor = MaterialTheme.colorScheme.primary,
            cancelButtonColor = MaterialTheme.colorScheme.secondary,
            dialogTitle = "Select Time",
            is24Hour = false,
            onDismissRequest = { showPicker = false }
        )
    }
}