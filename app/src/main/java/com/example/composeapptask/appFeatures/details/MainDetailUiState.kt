package com.example.composeapptask.appFeatures.details

import com.example.composeapptask.appFeatures.dao.UserMedicationResponse

data class MainDetailUiState(
    val emailOrMobile: String = "",
    val isLoading: Boolean = false,
    val remoteErrorMessage: String = "",
    val userMedicationResponse: UserMedicationResponse? = null
)
