package com.example.landtech.presentation.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.landtech.R
import com.example.landtech.domain.utils.UiEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigate: (UiEvent.Navigate) -> Unit, viewModel: LoginViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showPassword by remember { mutableStateOf(false) }
    viewModel.checkUserLoggedIn()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }

                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message, actionLabel = event.action
                    )
                }

                else -> Unit
            }
        }
    }

    if (!viewModel.userLoggedIn) {
        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = viewModel.userError,
                    supportingText = {
                        Text(text = viewModel.userErrorMsg)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    value = viewModel.user,
                    label = { Text(text = "Пользователь") },
                    onValueChange = {
                        viewModel.onEvent(LoginScreenEvent.OnUserChange(it))
                    })

                Spacer(modifier = Modifier.height(8.dp))

                TextField(modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Password
                    ),

                    isError = viewModel.passwordError,
                    supportingText = {
                        Text(text = viewModel.passwordErrorMsg)
                    },
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.key),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        val (description, imageRes) = if (showPassword)
                            "Hide password" to R.drawable.visibility_off
                        else "Show password" to R.drawable.visibility

                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(painter = painterResource(id = imageRes), description)
                        }
                    },
                    value = viewModel.password,
                    label = { Text(text = "Пароль") },
                    onValueChange = {
                        viewModel.onEvent(LoginScreenEvent.OnPasswordChange(it))
                    })

                Spacer(modifier = Modifier.height(8.dp))

                TextField(modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.onEvent(LoginScreenEvent.OnLoginButtonClick)
                        defaultKeyboardAction(ImeAction.Done)
                    }),
                    isError = viewModel.serverError,
                    supportingText = {
                        Text(text = viewModel.serverErrorMsg)
                    },
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.cloud),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    value = viewModel.server,
                    label = { Text(text = "Сервер") },
                    onValueChange = {
                        viewModel.onEvent(LoginScreenEvent.OnServerChange(it))
                    })

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(1.dp))
                    Button(onClick = { viewModel.onEvent(LoginScreenEvent.OnLoginButtonClick) }) {
                        Text(text = "Вход")
                    }
                }
            }
        }
    }
}