package com.example.evite.data.network

import com.example.evite.data.network.models.EmailRequest
import com.example.evite.data.network.models.EmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ResendApi {
    @POST("emails")
    suspend fun sendEmail(
        @Header("Authorization") apiKey: String,
        @Body request: EmailRequest
    ): Response<EmailResponse>
}
