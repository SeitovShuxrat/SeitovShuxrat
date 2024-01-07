package com.example.landtech.presentation.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.landtech.data.repository.LandtechRepository
import com.example.landtech.domain.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LandtechRepository) : ViewModel() {

    var user by mutableStateOf("")
        private set

    var userError by mutableStateOf(false)
        private set

    var userErrorMsg by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var passwordError by mutableStateOf(false)
        private set

    var passwordErrorMsg by mutableStateOf("")
        private set

    var server by mutableStateOf("")
        private set

    var serverError by mutableStateOf(false)
        private set

    var serverErrorMsg by mutableStateOf("")
        private set

    var userLoggedIn by mutableStateOf(true)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.OnUserChange -> {
                user = event.user
                userError = false
                userErrorMsg = ""
            }

            is LoginScreenEvent.OnPasswordChange -> {
                password = event.password
                passwordError = false
                passwordErrorMsg = ""
            }
            is LoginScreenEvent.OnServerChange -> {
                server = event.server
                serverError = false
                serverErrorMsg = ""
            }
            is LoginScreenEvent.OnLoginButtonClick -> login()
        }
    }


    private fun login() {
        val errorMsg = ""
        val fieldErrorMsg = "Поле не заполнено!"

        if (user.isBlank()) {
            userError = true
            userErrorMsg = fieldErrorMsg
            return
        }
        if (password.isBlank()) {
            passwordError = true
            passwordErrorMsg = fieldErrorMsg
            return
        }

        if (server.isBlank()) {
            serverError = true
            serverErrorMsg = fieldErrorMsg
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val loginSuccess = repository.userLogin(user, password, server)
            withContext(Dispatchers.Main) {
                if (loginSuccess)
                    _uiEvent.send(UiEvent.Navigate(LoginFragmentDirections.actionLoginFragmentToOrdersFragment()))
                else _uiEvent.send(UiEvent.ShowSnackbar(message = "Ошибка авторизации!"))
            }
        }

    }

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            userLoggedIn = withContext(Dispatchers.IO) {
                repository.checkUserLogin(true)
            }

            if (userLoggedIn) {
                _uiEvent.send(UiEvent.Navigate(LoginFragmentDirections.actionLoginFragmentToOrdersFragment()))
            }
        }
    }
}