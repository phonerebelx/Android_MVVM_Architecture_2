package com.example.meezan360.model.otp

data class OtpResponse(
    val token: String? = null,
    val verify: String? = null,
    val is_forget_password: String? = "",
    val user_id: String? = ""
)