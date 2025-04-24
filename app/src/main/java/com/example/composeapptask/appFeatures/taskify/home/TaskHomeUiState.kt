package com.example.composeapptask.appFeatures.taskify.home

import com.example.composeapptask.appFeatures.dao.taskFeature.TaskEntity

internal data class TaskHomeUiState (
    val taskEntity: List<TaskEntity>? = null,
    val selectedSortBy: TaskFilter? = null,
    val selectedFilterBy: TaskStatusFilter? = null,
    val isLoading: Boolean = false
)
