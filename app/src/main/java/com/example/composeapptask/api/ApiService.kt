package com.example.composeapptask.api

import com.example.composeapptask.constants.NetworkConstant.API_GET_MEDICATION
import com.example.composeapptask.feature.dao.UserMedicationResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET(API_GET_MEDICATION)
    suspend fun getUserMedication() : Response<UserMedicationResponse>
}