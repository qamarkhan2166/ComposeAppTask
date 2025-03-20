package com.example.composeapptask.feature.taskify.home

import androidx.annotation.StringRes
import com.example.composeapptask.R

enum class TaskFilter(
    @StringRes val stringResId: Int
) {
    PRIORITY(R.string.filter_priority),
    DUE_DATE(R.string.filter_due_date),
    ALPHABETICALLY(R.string.filter_alphabetically);

    companion object {
        fun getTaskSortByList(): List<TaskFilter> =
            TaskFilter.entries

        fun fromString(value: String): TaskFilter? {
            val normalizedInput = value.replace(" ", "_").uppercase()
            return entries.find { it.name.equals(normalizedInput, ignoreCase = true) }
        }
    }

}

enum class TaskStatusFilter(
    @StringRes val stringResId: Int
) {
    ALL(R.string.filter_all),
    PENDING(R.string.filter_pending),
    COMPLETED(R.string.filter_completed);

    companion object {
        fun getTaskFiltersList(): List<TaskStatusFilter> =
            TaskStatusFilter.entries

        fun fromString(value: String): TaskStatusFilter? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
