package com.example.proyecto1_plataformasmoviles_domingazo.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto1_plataformasmoviles_domingazo.ui.home.HomeScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.ItineraryFormScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.ItineraryScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.login.LoginScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.register.RegisterScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.settings.SettingsScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(startDestination: String = "login") {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") },
                snackbarHostState = snackbarHostState
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() },
                snackbarHostState = snackbarHostState
            )
        }

        // RUTAS PROTEGIDAS: Solo si hay usuario
        if (currentUser != null) {
            composable("home") {
                HomeScreen(
                    userId = currentUser.uid,
                    onItineraryClick = { id -> navController.navigate("detail/$id") },
                    onSettingsClick = { navController.navigate("settings") },
                    onNewItineraryClick = { navController.navigate("create") }
                )
            }

            composable("create") {
                ItineraryFormScreen(
                    userId = currentUser.uid,
                    onSaveSuccess = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                "detail/{itineraryId}",
                arguments = listOf(navArgument("itineraryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itineraryId = backStackEntry.arguments?.getString("itineraryId")!!
                ItineraryScreen(
                    itineraryId = itineraryId,
                    userId = currentUser.uid,
                    navController = navController,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                "edit/{itineraryId}",
                arguments = listOf(navArgument("itineraryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itineraryId = backStackEntry.arguments?.getString("itineraryId")!!
                ItineraryFormScreen(
                    userId = currentUser.uid,
                    itineraryId = itineraryId,
                    onSaveSuccess = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}