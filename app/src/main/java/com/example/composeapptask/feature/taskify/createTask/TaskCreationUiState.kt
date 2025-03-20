package com.example.composeapptask.feature.taskify.createTask

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

data class TaskCreationUiState (
    val onTaskPrioritySelected: (TaskPriority) -> Unit = {},
    val selectedTaskPriority: TaskPriority? = null,
    val selectedDueDate: String? = null,
    val taskTitle: String? = null,
    val taskDescription: String? = null,
    val isLoading: Boolean = false,
) {
    val isEnableSaveTaskButton by derivedStateOf {
        !taskTitle.isNullOrEmpty() &&
                !taskDescription.isNullOrEmpty() &&
                selectedTaskPriority != null &&
                !selectedDueDate.isNullOrEmpty()
    }
}

enum class InputFieldType {
    Title,
    Description,
    Date;
}