package com.example.proyecto1_plataformasmoviles_domingazo.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

@Composable
fun NavGraph(startDestination: String = "login") {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    var currentUser by remember { mutableStateOf(auth.currentUser) }

    // Escucha cambios de autenticación
    LaunchedEffect(Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            currentUser = user

            scope.launch {
                if (user == null) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // === LOGIN ===
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { userId ->
                    userId?.let { uid ->
                        navController.navigate("home/$uid") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onRegisterClick = { navController.navigate("register") },
                snackbarHostState = snackbarHostState
            )
        }

        // === REGISTRO ===
        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        navController.navigate("home/$userId") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onBackToLogin = { navController.popBackStack() },
                snackbarHostState = snackbarHostState
            )
        }

        // === PANTALLAS PROTEGIDAS ===
        if (currentUser != null) {
            val userId = currentUser!!.uid

            composable(
                "home/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("userId") ?: run {
                    LaunchedEffect(Unit) {
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                    return@composable
                }

                if (uid != userId) {
                    LaunchedEffect(Unit) {
                        navController.navigate("home/$userId") {
                            popUpTo("home/{userId}") { inclusive = true }
                        }
                    }
                    return@composable
                }

                HomeScreen(
                    userId = uid,
                    onItineraryClick = { itineraryId ->
                        navController.navigate("detail/$uid/$itineraryId")
                    },
                    onSettingsClick = { navController.navigate("settings") },
                    onNewItineraryClick = { navController.navigate("create/$uid") }
                )
            }

            composable(
                "create/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("userId") ?: run {
                    LaunchedEffect(Unit) { navController.navigate("login") { popUpTo(0) } }
                    return@composable
                }
                if (uid != userId) return@composable

                ItineraryFormScreen(
                    userId = uid,
                    onSaveSuccess = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                "detail/{userId}/{itineraryId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("itineraryId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("userId") ?: run {
                    LaunchedEffect(Unit) { navController.navigate("login") { popUpTo(0) } }
                    return@composable
                }
                val itineraryId = backStackEntry.arguments?.getString("itineraryId") ?: run {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                    return@composable
                }
                if (uid != userId) return@composable

                ItineraryScreen(
                    itineraryId = itineraryId,
                    userId = uid,
                    navController = navController,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                "edit/{userId}/{itineraryId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("itineraryId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("userId") ?: run {
                    LaunchedEffect(Unit) { navController.navigate("login") { popUpTo(0) } }
                    return@composable
                }
                val itineraryId = backStackEntry.arguments?.getString("itineraryId") ?: run {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                    return@composable
                }
                if (uid != userId) return@composable

                ItineraryFormScreen(
                    userId = uid,
                    itineraryId = itineraryId,
                    onSaveSuccess = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = { auth.signOut() },
                    snackbarHostState = snackbarHostState
                )
            }
        }

        // === REDIRECCIÓN AUTOMÁTICA SI NO HAY USUARIO ===
        else {
            composable(
                route = "{path}",
                arguments = listOf(navArgument("path") { type = NavType.StringType })
            ) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}