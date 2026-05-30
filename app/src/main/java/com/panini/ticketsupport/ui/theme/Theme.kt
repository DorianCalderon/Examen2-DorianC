package com.panini.ticketsupport.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PaniniColorScheme = lightColorScheme()

/** Material3 theme applied at the root of the app via MainActivity. */
@Composable
fun TicketSupportTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PaniniColorScheme,
        content = content,
    )
}
