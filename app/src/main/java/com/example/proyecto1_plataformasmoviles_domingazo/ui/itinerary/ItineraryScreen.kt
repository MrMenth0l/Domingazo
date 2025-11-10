// Archivo: ui/itinerary/ItineraryScreen.kt
package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    itineraryId: String,
    userId: String,
    navController: NavController,
    onBackClick: () -> Unit
) {
    var itinerary by remember { mutableStateOf<Itinerary?>(null) }
    var activities by remember { mutableStateOf<List<Activity>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showActivityDialog by remember { mutableStateOf(false) }
    var editingActivityId by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

    // Cargar itinerario
    LaunchedEffect(itineraryId) {
        try {
            val doc = db.collection("usuarios")
                .document(userId)
                .collection("itinerarios")
                .document(itineraryId)
                .get()
                .await()
            if (doc.exists()) {
                itinerary = doc.toObject(Itinerary::class.java)?.copy(id = doc.id)
            } else {
                error = "Itinerario no encontrado"
            }
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            loading = false
        }
    }

    // Cargar actividades en tiempo real
    LaunchedEffect(itineraryId) {
        db.collection("usuarios").document(userId)
            .collection("itinerarios").document(itineraryId)
            .collection("actividades")
            .orderBy("createdAt")
            .addSnapshotListener { snap, _ ->
                activities = snap?.documents?.mapNotNull {
                    it.toObject(Activity::class.java)?.copy(id = it.id)
                } ?: emptyList()
            }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        return
    }

    if (error != null || itinerary == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(error ?: "Itinerario no encontrado", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(itinerary!!.destino) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingActivityId = null
                    showActivityDialog = true
                }
            ) {
                Icon(Icons.Default.Add, "Nueva actividad")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // === INFO ITINERARIO ===
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Destino", style = MaterialTheme.typography.labelSmall)
                        Text(itinerary!!.destino, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(12.dp))
                        Text("Fechas", style = MaterialTheme.typography.labelSmall)
                        Text("${itinerary!!.fechaInicio} – ${itinerary!!.fechaFin}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(12.dp))
                        Text("Estado", style = MaterialTheme.typography.labelSmall)
                        AssistChip(
                            onClick = {},
                            label = { Text(itinerary!!.estado) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (itinerary!!.estado) {
                                    "Borrador" -> Color(0xFFFFF3E0)
                                    "Publicado" -> Color(0xFFE8F5E8)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                        )
                    }
                }
            }

            item {
                Button(onClick = { /* IA */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Reorganizar con IA")
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("edit/$itineraryId") },
                        modifier = Modifier.weight(1f)
                    ) { Text("Editar") }

                    if (itinerary!!.estado == "Borrador") {
                        Button(
                            onClick = {
                                db.collection("usuarios").document(userId)
                                    .collection("itinerarios").document(itineraryId)
                                    .update("estado", "Publicado")
                                    .addOnSuccessListener {
                                        navController.navigate("detail/$itineraryId") {
                                            popUpTo("detail/$itineraryId") { inclusive = true }
                                        }
                                    }
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Publicar") }
                    }
                }
            }

            // === TÍTULO ACTIVIDADES ===
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Actividades", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    TextButton(onClick = { editingActivityId = null; showActivityDialog = true }) {
                        Text("Agregar")
                    }
                }
            }

            // === LISTA DE ACTIVIDADES ===
            if (activities.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(Modifier.padding(32.dp), Alignment.Center) {
                            Text("No hay actividades. Pulsa + para agregar", color = Color.Gray)
                        }
                    }
                }
            } else {
                items(activities) { activity ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(activity.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(activity.hora, style = MaterialTheme.typography.bodyMedium)
                                if (activity.descripcion.isNotBlank()) {
                                    Text(activity.descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                            IconButton(onClick = {
                                editingActivityId = activity.id
                                showActivityDialog = true
                            }) {
                                Icon(Icons.Default.Edit, "Editar")
                            }
                            IconButton(onClick = {
                                db.collection("usuarios").document(userId)
                                    .collection("itinerarios").document(itineraryId)
                                    .collection("actividades").document(activity.id)
                                    .delete()
                            }) {
                                Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            // === ELIMINAR ITINERARIO ===
            item {
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar Itinerario", color = Color.White)
                }
            }
        }
    }

    // === DIÁLOGO ACTIVIDAD ===
    if (showActivityDialog) {
        ActivityFormDialog(
            userId = userId,
            itineraryId = itineraryId,
            activityId = editingActivityId,
            onDismiss = {
                showActivityDialog = false
                editingActivityId = null
            },
            onSaveSuccess = { editingActivityId = null }
        )
    }

    // === CONFIRMACIÓN ELIMINAR ITINERARIO ===
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar itinerario") },
            text = { Text("¿Estás seguro? Se eliminarán todas las actividades.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        db.collection("usuarios").document(userId)
                            .collection("itinerarios").document(itineraryId)
                            .delete()
                            .addOnSuccessListener {
                                navController.popBackStack("home", false)
                            }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}