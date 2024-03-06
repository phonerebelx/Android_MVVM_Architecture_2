package com.app.adcarchitecture.model.otp

data class OtpResponse(
    val token: String? = null,
    val verify: String? = null,
    val is_forget_password: String? = ""
)