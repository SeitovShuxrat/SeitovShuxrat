package com.example.landtech.presentation.ui.login

sealed class LoginScreenEvent {
    data class OnUserChange(val user: String) : LoginScreenEvent()
    data class OnPasswordChange(val password: String) : LoginScreenEvent()
    object OnLoginButtonClick : LoginScreenEvent()
}
