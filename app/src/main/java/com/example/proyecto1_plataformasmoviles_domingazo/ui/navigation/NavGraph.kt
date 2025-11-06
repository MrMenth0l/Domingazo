package com.example.proyecto1_plataformasmoviles_domingazo.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto1_plataformasmoviles_domingazo.ui.auth.AuthScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.home.HomeScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.ItineraryFormScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.ItineraryScreen

@Composable
fun NavGraph(startDestination: String, userId: String?) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            AuthScreen { navController.navigate("home") { popUpTo("auth") { inclusive = true } } }
        }
        composable("home") {
            HomeScreen(
                userId = userId ?: "",
                onItineraryClick = { navController.navigate("detail/$it") },
                onSettingsClick = { navController.navigate("profile") },
                onNewItineraryClick = { navController.navigate("create") }
            )
        }
        composable("create") {
            ItineraryFormScreen(
                userId = userId ?: "",
                onSaveSuccess = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            "detail/{itineraryId}",
            arguments = listOf(navArgument("itineraryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("itineraryId") ?: ""
            ItineraryScreen(id, userId ?: "", { navController.popBackStack() })
        }
        composable("profile") {
            Text("Perfil (próximamente)", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
        }
    }
}