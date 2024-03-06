package com.app.adcarchitecture.model.otp

data class OtpModel(
    var login_id: String? = null,
    var otp_code: String? = null,
    var device_id: String = "",
    var prefix: String = "",
    var is_forget_password: String = ""
)