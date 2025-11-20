package com.example.proyecto1_plataformasmoviles_domingazo.ui.newitinerary

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItineraryScreen(onBackClick: () -> Unit, onSaveClick: (String, String) -> Unit) {
    var destino by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var destinoError by remember { mutableStateOf(false) }
    var fechaError by remember { mutableStateOf(false) }
    var buttonPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(if (buttonPressed) 0.95f else 1f)

    // Validación simple para el formato de fecha (dd/mm/aaaa)
    fun isValidDate(date: String): Boolean {
        val regex = Regex("""^\d{2}/\d{2}/\d{4}$""")
        return date.matches(regex)
    }

    val colorScheme = MaterialTheme.colorScheme
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colorScheme.onSurfaceVariant,
        cursorColor = colorScheme.secondary,
        focusedContainerColor = colorScheme.surface,
        unfocusedContainerColor = colorScheme.surface
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nuevo Itinerario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver a la pantalla anterior",
                            tint = colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            destinoError = destino.isBlank()
                            fechaError = fecha.isBlank() || !isValidDate(fecha)
                            if (!destinoError && !fechaError) {
                                onSaveClick(destino, fecha)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Guardar itinerario",
                            tint = colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface
                )
            )
        },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorScheme.background, colorScheme.surface)
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Texto de encabezado
            Text(
                text = "Planifica tu próximo viaje",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = colorScheme.primary
                )
            )

            // Campo de destino
            OutlinedTextField(
                value = destino,
                onValueChange = {
                    destino = it
                    destinoError = it.isBlank()
                },
                label = { Text("Destino") },
                placeholder = { Text("Ej. Roma") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                isError = destinoError,
                supportingText = {
                    if (destinoError) {
                        Text(
                            text = "Por favor, ingresa un destino",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = textFieldColors,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )

            // Campo de fecha
            OutlinedTextField(
                value = fecha,
                onValueChange = {
                    fecha = it
                    fechaError = it.isBlank() || !isValidDate(it)
                },
                label = { Text("Fechas") },
                placeholder = { Text("Ej. 12/09/2025") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                isError = fechaError,
                supportingText = {
                    if (fechaError) {
                        Text(
                            text = "Ingresa la fecha en formato dd/mm/aaaa",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = textFieldColors,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )

            // Campo de notas (opcional)
            OutlinedTextField(
                value = "",
                onValueChange = { /* Lógica para notas */ },
                label = { Text("Notas (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = textFieldColors,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )

            // Botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, colorScheme.outline)
                ) {
                    Text("Cancelar", fontSize = 16.sp)
                }
                Button(
                    onClick = {
                        buttonPressed = true
                        destinoError = destino.isBlank()
                        fechaError = fecha.isBlank() || !isValidDate(fecha)
                        if (!destinoError && !fechaError) {
                            onSaveClick(destino, fecha)
                        }
                        buttonPressed = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .scale(buttonScale)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text("Guardar", fontSize = 16.sp)
                }
            }

            // Tarjeta de vista previa
                if (destino.isNotBlank() && fecha.isNotBlank() && !destinoError && !fechaError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = destino,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = colorScheme.primary
                            )
                        )
                        Text(
                            text = fecha,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}
