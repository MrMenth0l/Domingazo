package com.example.proyecto1_plataformasmoviles_domingazo.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit,
    snackbarHostState: SnackbarHostState  // ← AGREGADO
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    val colorScheme = MaterialTheme.colorScheme
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colorScheme.onSurfaceVariant,
        cursorColor = colorScheme.secondary,
        focusedContainerColor = colorScheme.surface,
        unfocusedContainerColor = colorScheme.surface
    )

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Iniciar Sesión", color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            Spacer(Modifier.height(24.dp))

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
            }

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        error = "Completa todos los campos"
                        return@Button
                    }
                    loading = true
                    scope.launch {
                        try {
                            auth.signInWithEmailAndPassword(email, password).await()
                            val userId = auth.currentUser?.uid ?: return@launch
                            onLoginSuccess(userId)
                        } catch (e: Exception) {
                            error = "Error: ${e.message}"
                            // Mostrar error en snackbar (opcional)
                            snackbarHostState.currentSnackbarData?.dismiss()
                            scope.launch {
                                snackbarHostState.showSnackbar(e.message ?: "Error desconocido")
                            }
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                if (loading) {
                    CircularProgressIndicator(Modifier.size(16.dp), color = colorScheme.onPrimary)
                } else {
                    Text("Iniciar Sesión", color = colorScheme.onPrimary)
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate", color = colorScheme.primary)
            }
        }
    }
}
