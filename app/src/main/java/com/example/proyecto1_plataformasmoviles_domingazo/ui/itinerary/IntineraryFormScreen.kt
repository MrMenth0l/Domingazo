package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoPrimary
import com.google.firebase.Timestamp
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
    var descripcion by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Borrador") }
    var urlImagenDestino by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showImageDialog by remember { mutableStateOf(false) }

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
                val data = doc.data ?: return@LaunchedEffect
                destino = data["destino"] as? String ?: ""
                fechaInicio = data["fechaInicio"] as? String ?: ""
                fechaFin = data["fechaFin"] as? String ?: ""
                descripcion = data["descripcion"] as? String ?: ""
                estado = data["estado"] as? String ?: "Borrador"
                urlImagenDestino = data["urlImagenDestino"] as? String ?: ""
            } catch (e: Exception) {
                error = "Error al cargar"
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
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (itineraryId == null) "Nuevo Itinerario" else "Editar", color = IndigoPrimary) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, "Cancelar", tint = IndigoPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
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
            // IMAGEN
            Card(colors = CardDefaults.cardColors(Color.White)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (urlImagenDestino.isNotBlank()) {
                        AsyncImage(
                            model = urlImagenDestino,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp).clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                    Button(
                        onClick = { showImageDialog = true },
                        colors = ButtonDefaults.buttonColors(AquaAccent)
                    ) {
                        Text(if (urlImagenDestino.isBlank()) "Agregar Imagen" else "Cambiar", color = Color.White)
                    }
                }
            }

            OutlinedTextField(
                value = destino,
                onValueChange = { destino = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary, cursorColor = AquaAccent)
            )

            OutlinedTextField(
                value = fechaInicio,
                onValueChange = { fechaInicio = it },
                label = { Text("Fecha Inicio (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary, cursorColor = AquaAccent)
            )

            OutlinedTextField(
                value = fechaFin,
                onValueChange = { fechaFin = it },
                label = { Text("Fecha Fin (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary, cursorColor = AquaAccent)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary, cursorColor = AquaAccent)
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Borrador", "Publicado").forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { estado = option; expanded = false })
                    }
                }
            }

            if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onCancel) { Text("Cancelar") }
                Button(
                    onClick = {
                        if (destino.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
                            error = "Completa los campos obligatorios"
                            return@Button
                        }
                        loading = true
                        val data = hashMapOf(
                            "destino" to destino,
                            "fechaInicio" to fechaInicio,
                            "fechaFin" to fechaFin,
                            "descripcion" to descripcion,
                            "estado" to estado,
                            "urlImagenDestino" to urlImagenDestino,
                            "createdAt" to Timestamp.now()
                        )
                        val ref = if (itineraryId == null) {
                            db.collection("usuarios").document(userId).collection("itinerarios").document()
                        } else {
                            db.collection("usuarios").document(userId).collection("itinerarios").document(itineraryId)
                        }
                        ref.set(data).addOnSuccessListener { onSaveSuccess() }
                            .addOnFailureListener { error = "Error al guardar"; loading = false }
                    },
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(AquaAccent)
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(16.dp), color = Color.White)
                    else Text("Guardar", color = Color.White)
                }
            }
        }
    }

    if (showImageDialog) {
        ImagePickerDialog(
            userId = userId,
            itineraryId = itineraryId ?: "",
            onDismiss = { showImageDialog = false },
            onImageUploaded = { url ->
                urlImagenDestino = url
                if (itineraryId != null) {
                    db.collection("usuarios").document(userId)
                        .collection("itinerarios").document(itineraryId)
                        .update("urlImagenDestino", url)
                }
            }
        )
    }
}