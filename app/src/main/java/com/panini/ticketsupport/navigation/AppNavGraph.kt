package com.panini.ticketsupport.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.panini.ticketsupport.ui.screens.login.LoginScreen
import com.panini.ticketsupport.ui.screens.tickets.CreateTicketScreen
import com.panini.ticketsupport.ui.screens.tickets.TicketDetailScreen
import com.panini.ticketsupport.ui.screens.tickets.TicketListScreen
import com.panini.ticketsupport.viewmodel.CreateTicketViewModel
import com.panini.ticketsupport.viewmodel.LoginViewModel
import com.panini.ticketsupport.viewmodel.TicketDetailViewModel
import com.panini.ticketsupport.viewmodel.TicketListViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.LOGIN,
    ) {

        composable(NavigationRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationRoutes.TICKETS) {
                        // Remove login from the back stack so the user cannot press Back to return.
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                },
                viewModel = viewModel<LoginViewModel>(),
            )
        }

        composable(NavigationRoutes.TICKETS) {
            TicketListScreen(
                onTicketClick = { ticketId ->
                    navController.navigate(NavigationRoutes.ticketDetail(ticketId))
                },
                onCreateClick = {
                    navController.navigate(NavigationRoutes.CREATE_TICKET)
                },
                viewModel = viewModel<TicketListViewModel>(),
            )
        }

        composable(
            route = NavigationRoutes.TICKET_DETAIL,
            arguments = listOf(
                navArgument("ticketId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId").orEmpty()
            TicketDetailScreen(
                ticketId = ticketId,
                onBack = { navController.navigateUp() },
                viewModel = viewModel(factory = TicketDetailViewModel.factory(ticketId)),
            )
        }

        composable(NavigationRoutes.CREATE_TICKET) {
            CreateTicketScreen(
                onTicketSubmitted = { navController.popBackStack() },
                viewModel = viewModel<CreateTicketViewModel>(),
            )
        }
    }
}
