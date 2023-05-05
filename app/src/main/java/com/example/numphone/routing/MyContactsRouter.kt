package com.example.mynotes.routing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


sealed class Screen {
    object Contacts: Screen()
    object SaveContact: Screen()
    object ContactInfo: Screen()
}

object MyContactsRouter {
    var currentScreen: Screen by mutableStateOf(Screen.Contacts)

    fun navigateTo(destination: Screen) {
        currentScreen = destination
    }
}