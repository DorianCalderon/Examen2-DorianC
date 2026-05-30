package com.panini.ticketsupport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.panini.ticketsupport.navigation.AppNavGraph
import com.panini.ticketsupport.ui.theme.TicketSupportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicketSupportTheme {
                AppNavGraph()
            }
        }
    }
}
