package com.example.composeapptask.appFeatures.sensorActivity.medicineReminder

import com.example.composeapptask.appFeatures.dao.sensorActivity.DayOfWeek
import com.example.composeapptask.appFeatures.dao.sensorActivity.MedicineReminder

data class AddReminderUiState(
    val medicineName: String = "",
    val dosage: String = "",
    val selectedTime: String? = null,
    val selectedDays: MutableList<DayOfWeek> = mutableListOf(),
    val isLoading: Boolean = false,
    val remindersList: List<MedicineReminder> = emptyList(),
    val isAddReminder: Boolean = true
)
