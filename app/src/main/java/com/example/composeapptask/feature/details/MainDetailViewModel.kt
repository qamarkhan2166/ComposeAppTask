package com.example.composeapptask.feature.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapptask.api.NetworkResult
import com.example.composeapptask.data.UserPreferences
import com.example.composeapptask.feature.dao.UserMedicationResponse
import com.example.composeapptask.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainDetailViewModel @Inject constructor(
    private val repository: MainRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getUserType()
        getUserMedication()
    }

    private fun getUserType() = viewModelScope.launch {
        _uiState.update { it.copy(emailOrMobile = userPreferences.userEmail.first().orEmpty()) }
    }

    private fun updateLoaderState(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    private fun getUserMedication() {
        updateLoaderState(isLoading = true)
        viewModelScope.launch {
            val networkResponse = repository.getUserMedication()
            updateLoaderState(isLoading = false)

            when (networkResponse) {
                is NetworkResult.Success -> {
                    val response: UserMedicationResponse? = networkResponse.data
                    response?.let {
                        Log.e("response->", response.toString())
                        _uiState.update { it.copy(userMedicationResponse = response) }
                        println(response.toString())
                    } ?: run {
                        Log.e("response->", "Unable to load response")
                    }
                }

                else -> updateError(message = networkResponse.message ?: (networkResponse as NetworkResult.Error).messageValue)
            }
        }
    }

    private fun updateError(message: String) {
        _uiState.update { it.copy(remoteErrorMessage = message) }
    }
}