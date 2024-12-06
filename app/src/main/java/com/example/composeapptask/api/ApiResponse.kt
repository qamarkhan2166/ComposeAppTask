package com.example.composeapptask.api

import android.util.Log
import org.json.JSONObject
import retrofit2.Response

sealed class NetworkResult<T>(
    val data: T? = null,
    val code: Int? = null,
    val message: String? = null
) {
    class Success<T> internal constructor(
        val dataModel: T
    ) : NetworkResult<T>(dataModel)

    class Error<T> internal constructor(
        val codeValue: Int,
        val messageValue: String,
        data: T? = null
    ) : NetworkResult<T>(data = data, code = codeValue, message = messageValue)
}

abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return safeApiCall(apiCall) { it }
    }
    suspend fun <T, R> safeApiCall(
        apiCall: suspend () -> Response<T>,
        mapper: suspend (T) -> R
    ): NetworkResult<R> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    Log.e("BaseApiResponse", "${response.code()} , ${response.message()}")
                    return NetworkResult.Success(mapper.invoke(body))
                }
            }
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            return NetworkResult.Error(response.code(), jsonObj.toString())
        } catch (e: Exception) {
            Log.e("safeApiCall", "Error")
            e.printStackTrace()
            return error(409, e.message ?: e.toString())
        }
    }
    private fun <T> error(code: Int, errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(code, "Api call failed $errorMessage")
}
