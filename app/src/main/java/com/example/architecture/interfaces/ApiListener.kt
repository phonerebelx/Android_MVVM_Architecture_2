package com.example.architecture.interfaces

import androidx.lifecycle.LiveData

interface ApiListener {
    fun onStarted();
    fun onSuccess(liveData: LiveData<String>, tag:String)
    fun onFailure(message:String,tag:String)
    fun onFailureWithResponseCode(code: Int,message:String,tag: String)
    fun showPasswordChangingInstructions(text: String?)
}