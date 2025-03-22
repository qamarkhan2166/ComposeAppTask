package com.example.composeapptask.feature.taskify.home

import com.example.composeapptask.feature.dao.taskFeature.TaskEntity

internal data class TaskHomeUiState (
    val taskEntity: List<TaskEntity>? = null,
    val selectedSortBy: TaskFilter? = null,
    val selectedFilterBy: TaskStatusFilter? = null,
    val isLoading: Boolean = false
)
