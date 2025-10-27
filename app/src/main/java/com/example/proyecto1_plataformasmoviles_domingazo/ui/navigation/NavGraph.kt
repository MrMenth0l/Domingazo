package com.example.proyecto1_plataformasmoviles_domingazo.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.proyecto1_plataformasmoviles_domingazo.ui.home.HomeScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.ItineraryScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.newitinerary.NewItineraryScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.settings.SettingsScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onItineraryClick = { itineraryId ->
                    // Codificar el itineraryId para manejar caracteres especiales
                    val encodedId = URLEncoder.encode(itineraryId, StandardCharsets.UTF_8.toString())
                    navController.navigate("itinerary/$encodedId")
                },
                onSettingsClick = { navController.navigate("settings") },
                onNewItineraryClick = { navController.navigate("newItinerary") }
            )
        }

        composable(
            route = "itinerary/{itineraryId}",
            arguments = listOf(navArgument("itineraryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itineraryId = backStackEntry.arguments?.getString("itineraryId") ?: ""
            ItineraryScreen(
                itineraryId = itineraryId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("newItinerary") {
            NewItineraryScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { destino, fecha ->
                    navController.popBackStack()
                }
            )
        }

        composable("settings") {
            val snackbarHostState = remember { SnackbarHostState() }
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    // Lógica de cierre de sesión, por ejemplo, navegar a login
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                snackbarHostState = snackbarHostState
            )
        }
    }
}