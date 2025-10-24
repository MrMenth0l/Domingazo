package com.example.proyecto1_plataformasmoviles_domingazo.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onItineraryClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onNewItineraryClick: () -> Unit
) {
    val itinerarios = listOf(
        Triple("Roma", "12–14 Sep", "Activo"),
        Triple("Oaxaca", "5–7 Oct", "Borrador"),
        Triple("CDMX", "21–22 Nov", "Finalizado")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Itinerarios", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewItineraryClick,
                containerColor = AquaAccent
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Itinerario")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(itinerarios) { (destino, fecha, estado) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(4.dp),
                    onClick = { onItineraryClick(destino) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = destino,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = fecha,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                        Text(
                            text = estado,
                            color = when (estado) {
                                "Activo" -> Color(0xFF2E7D32)
                                "Borrador" -> Color(0xFFF57C00)
                                else -> Color(0xFF616161)
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
