package com.example.proyecto1_plataformasmoviles_domingazo.ui.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoPrimary
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var loginButtonPressed by remember { mutableStateOf(false) }
    val loginButtonScale by animateFloatAsState(if (loginButtonPressed) 0.95f else 1f)
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    fun isValidEmail(e: String) = e.contains("@") && e.contains(".")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFFF5F7FA), Color.White))),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = IndigoPrimary
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailError = !isValidEmail(it) },
                        label = { Text("Correo") },
                        placeholder = { Text("Ej. ejemplo@correo.com") },
                        leadingIcon = { Icon(Icons.Default.Email, "", tint = IndigoPrimary) },
                        isError = emailError,
                        supportingText = {
                            if (emailError) Text("Correo inválido", color = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = it.isBlank() },
                        label = { Text("Contraseña") },
                        placeholder = { Text("Ingresa tu contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, "", tint = IndigoPrimary) },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = passwordError,
                        supportingText = {
                            if (passwordError) Text("Requerida", color = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        loginButtonPressed = true
                        emailError = !isValidEmail(email)
                        passwordError = password.isBlank()
                        if (!emailError && !passwordError) {
                            scope.launch {
                                try {
                                    auth.signInWithEmailAndPassword(email, password).await()
                                    snackbarHostState.showSnackbar("¡Bienvenido!")
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error: ${e.message}")
                                }
                            }
                        }
                        loginButtonPressed = false
                    },
                    modifier = Modifier.weight(1f).scale(loginButtonScale),
                    colors = ButtonDefaults.buttonColors(AquaAccent)
                ) {
                    Text("Iniciar sesión", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = onRegisterClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = IndigoPrimary),
                    border = BorderStroke(1.dp, IndigoPrimary)
                ) {
                    Text("Registrarse", fontSize = 16.sp)
                }
            }
        }
    }
}