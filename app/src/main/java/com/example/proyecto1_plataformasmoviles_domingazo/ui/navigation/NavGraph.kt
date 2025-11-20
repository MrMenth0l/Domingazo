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

        composable(
            "home/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestedUid = backStackEntry.arguments?.getString("userId")
            val activeUid = currentUser?.uid

            if (requestedUid.isNullOrBlank() || activeUid == null) {
                LaunchedEffect(activeUid) {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
                return@composable
            }

            if (requestedUid != activeUid) {
                LaunchedEffect(activeUid) {
                    navController.navigate("home/$activeUid") {
                        popUpTo("home/{userId}") { inclusive = true }
                    }
                }
                return@composable
            }

            HomeScreen(
                userId = activeUid,
                onItineraryClick = { itineraryId ->
                    navController.navigate("detail/$activeUid/$itineraryId")
                },
                onSettingsClick = { navController.navigate("settings") },
                onNewItineraryClick = { navController.navigate("create/$activeUid") }
            )
        }

        composable(
            "create/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestedUid = backStackEntry.arguments?.getString("userId")
            val activeUid = currentUser?.uid

            if (requestedUid.isNullOrBlank() || activeUid == null) {
                LaunchedEffect(activeUid) { navController.navigate("login") { popUpTo(0) } }
                return@composable
            }
            if (requestedUid != activeUid) {
                LaunchedEffect(activeUid) {
                    navController.navigate("home/$activeUid") { popUpTo("home/{userId}") { inclusive = true } }
                }
                return@composable
            }

            ItineraryFormScreen(
                userId = activeUid,
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
            val requestedUid = backStackEntry.arguments?.getString("userId")
            val itineraryId = backStackEntry.arguments?.getString("itineraryId")
            val activeUid = currentUser?.uid

            if (requestedUid.isNullOrBlank() || itineraryId.isNullOrBlank() || activeUid == null) {
                LaunchedEffect(activeUid) { navController.navigate("login") { popUpTo(0) } }
                return@composable
            }
            if (requestedUid != activeUid) {
                LaunchedEffect(activeUid) {
                    navController.navigate("home/$activeUid") { popUpTo("home/{userId}") { inclusive = true } }
                }
                return@composable
            }

            ItineraryScreen(
                itineraryId = itineraryId,
                userId = activeUid,
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
            val requestedUid = backStackEntry.arguments?.getString("userId")
            val itineraryId = backStackEntry.arguments?.getString("itineraryId")
            val activeUid = currentUser?.uid

            if (requestedUid.isNullOrBlank() || itineraryId.isNullOrBlank() || activeUid == null) {
                LaunchedEffect(activeUid) { navController.navigate("login") { popUpTo(0) } }
                return@composable
            }
            if (requestedUid != activeUid) {
                LaunchedEffect(activeUid) {
                    navController.navigate("home/$activeUid") { popUpTo("home/{userId}") { inclusive = true } }
                }
                return@composable
            }

            ItineraryFormScreen(
                userId = activeUid,
                itineraryId = itineraryId,
                onSaveSuccess = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("settings") {
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
                return@composable
            }

            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = { auth.signOut() },
                snackbarHostState = snackbarHostState
            )
        }
    }
}
