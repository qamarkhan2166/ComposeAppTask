package com.example.composeapptask.feature.taskify.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapptask.feature.dao.taskFeature.TaskEntity
import com.example.composeapptask.repository.LocalDatabaseTaskFeatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TaskHomeViewModel @Inject constructor(
    private val localStorageRepo: LocalDatabaseTaskFeatureRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskHomeUiState())
    val uiState: StateFlow<TaskHomeUiState> = _uiState.asStateFlow()

    private var tempTaskList: List<TaskEntity> = emptyList()

    internal fun initialSetup() {
        getTask()
    }

    private fun getTask() {
        updateLoadingState(true)
        viewModelScope.launch {
            val deferredData = async { localStorageRepo.getAllTasks().first() }
            val deferredDelay = async { delay(2000L) }
            deferredDelay.await()

            val taskEntity: List<TaskEntity> = deferredData.await()
            tempTaskList = taskEntity
            _uiState.update { it.copy(taskEntity = taskEntity) }
            updateLoadingState(false)
        }
    }

    internal fun onOptionSelectedSortBy(value: String) {
        val selectedType = TaskFilter.fromString(value)
        _uiState.update {
            it.copy(
                selectedSortBy = selectedType,
                taskEntity = getSortedAndFilteredTasks(
                    tasks = tempTaskList,
                    selectedFilterBy = it.selectedFilterBy,
                    selectedSortType = selectedType
                )
            )
        }
    }

    internal fun onOptionSelectedFilterBy(value: String) {
        val selectedType = TaskStatusFilter.fromString(value)
        _uiState.update {
            it.copy(
                selectedFilterBy = selectedType,
                taskEntity = getSortedAndFilteredTasks(
                    tasks = tempTaskList,
                    selectedSortType = it.selectedSortBy,
                    selectedFilterBy = selectedType
                )
            )
        }
    }

    private fun getSortedAndFilteredTasks(
        tasks: List<TaskEntity>,
        selectedSortType: TaskFilter?,
        selectedFilterBy: TaskStatusFilter?
    ): List<TaskEntity> {
        val filteredTasks = when (selectedFilterBy) {
            TaskStatusFilter.PENDING -> tasks.filter { !it.isCompleted }
            TaskStatusFilter.COMPLETED -> tasks.filter { it.isCompleted }
            else -> tasks
        }

        return when (selectedSortType) {
            TaskFilter.PRIORITY -> filteredTasks.sortedBy { it.taskPriority }
            TaskFilter.DUE_DATE -> filteredTasks.sortedBy { it.dueDate }
            TaskFilter.ALPHABETICALLY -> filteredTasks.sortedBy { it.taskTitle }
            else -> filteredTasks
        }
    }

    private fun updateLoadingState(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    internal fun onSwapItems(from: Int, to: Int) {
        val fromItem = _uiState.value.taskEntity?.get(from)
        val toItem = _uiState.value.taskEntity?.get(to)
        val newList = _uiState.value.taskEntity?.toMutableList()

        if (toItem != null && fromItem != null) {
            newList?.set(from, toItem)
            newList?.set(to, fromItem)
        }
        _uiState.update { it.copy(taskEntity = newList) }
    }


    internal fun onDeleteItem(taskEntity: TaskEntity) {
        val nonNullTaskId = taskEntity.id
        viewModelScope.launch {
            localStorageRepo.deleteTaskById(nonNullTaskId)
            _uiState.update { currentState ->
                val updatedList = currentState.taskEntity?.filter { it != taskEntity }
                currentState.copy(taskEntity = updatedList)
            }
        }
    }

    internal fun onCompleteItem(value: TaskEntity) {
        viewModelScope.launch {
            updateTask(value.copy(isCompleted = true))

            _uiState.update { currentState ->
                val updatedList = currentState.taskEntity?.map { task ->
                    if (task == value) task.copy(isCompleted = true) else task
                }
                currentState.copy(taskEntity = updatedList)
            }
        }
    }

    private suspend fun updateTask(updatedTask: TaskEntity) {
        try {
            localStorageRepo.updateTask(updatedTask)
        } catch (e: Exception) {
            Log.e("TaskError", "Failed to update task", e)
        }
    }

    internal fun onUndoDelete(task: TaskEntity) {
        viewModelScope.launch {
            localStorageRepo.insertTask(task)
            _uiState.update { currentState ->
                val updatedList = currentState.taskEntity?.toMutableList()?.apply {
                    add(task)
                }
                currentState.copy(taskEntity = updatedList)
            }
        }
    }

    internal fun onUndoCompleteTask(task: TaskEntity) {
        viewModelScope.launch {
            val originalTask = task.copy(isCompleted = false)
            updateTask(originalTask)
            _uiState.update { currentState ->
                val updatedList = currentState.taskEntity?.map { t ->
                    if (t == task) originalTask else t
                }
                currentState.copy(taskEntity = updatedList)
            }
        }
    }

}
