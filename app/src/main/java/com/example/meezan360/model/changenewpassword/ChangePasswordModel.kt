package com.example.meezan360.model.changenewpassword

data class ChangePasswordModel(
    val login_id: String = "",
    val new_password_confirmation: String = "",
    val new_password: String = "",
    val old_password: String = ""
)