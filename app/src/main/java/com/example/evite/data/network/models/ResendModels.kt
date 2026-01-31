package com.example.evite.data.network.models

import com.google.gson.annotations.SerializedName

data class EmailRequest(
    val from: String = "eVite <onboarding@resend.dev>",
    val to: List<String>,
    val subject: String,
    val html: String
)

data class EmailResponse(
    val id: String
)
