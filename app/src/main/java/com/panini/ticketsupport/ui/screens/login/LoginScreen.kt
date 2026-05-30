package com.panini.ticketsupport.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.viewmodel.LoginViewModel

/** Entry point composable for the login screen where users authenticate. */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
) {
    val loginState by viewModel.loginState.collectAsState()

    // Navigate away as soon as authentication succeeds.
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success && (loginState as UiState.Success<Boolean>).data) {
            onLoginSuccess()
        }
    }

    when (loginState) {
        is UiState.Loading -> { /* show progress indicator */ }
        is UiState.Error -> { /* show error banner with (loginState as UiState.Error).message */ }
        is UiState.Success -> { /* render username/password fields and login button */ }
    }
}
