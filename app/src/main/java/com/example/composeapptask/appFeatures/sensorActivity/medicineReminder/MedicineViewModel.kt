package com.example.composeapptask.appFeatures.sensorActivity.medicineReminder

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.composeapptask.appFeatures.dao.sensorActivity.DayOfWeek
import com.example.composeapptask.appFeatures.dao.sensorActivity.MedicineReminder
import com.example.composeapptask.appFeatures.sensorActivity.sensorWorkers.ReminderWorker
import com.example.composeapptask.appFeatures.taskify.createTask.InputFieldType
import com.example.composeapptask.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
internal class MedicineViewModel @Inject constructor(
    private val repository: MedicineRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddReminderUiState())
    val uiState = _uiState.asStateFlow()

    private val allReminders = repository.getAllReminders()

    init {
        getAllReminderList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addReminder(reminder: MedicineReminder, onSaved: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.addReminder(reminder)
            scheduleReminder(reminder, onSaved)
        }
    }

    private fun getAllReminderList() {
        viewModelScope.launch {
            val deferredData = async { allReminders.first() }
            val remindersList: List<MedicineReminder> = deferredData.await()
            if (remindersList.isNotEmpty()) {
                _uiState.update { it.copy(remindersList = remindersList) }
            }
        }
    }

    fun onSwapLayout() {
        _uiState.update { it.copy(isAddReminder = !it.isAddReminder) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleReminder(reminder: MedicineReminder, onSaved: () -> Unit) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val localTime = LocalTime.parse(reminder.time, formatter)
        reminder.days.forEach { day ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, day.toCalendarDay())
                set(Calendar.HOUR_OF_DAY, localTime.hour)
                set(Calendar.MINUTE, localTime.minute)
                set(Calendar.SECOND, 0)
            }

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }

            val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

            val data = workDataOf(
                "medicine_name" to reminder.medicineName,
                "dosage" to reminder.dosage
            )

            val request = PeriodicWorkRequestBuilder<ReminderWorker>(
                7, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "reminder_${reminder.id}_$day",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
            _uiState.update { it.copy(isLoading = false) }
            onSaved.invoke()
        }
    }

    internal fun onValueChange(inputFieldType: InputFieldType, s: String) {
        when (inputFieldType) {
            InputFieldType.Date -> _uiState.update { it.copy(selectedTime = s) }
            InputFieldType.Title -> TODO()
            InputFieldType.Description -> TODO()
        }
    }

    fun onValueChangeMedicineName(s: String) {
        _uiState.update { it.copy(medicineName = s) }
    }

    fun onValueChangedDosage(s: String) {
        _uiState.update { it.copy(dosage = s) }
    }

    fun onReminderDayChange(checked: Boolean, dayOfWeek: DayOfWeek) {
        _uiState.update {
            val updatedDays = if (checked) {
                it.selectedDays.toMutableList().apply { add(dayOfWeek) }
            } else {
                it.selectedDays.toMutableList().apply { remove(dayOfWeek) }
            }

            it.copy(selectedDays = updatedDays)
        }
    }

    fun onDeleteReminder(medicineReminder: MedicineReminder) {
        viewModelScope.launch {
            repository.deleteReminderById(id = medicineReminder.id)
            getAllReminderList()
        }
    }

    fun refreshStates() {
        _uiState.update {
            it.copy(
                dosage = "",
                medicineName = "",
                selectedDays = mutableListOf(),
                selectedTime = null
            )
        }
    }

}

private fun DayOfWeek.toCalendarDay(): Int = when (this) {
    DayOfWeek.MONDAY -> Calendar.MONDAY
    DayOfWeek.TUESDAY -> Calendar.TUESDAY
    DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
    DayOfWeek.THURSDAY -> Calendar.THURSDAY
    DayOfWeek.FRIDAY -> Calendar.FRIDAY
    DayOfWeek.SATURDAY -> Calendar.SATURDAY
    DayOfWeek.SUNDAY -> Calendar.SUNDAY
}
