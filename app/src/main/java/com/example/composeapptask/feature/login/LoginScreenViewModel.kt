package com.example.composeapptask.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapptask.data.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginScreenViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(s: String) {
        _uiState.update { it.copy(emailOrMobile = s) }
    }

    fun onPasswordChange(s: String) {
        _uiState.update { it.copy(password = s) }
    }

    private suspend fun saveUserType(userType: String) = userPreferences.saveUserEmail(userType)

    fun onClickLogin(onProceedNext:() -> Unit) {
        viewModelScope.launch {
            saveUserType(userType = uiState.value.emailOrMobile)
            onProceedNext.invoke()
        }
    }

}