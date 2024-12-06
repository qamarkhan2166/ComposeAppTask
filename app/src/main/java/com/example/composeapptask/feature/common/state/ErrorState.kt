package com.example.composeapptask.feature.common.state

import androidx.annotation.StringRes
import com.example.composeapptask.R

/**
 * Error state holding values for error ui
 */
data class ErrorState(
    val hasError: Boolean = false,
    @StringRes val errorMessageStringResource: Int = R.string.empty_string
)