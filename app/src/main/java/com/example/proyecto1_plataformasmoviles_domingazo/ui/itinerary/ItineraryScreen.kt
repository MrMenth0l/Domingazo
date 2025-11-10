package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

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
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                Button(
                    onClick = { /* IA futura */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                    ) {
                        Text("Editar")
                    }

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
                        ) {
                            Text("Publicar")
                        }
                    }
                }
            }
        }
    }
}