package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryFormScreen(
    userId: String,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var destino by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Nuevo Itinerario", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = destino,
            onValueChange = { destino = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = fechaInicio,
            onValueChange = {},
            label = { Text("Fecha Inicio") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showStartPicker = true }
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = fechaFin,
            onValueChange = {},
            label = { Text("Fecha Fin") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showEndPicker = true }
        )

        Spacer(Modifier.height(32.dp))

        Row {
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    loading = true
                    val data = hashMapOf(
                        "destino" to destino,
                        "fechaInicio" to fechaInicio,
                        "fechaFin" to fechaFin,
                        "estado" to "Borrador",
                        "createdAt" to Timestamp.now()
                    )
                    db.collection("usuarios").document(userId).collection("itinerarios")
                        .add(data)
                        .addOnSuccessListener { onSaveSuccess() }
                        .addOnFailureListener { loading = false }
                },
                enabled = destino.isNotBlank() && fechaInicio.isNotBlank() && fechaFin.isNotBlank() && !loading,
                modifier = Modifier.weight(1f)
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp)) else Text("Guardar")
            }
        }
    }

    if (showStartPicker) {
        DatePickerDialog(
            onDateSelected = { fechaInicio = formatter.format(it); showStartPicker = false },
            onDismiss = { showStartPicker = false }
        )
    }

    if (showEndPicker) {
        DatePickerDialog(
            onDateSelected = { fechaFin = formatter.format(it); showEndPicker = false },
            onDismiss = { showEndPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(onDateSelected: (Date) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { onDateSelected(Date(it)) }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) {
        DatePicker(state = datePickerState)
    }
}