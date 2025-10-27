package com.example.proyecto1_plataformasmoviles_domingazo.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoPrimary
import com.example.proyecto1_plataformasmoviles_domingazo.R

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

    // State for FAB animation
    var fabPressed by remember { mutableStateOf(false) }
    val fabScale by animateFloatAsState(if (fabPressed) 0.9f else 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Itinerarios",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = IndigoPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Perfil de usuario",
                            tint = IndigoPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = IndigoPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    fabPressed = true
                    onNewItineraryClick()
                    fabPressed = false
                },
                modifier = Modifier.scale(fabScale),
                containerColor = AquaAccent,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Crear nuevo itinerario",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = Color(0xFFF5F7FA) // Light background for depth
    ) { padding ->
        if (itinerarios.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "¡No hay itinerarios!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Crea uno nuevo con el botón +",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFF5F7FA), Color.White)
                        )
                    )
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                item {
                    // Header image or banner
                    Image(
                        painter = painterResource(id = R.drawable.travel_banner), // Add a banner image in resources
                        contentDescription = "Banner de viajes",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(itinerarios) { (destino, fecha, estado) ->
                    var isPressed by remember { mutableStateOf(false) }
                    val cardScale by animateFloatAsState(if (isPressed) 0.98f else 1f)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(cardScale)
                            .clickable {
                                isPressed = true
                                onItineraryClick(destino)
                                isPressed = false
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Placeholder for destination image
                            Image(
                                painter = painterResource(
                                    id = when (destino) {
                                        "Roma" -> R.drawable.roma_image // Add actual resource
                                        "Oaxaca" -> R.drawable.oaxaca_image
                                        "CDMX" -> R.drawable.cdmx_image
                                        else -> R.drawable.placeholder_image
                                    }
                                ),
                                contentDescription = "Imagen de $destino",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = destino,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = IndigoPrimary
                                )
                                Text(
                                    text = fecha,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF757575),
                                        fontSize = 14.sp
                                    )
                                )
                                // Status badge
                                Surface(
                                    color = when (estado) {
                                        "Activo" -> Color(0xFF2E7D32).copy(alpha = 0.1f)
                                        "Borrador" -> Color(0xFFF57C00).copy(alpha = 0.1f)
                                        else -> Color(0xFF616161).copy(alpha = 0.1f)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = estado,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = when (estado) {
                                            "Activo" -> Color(0xFF2E7D32)
                                            "Borrador" -> Color(0xFFF57C00)
                                            else -> Color(0xFF616161)
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}