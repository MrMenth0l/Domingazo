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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoPrimary

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nuevo Itinerario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = IndigoPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver a la pantalla anterior",
                            tint = IndigoPrimary
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
                            tint = AquaAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = IndigoPrimary
                )
            )
        },
        containerColor = Color(0xFFF5F7FA) // Fondo claro consistente con HomeScreen
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
            // Texto de encabezado
            Text(
                text = "Planifica tu próximo viaje",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = IndigoPrimary
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = Color(0xFF757575),
                    focusedLabelColor = IndigoPrimary,
                    cursorColor = AquaAccent
                ),
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = Color(0xFF757575),
                    focusedLabelColor = IndigoPrimary,
                    cursorColor = AquaAccent
                ),
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = Color(0xFF757575),
                    focusedLabelColor = IndigoPrimary,
                    cursorColor = AquaAccent
                ),
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
                        contentColor = Color(0xFF757575)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF757575))
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
                        containerColor = AquaAccent,
                        contentColor = Color.White
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
                        containerColor = Color.White
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
                                color = IndigoPrimary
                            )
                        )
                        Text(
                            text = fecha,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = Color(0xFF757575)
                            )
                        )
                    }
                }
            }
        }
    }
}