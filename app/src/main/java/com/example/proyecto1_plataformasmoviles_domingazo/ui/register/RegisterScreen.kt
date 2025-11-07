package com.example.proyecto1_plataformasmoviles_domingazo.ui.register

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var registerButtonPressed by remember { mutableStateOf(false) }
    val registerButtonScale by animateFloatAsState(if (registerButtonPressed) 0.95f else 1f)
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun isValidEmail(e: String) = e.contains("@") && e.contains(".")
    fun isValidPassword(p: String) = p.length >= 6

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFFF5F7FA), Color.White))),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp, color = IndigoPrimary), modifier = Modifier.padding(bottom = 32.dp))

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it; nameError = it.isBlank() },
                        label = { Text("Nombre") }, leadingIcon = { Icon(Icons.Default.Person, "", tint = IndigoPrimary) },
                        isError = nameError, supportingText = { if (nameError) Text("Requerido", color = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary)
                    )
                    OutlinedTextField(
                        value = email, onValueChange = { email = it; emailError = !isValidEmail(it) },
                        label = { Text("Correo") }, leadingIcon = { Icon(Icons.Default.Email, "", tint = IndigoPrimary) },
                        isError = emailError, supportingText = { if (emailError) Text("Correo inválido", color = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary)
                    )
                    OutlinedTextField(
                        value = password, onValueChange = { password = it; passwordError = !isValidPassword(it) },
                        label = { Text("Contraseña") }, leadingIcon = { Icon(Icons.Default.Lock, "", tint = IndigoPrimary) },
                        visualTransformation = PasswordVisualTransformation(), isError = passwordError,
                        supportingText = { if (passwordError) Text("Mínimo 6 caracteres", color = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary)
                    )
                }
            }

            Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        registerButtonPressed = true
                        nameError = name.isBlank(); emailError = !isValidEmail(email); passwordError = !isValidPassword(password)
                        if (!nameError && !emailError && !passwordError) {
                            scope.launch {
                                try {
                                    val result = auth.createUserWithEmailAndPassword(email, password).await()
                                    val userId = result.user?.uid ?: return@launch
                                    db.collection("usuarios").document(userId).set(
                                        hashMapOf(
                                            "nombre" to name,
                                            "email" to email,
                                            "bio" to "",
                                            "createdAt" to Timestamp.now()
                                        )
                                    ).await()
                                    snackbarHostState.showSnackbar("¡Cuenta creada!")
                                    onRegisterSuccess()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error: ${e.message}")
                                }
                            }
                        }
                        registerButtonPressed = false
                    },
                    modifier = Modifier.weight(1f).scale(registerButtonScale),
                    colors = ButtonDefaults.buttonColors(AquaAccent)
                ) { Text("Registrarse") }

                OutlinedButton(
                    onClick = onBackToLogin, modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = IndigoPrimary),
                    border = BorderStroke(1.dp, IndigoPrimary)
                ) { Text("Volver") }
            }
        }
    }
}