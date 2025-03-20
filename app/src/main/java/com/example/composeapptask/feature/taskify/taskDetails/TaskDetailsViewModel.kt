package com.example.composeapptask.feature.taskify.taskDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapptask.feature.dao.taskFeature.TaskEntity
import com.example.composeapptask.repository.LocalDatabaseTaskFeatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TaskDetailsViewModel @Inject constructor(
    private val localStorageRepo: LocalDatabaseTaskFeatureRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailsUiState())
    val uiState = _uiState.asStateFlow()

    internal fun initialSetup(id: Int) {
        getDetailsByID(id = id)
    }

    private fun getDetailsByID(id: Int) {
        updateLoadingState(true)
        viewModelScope.launch {
            val deferredTask = async { localStorageRepo.getTaskById(id).first() }
            val deferredDelay = async { delay(2000L) }

            deferredDelay.await()
            val task = deferredTask.await()

            _uiState.update { it.copy(taskDetails = task?.toTaskDetailsContractor(isCompleted = null)) }
            updateLoadingState(false)
        }

    }


    fun onDelete(onDeletedSuccessfully: () -> Unit = {}) {
        val nonNullTaskId = uiState.value.taskDetails?.taskID ?: return
        viewModelScope.launch {
            localStorageRepo.deleteTaskById(nonNullTaskId)
            onDeletedSuccessfully()
        }
    }

    fun onMarkCompleted() {
        val nonNullTaskId = uiState.value.taskDetails?.taskID ?: return
        viewModelScope.launch {
            val taskEntity = getTaskByIdAndMarkCompleted(nonNullTaskId)
            taskEntity?.let { task ->
                updateTask(task.copy(isCompleted = true))
                _uiState.update { it.copy(taskDetails = task.toTaskDetailsContractor(isCompleted = true)) }
            }
        }
    }

    private suspend fun getTaskByIdAndMarkCompleted(taskId: Int): TaskEntity? {
        return localStorageRepo.getTaskById(taskId).firstOrNull()
    }

    private suspend fun updateTask(updatedTask: TaskEntity) {
        try {
            localStorageRepo.updateTask(updatedTask)
        } catch (e: Exception) {
            Log.e("TaskError", "Failed to update task", e)
        }
    }

    private fun updateLoadingState(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }
}