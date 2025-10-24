package com.example.proyecto1_plataformasmoviles_domingazo.ui.newitinerary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItineraryScreen(onBackClick: () -> Unit) {
    var destino by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Itinerario") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* guardar */ }) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = destino, onValueChange = { destino = it }, label = { Text("Destino") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fechas") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar Itinerario")
            }
        }
    }
}
