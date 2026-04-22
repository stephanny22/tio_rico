package com.datoban.rich_uncle.visual.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun login() {
        println("LOGIN: ${_email.value} / ${_password.value}")
    }

    fun signUp() {
        println("SIGN UP: ${_username.value} / ${_email.value}")
    }
}