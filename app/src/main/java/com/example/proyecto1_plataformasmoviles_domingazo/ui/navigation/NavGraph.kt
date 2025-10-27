package com.example.proyecto1_plataformasmoviles_domingazo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.proyecto1_plataformasmoviles_domingazo.ui.home.HomeScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.ItineraryScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.login.LoginScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.newitinerary.NewItineraryScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.register.RegisterScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.settings.SettingsScreen
import androidx.compose.material3.SnackbarHostState
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost(navController: NavHostController) {
    // Lista simulada para almacenar usuarios registrados
    val registeredUsers = remember { mutableStateListOf<Triple<String, String, String>>() }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val snackbarHostState = remember { SnackbarHostState() }
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                },
                registeredUsers = registeredUsers
            )
        }

        composable("register") {
            val snackbarHostState = remember { SnackbarHostState() }
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                },
                onUserRegistered = { name, email, password ->
                    // Guardar usuario en la lista simulada
                    registeredUsers.add(Triple(name, email, password))
                },
                snackbarHostState = snackbarHostState
            )
        }

        composable("home") {
            HomeScreen(
                onItineraryClick = { itineraryId ->
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
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                snackbarHostState = snackbarHostState
            )
        }
    }
}