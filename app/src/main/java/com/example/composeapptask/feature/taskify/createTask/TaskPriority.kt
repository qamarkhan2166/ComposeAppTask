package com.example.composeapptask.feature.taskify.createTask

import androidx.annotation.StringRes
import com.example.composeapptask.R

enum class TaskPriority(
    @StringRes val stringResId: Int
) {
    HIGH(R.string.priority_high),
    MEDIUM(R.string.priority_medium),
    LOW(R.string.priority_low);
    companion object {
        fun getTaskPriorityList(): List<TaskPriority> =
            entries
    }
}
