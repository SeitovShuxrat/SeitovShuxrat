package com.example.landtech.domain.utils

import androidx.navigation.NavDirections

sealed class UiEvent {
    object PopBackstack : UiEvent()
    data class Navigate(val route: NavDirections) : UiEvent()
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ) : UiEvent()
}
