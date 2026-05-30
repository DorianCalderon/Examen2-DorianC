package com.panini.ticketsupport.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.data.remote.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val VALID_USERNAME = "panini"
private const val VALID_PASSWORD = "2026"
private const val FAKE_TOKEN = "fake-jwt-panini-2026"

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val loginState: StateFlow<UiState<Boolean>> = _loginState.asStateFlow()

    init {
        // Start idle, not loading — login is user-triggered.
        _loginState.value = UiState.Success(false)
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            if (username == VALID_USERNAME && password == VALID_PASSWORD) {
                TokenManager.token = FAKE_TOKEN
                _loginState.value = UiState.Success(true)
            } else {
                _loginState.value = UiState.Error("Invalid credentials. Try panini / 2026.")
            }
        }
    }
}
