package com.example.proyecto1_plataformasmoviles_domingazo.ui.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Diego Quan") }
    var email by remember { mutableStateOf("diego.quan@uvg.edu.gt") }
    var bio by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var editButtonPressed by remember { mutableStateOf(false) }
    var logoutButtonPressed by remember { mutableStateOf(false) }
    val editButtonScale by animateFloatAsState(if (editButtonPressed) 0.95f else 1f)
    val logoutButtonScale by animateFloatAsState(if (logoutButtonPressed) 0.95f else 1f)
    val coroutineScope = rememberCoroutineScope()

    // Validación para el correo
    fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Perfil de Usuario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = IndigoPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver a la pantalla anterior",
                            tint = IndigoPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = IndigoPrimary
                )
            )
        },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Surface(
                shape = CircleShape,
                color = AquaAccent.copy(alpha = 0.2f),
                shadowElevation = 8.dp,
                modifier = Modifier.size(120.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Avatar del usuario",
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    tint = AquaAccent
                )
            }

            // Tarjeta de perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isEditing) {
                        // Campo de nombre
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = it.isBlank()
                            },
                            label = { Text("Nombre") },
                            placeholder = { Text("Ej. Diego Quan") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            isError = nameError,
                            supportingText = {
                                if (nameError) {
                                    Text(
                                        text = "Por favor, ingresa un nombre",
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

                        // Campo de correo
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = !isValidEmail(it)
                            },
                            label = { Text("Correo") },
                            placeholder = { Text("Ej. ejemplo@correo.com") },
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

                        // Campo de bio
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Bio (opcional)") },
                            placeholder = { Text("Ej. Amante de los viajes") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IndigoPrimary,
                                unfocusedBorderColor = Color(0xFF757575),
                                focusedLabelColor = IndigoPrimary,
                                cursorColor = AquaAccent
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                        )
                    } else {
                        // Modo de solo lectura
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = IndigoPrimary
                            )
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = Color(0xFF757575)
                            )
                        )
                        if (bio.isNotBlank()) {
                            Text(
                                text = bio,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = Color(0xFF757575)
                                )
                            )
                        }
                    }
                }
            }

            // Botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        editButtonPressed = true
                        if (isEditing) {
                            nameError = name.isBlank()
                            emailError = !isValidEmail(email)
                            if (!nameError && !emailError) {
                                isEditing = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Cambios guardados")
                                }
                            }
                        } else {
                            isEditing = true
                        }
                        editButtonPressed = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .scale(editButtonScale)
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
                    Text(if (isEditing) "Guardar cambios" else "Editar perfil", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .scale(logoutButtonScale)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Text("Cerrar sesión", fontSize = 16.sp, color = Color.Red)
                }
            }

            // Diálogo de confirmación para cerrar sesión
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = {
                        Text(
                            text = "Cerrar sesión",
                            color = IndigoPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    text = {
                        Text(
                            text = "¿Estás seguro de que quieres cerrar sesión?",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Sesión cerrada")
                                    onLogout()
                                }
                            }
                        ) {
                            Text("Sí", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("No", color = IndigoPrimary)
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}