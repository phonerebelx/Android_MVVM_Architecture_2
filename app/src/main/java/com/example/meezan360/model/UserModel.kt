package com.example.meezan360.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("branch_name")
    val branchName: String,
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("designation_id")
    val designationId: String,
    @SerializedName("email_address")
    val emailAddress: String,
    @SerializedName("emp_code")
    val empCode: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("last_login_time")
    val lastLoginTime: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("role_name")
    val roleName: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_type")
    val userType: String
)