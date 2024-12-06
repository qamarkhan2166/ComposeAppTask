package com.example.composeapptask.feature.login

import com.example.composeapptask.feature.common.state.ErrorState

/**
 * Login State holding ui input values
 */
data class LoginUiState(
    val emailOrMobile: String = "",
    val password: String = "",
    val errorState: LoginErrorState = LoginErrorState(),
)

/**
 * Error state in login holding respective
 * text field validation errors
 */
data class LoginErrorState(
    val emailOrMobileErrorState: ErrorState = ErrorState(),
    val passwordErrorState: ErrorState = ErrorState()
)