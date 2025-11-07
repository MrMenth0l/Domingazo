package com.example.proyecto1_plataformasmoviles_domingazo.ui.settings

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser ?: run { onBack(); return }
    val userId = user.uid
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Cargando...") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var editButtonPressed by remember { mutableStateOf(false) }
    var logoutButtonPressed by remember { mutableStateOf(false) }
    val editButtonScale by animateFloatAsState(if (editButtonPressed) 0.95f else 1f)
    val logoutButtonScale by animateFloatAsState(if (logoutButtonPressed) 0.95f else 1f)

    fun isValidEmail(e: String) = e.contains("@") && e.contains(".")

    LaunchedEffect(userId) {
        try {
            val doc = db.collection("usuarios").document(userId).get().await()
            if (doc.exists()) {
                name = doc.getString("nombre") ?: user.displayName ?: "Usuario"
                email = doc.getString("email") ?: user.email ?: ""
                bio = doc.getString("bio") ?: ""
            } else {
                db.collection("usuarios").document(userId).set(
                    hashMapOf("nombre" to (user.displayName ?: "Usuario"), "email" to (user.email ?: ""), "bio" to "")
                ).await()
                name = user.displayName ?: "Usuario"
                email = user.email ?: ""
                bio = ""
            }
        } catch (e: Exception) {
            Log.e("Settings", "Error: ${e.message}", e)
            name = user.displayName ?: "Usuario"
            email = user.email ?: ""
            bio = ""
            scope.launch { snackbarHostState.showSnackbar("Error de red") }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario", fontWeight = FontWeight.Bold, color = IndigoPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "", tint = IndigoPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = AquaAccent.copy(0.2f), shadowElevation = 8.dp, modifier = Modifier.size(120.dp)) {
                Icon(Icons.Default.Person, "Avatar", modifier = Modifier.fillMaxSize().padding(24.dp), tint = AquaAccent)
            }

            Spacer(Modifier.height(16.dp))

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isEditing) {
                        OutlinedTextField(name, { name = it; nameError = it.isBlank() }, label = { Text("Nombre") }, isError = nameError,
                            supportingText = { if (nameError) Text("Requerido", color = MaterialTheme.colorScheme.error) },
                            modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary))
                        OutlinedTextField(email, { email = it; emailError = !isValidEmail(it) }, label = { Text("Correo") }, isError = emailError,
                            supportingText = { if (emailError) Text("Correo inválido", color = MaterialTheme.colorScheme.error) },
                            modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary))
                        OutlinedTextField(bio, { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                    } else {
                        Text(name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = IndigoPrimary))
                        Text(email, style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF757575)))
                        if (bio.isNotBlank()) Text(bio, style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF757575)))
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        editButtonPressed = true
                        if (isEditing) {
                            nameError = name.isBlank(); emailError = !isValidEmail(email)
                            if (!nameError && !emailError) {
                                scope.launch {
                                    try {
                                        db.collection("usuarios").document(userId).update(mapOf("nombre" to name, "email" to email, "bio" to bio)).await()
                                        snackbarHostState.showSnackbar("Guardado")
                                        isEditing = false
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Error al guardar")
                                    }
                                }
                            }
                        } else isEditing = true
                        editButtonPressed = false
                    },
                    modifier = Modifier.weight(1f).scale(editButtonScale),
                    colors = ButtonDefaults.buttonColors(AquaAccent)
                ) { Text(if (isEditing) "Guardar" else "Editar") }

                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.weight(1f).scale(logoutButtonScale),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red)
                ) { Text("Logout", color = Color.Red) }
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Cerrar sesión") },
                    text = { Text("¿Estás seguro?") },
                    confirmButton = { TextButton(onClick = { showLogoutDialog = false; onLogout() }) { Text("Sí", color = Color.Red) } },
                    dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("No") } }
                )
            }
        }
    }
}