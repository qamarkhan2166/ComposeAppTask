package com.example.composeapptask.feature.details

import com.example.composeapptask.feature.dao.UserMedicationResponse

data class MainDetailUiState(
    val emailOrMobile: String = "",
    val isLoading: Boolean = false,
    val remoteErrorMessage: String = "",
    val userMedicationResponse: UserMedicationResponse? = null
)
