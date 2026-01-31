package com.example.evite.data.network

import android.util.Log
import com.example.evite.data.network.models.EmailRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object EmailService {
    private const val BASE_URL = "https://api.resend.com/"
    private val API_KEY = "Bearer ${com.example.evite.BuildConfig.RESEND_API_KEY}"

    private val api: ResendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ResendApi::class.java)
    }

    suspend fun sendInviteEmail(
        toEmail: String,
        eventTitle: String,
        eventDate: String,
        eventLocation: String,
        organizerName: String
    ) {
        val htmlContent = """
            <div style="font-family: sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px;">
                <h2 style="color: #4F46E5;">You're Invited!</h2>
                <p>Hi there,</p>
                <p><strong>$organizerName</strong> has invited you to <strong>$eventTitle</strong>.</p>
                
                <div style="background-color: #F9FAFB; padding: 15px; border-radius: 8px; margin: 20px 0;">
                    <p style="margin: 5px 0;">📅 <strong>Date:</strong> $eventDate</p>
                    <p style="margin: 5px 0;">📍 <strong>Location:</strong> $eventLocation</p>
                </div>
                
                <p>We'd love to have you there! Please let us know if you can make it.</p>
                
                <div style="margin-top: 30px; text-align: center;">
                    <a href="mailto:onboarding@resend.dev?subject=Accepted: $eventTitle" 
                       style="background: #4F46E5; color: white; padding: 12px 25px; text-decoration: none; border-radius: 8px; font-weight: bold; margin-right: 10px;">
                       Accept
                    </a>
                    <a href="mailto:onboarding@resend.dev?subject=Declined: $eventTitle" 
                       style="background: #EF4444; color: white; padding: 12px 25px; text-decoration: none; border-radius: 8px; font-weight: bold;">
                       Decline
                    </a>
                </div>
                
                <hr style="border: 0; border-top: 1px solid #eee; margin-top: 40px;" />
                <p style="font-size: 12px; color: #6B7280; text-align: center;">Sent via eVite - Plan your moments with ease.</p>
            </div>
        """.trimIndent()

        try {
            val response = api.sendEmail(
                apiKey = API_KEY,
                request = EmailRequest(
                    to = listOf(toEmail),
                    subject = "Invitation: $eventTitle",
                    html = htmlContent
                )
            )
            
            if (response.isSuccessful) {
                Log.d("EmailService", "Email sent successfully to $toEmail. ID: ${response.body()?.id}")
            } else {
                Log.e("EmailService", "Failed to send email. Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("EmailService", "Error sending email", e)
        }
    }
}
