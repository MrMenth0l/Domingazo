// Archivo: ui/itinerary/ActivityFormDialog.kt
package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFormDialog(
    userId: String,
    itineraryId: String,
    activityId: String? = null,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(activityId) {
        if (activityId != null) {
            loading = true
            try {
                val doc = db.collection("usuarios")
                    .document(userId)
                    .collection("itinerarios")
                    .document(itineraryId)
                    .collection("actividades")
                    .document(activityId)
                    .get()
                    .await()
                val act = doc.toObject(Activity::class.java)?.copy(id = doc.id)
                if (act != null) {
                    nombre = act.nombre
                    hora = act.hora
                    descripcion = act.descripcion
                }
            } catch (e: Exception) {
                // error
            } finally {
                loading = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (activityId == null) "Nueva Actividad" else "Editar Actividad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Hora (ej: 10:00 AM)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank() || hora.isBlank()) return@Button
                    loading = true
                    val data = hashMapOf(
                        "nombre" to nombre,
                        "hora" to hora,
                        "descripcion" to descripcion,
                        "createdAt" to Timestamp.now()
                    )
                    val ref = if (activityId == null) {
                        db.collection("usuarios").document(userId)
                            .collection("itinerarios").document(itineraryId)
                            .collection("actividades").document()
                    } else {
                        db.collection("usuarios").document(userId)
                            .collection("itinerarios").document(itineraryId)
                            .collection("actividades").document(activityId)
                    }
                    ref.set(data).addOnSuccessListener {
                        onSaveSuccess()
                        onDismiss()
                    }
                },
                enabled = !loading
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(16.dp)) else Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}