package com.example.proyecto1_plataformasmoviles_domingazo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.proyecto1_plataformasmoviles_domingazo.ui.home.HomeScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.ItineraryScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.mockConnectedMembers
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.mockItinerary
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.mockProposals
import com.example.proyecto1_plataformasmoviles_domingazo.ui.settings.SettingsScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.newitinerary.NewItineraryScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onItineraryClick = { navController.navigate("itinerary/$it") },
                onSettingsClick = { navController.navigate("settings") },
                onNewItineraryClick = { navController.navigate("newItinerary") }
            )
        }

        composable(
            route = "itinerary/{itineraryId}",
            arguments = listOf(navArgument("itineraryId") { type = NavType.StringType })
        ) {
            val id = it.arguments?.getString("itineraryId") ?: ""
            ItineraryScreen(
                itineraryId = id,
                onBackClick = { navController.popBackStack() }
            )
        }



        composable("newItinerary") {
            NewItineraryScreen(onBackClick = { navController.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
