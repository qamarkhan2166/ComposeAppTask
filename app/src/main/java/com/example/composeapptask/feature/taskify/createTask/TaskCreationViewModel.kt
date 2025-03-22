package com.example.composeapptask.feature.taskify.createTask

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapptask.feature.dao.taskFeature.TaskEntity
import com.example.composeapptask.repository.LocalDatabaseTaskFeatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TaskCreationViewModel @Inject constructor(
    private val localStorageRepo: LocalDatabaseTaskFeatureRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskCreationUiState())
    val uiState: StateFlow<TaskCreationUiState> = _uiState.asStateFlow()

    internal fun savePriority(value: TaskPriority) {
        _uiState.update { it.copy(selectedTaskPriority = value) }
    }

    internal fun onSaveTask() {
        updateLoadingState(true)
        val taskPriority = uiState.value.selectedTaskPriority?.let {
            context.getString(it.stringResId)
        } ?: context.getString(TaskPriority.LOW.stringResId)
        viewModelScope.launch {
            localStorageRepo.insertTask(
                entity = TaskEntity(
                    taskPriority = taskPriority,
                    taskTitle = uiState.value.taskTitle.orEmpty(),
                    taskDescription = uiState.value.taskDescription.orEmpty(),
                    dueDate = uiState.value.selectedDueDate.orEmpty(),
                    isCompleted = false
                )
            )
            resetTextInputToDefaultState()
            delay(timeMillis = 1000L)
            updateLoadingState(false)
        }
    }

    private fun updateLoadingState(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    private fun resetTextInputToDefaultState() {
        _uiState.update {
            it.copy(
                selectedTaskPriority = null,
                selectedDueDate = null,
                taskTitle = null,
                taskDescription = null
            )
        }
    }

    internal fun onValueChange(inputFieldType: InputFieldType, s: String) {
        when(inputFieldType) {
            InputFieldType.Title -> _uiState.update { it.copy(taskTitle = s) }
            InputFieldType.Description -> _uiState.update { it.copy(taskDescription = s) }
            InputFieldType.Date -> _uiState.update { it.copy(selectedDueDate = s) }
        }
    }
}
