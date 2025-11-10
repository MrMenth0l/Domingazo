// Archivo: ui/home/HomeScreen.kt
package com.example.proyecto1_plataformasmoviles_domingazo.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_plataformasmoviles_domingazo.R
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.Itinerary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: String,
    onItineraryClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onNewItineraryClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var itinerarios by remember { mutableStateOf<List<Itinerary>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        db.collection("usuarios").document(userId).collection("itinerarios")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                loading = false
                itinerarios = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(Itinerary::class.java)?.copy(id = doc.id)
                } ?: emptyList()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Itinerarios", fontWeight = FontWeight.Bold) },
                actions = { IconButton(onClick = onSettingsClick) { Icon(Icons.Default.Person, "Perfil") } }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onNewItineraryClick) { Icon(Icons.Default.Add, "Nuevo") } }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            if (loading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else if (itinerarios.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text("No hay itinerarios") }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(itinerarios) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onItineraryClick(item.id) },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(Modifier.padding(16.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.ic_placeholder),
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(item.destino, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("${item.fechaInicio} – ${item.fechaFin}", color = Color.Gray)
                                    Surface(
                                        color = when (item.estado) {
                                            "Publicado" -> Color(0xFFE8F5E8)
                                            else -> Color(0xFFFFF3E0)
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            item.estado,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = when (item.estado) {
                                                "Publicado" -> Color(0xFF2E7D32)
                                                else -> Color(0xFFF57C00)
                                            },
                                            fontSize = 12.sp
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
}