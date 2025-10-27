package com.example.proyecto1_plataformasmoviles_domingazo.ui.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    registeredUsers: List<Triple<String, String, String>>, // Lista de usuarios registrados
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var loginButtonPressed by remember { mutableStateOf(false) }
    val loginButtonScale by animateFloatAsState(if (loginButtonPressed) 0.95f else 1f)
    val coroutineScope = rememberCoroutineScope()

    // Validación para el correo
    fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF5F7FA), Color.White)
                    )
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = IndigoPrimary
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Tarjeta de formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Campo de correo
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = !isValidEmail(it)
                        },
                        label = { Text("Correo") },
                        placeholder = { Text("Ej. ejemplo@correo.com") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Ícono de correo",
                                tint = IndigoPrimary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        isError = emailError,
                        supportingText = {
                            if (emailError) {
                                Text(
                                    text = "Ingresa un correo válido",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = Color(0xFF757575),
                            focusedLabelColor = IndigoPrimary,
                            cursorColor = AquaAccent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                    )

                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = it.isBlank()
                        },
                        label = { Text("Contraseña") },
                        placeholder = { Text("Ingresa tu contraseña") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Ícono de contraseña",
                                tint = IndigoPrimary
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        isError = passwordError,
                        supportingText = {
                            if (passwordError) {
                                Text(
                                    text = "La contraseña no puede estar vacía",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = Color(0xFF757575),
                            focusedLabelColor = IndigoPrimary,
                            cursorColor = AquaAccent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                    )
                }
            }

            // Botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        loginButtonPressed = true
                        emailError = !isValidEmail(email)
                        passwordError = password.isBlank()
                        if (!emailError && !passwordError) {
                            // Verificar si el usuario existe en la lista de registrados
                            val userExists = registeredUsers.any { it.second == email && it.third == password }
                            if (userExists || (email == "diego.quan@uvg.edu.gt" && password == "123456")) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Inicio de sesión exitoso")
                                    onLoginSuccess()
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Credenciales incorrectas")
                                }
                            }
                        }
                        loginButtonPressed = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .scale(loginButtonScale)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AquaAccent,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text("Iniciar sesión", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = IndigoPrimary
                    ),
                    border = BorderStroke(1.dp, IndigoPrimary)
                ) {
                    Text("Registrarse", fontSize = 16.sp)
                }
            }
        }
    }
}