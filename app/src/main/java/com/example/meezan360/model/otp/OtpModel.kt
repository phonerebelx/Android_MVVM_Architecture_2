package com.example.meezan360.model.otp

data class OtpModel(
    var login_id: String? = null,
    var otp_code: String? = null,
    var device_id: String = "",
    var is_forget_password: String = "",
    var prefix: String = "360"
)