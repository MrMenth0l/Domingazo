package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryFormScreen(
    userId: String,
    itineraryId: String? = null,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var destino by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Borrador") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(itineraryId) {
        if (itineraryId != null) {
            loading = true
            try {
                val doc = db.collection("usuarios")
                    .document(userId)
                    .collection("itinerarios")
                    .document(itineraryId)
                    .get()
                    .await()
                val it = doc.toObject(Itinerary::class.java)?.copy(id = doc.id)
                if (it != null) {
                    destino = it.destino
                    fechaInicio = it.fechaInicio
                    fechaFin = it.fechaFin
                    estado = it.estado
                }
            } catch (e: Exception) {
                error = "Error al cargar: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (itineraryId == null) "Nuevo Itinerario" else "Editar Itinerario") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, "Cancelar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = destino,
                onValueChange = { destino = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaInicio,
                onValueChange = { fechaInicio = it },
                label = { Text("Fecha Inicio (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaFin,
                onValueChange = { fechaFin = it },
                label = { Text("Fecha Fin (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Borrador", "Publicado").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { estado = option; expanded = false }
                        )
                    }
                }
            }

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onCancel) { Text("Cancelar") }
                Button(
                    onClick = {
                        if (destino.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
                            error = "Completa todos los campos"
                            return@Button
                        }
                        loading = true
                        val data = hashMapOf(
                            "destino" to destino,
                            "fechaInicio" to fechaInicio,
                            "fechaFin" to fechaFin,
                            "estado" to estado,
                            "createdAt" to Timestamp.now()
                        )

                        val ref = if (itineraryId == null) {
                            db.collection("usuarios").document(userId).collection("itinerarios").document()
                        } else {
                            db.collection("usuarios").document(userId).collection("itinerarios").document(itineraryId)
                        }

                        ref.set(data).addOnSuccessListener {
                            onSaveSuccess()
                        }.addOnFailureListener {
                            error = "Error al guardar: ${it.message}"
                            loading = false
                        }
                    },
                    enabled = !loading
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(16.dp)) else Text("Guardar")
                }
            }
        }
    }
}