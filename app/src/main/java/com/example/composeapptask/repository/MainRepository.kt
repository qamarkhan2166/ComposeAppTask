package com.example.composeapptask.repository

import com.example.composeapptask.api.ApiService
import com.example.composeapptask.api.BaseApiResponse
import com.example.composeapptask.api.NetworkResult
import com.example.composeapptask.feature.dao.UserMedicationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService) : BaseApiResponse() {

    suspend fun getUserMedication(): NetworkResult<UserMedicationResponse> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                apiService.getUserMedication()
            }
        }
    }
}