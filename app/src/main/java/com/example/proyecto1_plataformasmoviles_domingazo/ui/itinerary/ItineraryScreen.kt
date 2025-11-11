package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoPrimary
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    itineraryId: String,
    userId: String,
    navController: NavController,
    onBackClick: () -> Unit
) {
    var itinerary by remember { mutableStateOf<Itinerary?>(null) }
    var activities by remember { mutableStateOf<List<Activity>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showActivityDialog by remember { mutableStateOf(false) }
    var editingActivityId by remember { mutableStateOf<String?>(null) }
    var showMap by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    // CARGAR ITINERARIO
    LaunchedEffect(itineraryId) {
        try {
            val doc = db.collection("usuarios")
                .document(userId)
                .collection("itinerarios")
                .document(itineraryId)
                .get()
                .await()
            if (doc.exists()) {
                itinerary = doc.toObject(Itinerary::class.java)?.copy(id = doc.id)
            } else {
                error = "Itinerario no encontrado"
            }
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            loading = false
        }
    }

    // CARGAR ACTIVIDADES
    LaunchedEffect(itineraryId) {
        db.collection("usuarios").document(userId)
            .collection("itinerarios").document(itineraryId)
            .collection("actividades")
            .orderBy("createdAt")
            .addSnapshotListener { snap, _ ->
                activities = snap?.documents?.mapNotNull {
                    it.toObject(Activity::class.java)?.copy(id = it.id)
                } ?: emptyList()
            }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = IndigoPrimary) }
        return
    }

    if (error != null || itinerary == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(error ?: "Itinerario no encontrado", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(itinerary!!.destino, color = IndigoPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = IndigoPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showMap = !showMap }) {
                        Icon(
                            imageVector = if (showMap) Icons.Default.List else Icons.Default.Map,
                            contentDescription = if (showMap) "Ver lista" else "Ver mapa",
                            tint = IndigoPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (!showMap) {
                FloatingActionButton(
                    onClick = { showActivityDialog = true; editingActivityId = null },
                    containerColor = AquaAccent
                ) {
                    Icon(Icons.Default.Add, "Nueva actividad", tint = Color.White)
                }
            }
        }
    ) { padding ->

        if (showMap) {
            // === MAPA ===
            Column {
                OutlinedButton(
                    onClick = { showMap = false },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = IndigoPrimary),
                    border = BorderStroke(1.dp, IndigoPrimary)
                ) {
                    Icon(Icons.Default.ArrowBack, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Volver al detalle")
                }

                val start = LatLng(14.6349, -90.5069) // Ciudad de Guatemala
                val end = LatLng(14.5562, -90.7297)   // Antigua Guatemala
                val route = listOf(start, end)

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(end, 10f)
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(horizontal = 16.dp),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                ) {
                    Marker(state = MarkerState(end), title = itinerary!!.destino)
                    Marker(state = MarkerState(start), title = "Ciudad de Guatemala")
                    Polyline(points = route, color = Color.Blue, width = 8f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ruta: Ciudad de Guatemala to ${itinerary!!.destino}", style = MaterialTheme.typography.titleMedium, color = IndigoPrimary)
                        Text("Distancia: ~45 km | Tiempo: ~1h 15min", color = Color.Gray)
                    }
                }
            }
        } else {
            // === DETALLE ===
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // IMAGEN DEL DESTINO
                if (itinerary!!.urlImagenDestino.isNotBlank()) {
                    item {
                        AsyncImage(
                            model = itinerary!!.urlImagenDestino,
                            contentDescription = "Imagen del destino",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Destino", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(itinerary!!.destino, style = MaterialTheme.typography.titleLarge, color = IndigoPrimary)
                            Spacer(Modifier.height(12.dp))
                            Text("Fechas", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("${itinerary!!.fechaInicio} to ${itinerary!!.fechaFin}", color = Color.Gray)
                            Spacer(Modifier.height(12.dp))
                            Text("Estado", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            AssistChip(
                                onClick = {},
                                label = { Text(itinerary!!.estado) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (itinerary!!.estado == "Publicado") Color(0xFFE8F5E8) else Color(0xFFFFF3E0),
                                    labelColor = if (itinerary!!.estado == "Publicado") Color(0xFF2E7D32) else Color(0xFFF57C00)
                                )
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = { /* Reorganizar con IA */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AquaAccent)
                    ) {
                        Text("Reorganizar con IA", color = Color.White)
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { navController.navigate("edit/$itineraryId") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = IndigoPrimary),
                            border = BorderStroke(1.dp, IndigoPrimary)
                        ) { Text("Editar") }

                        if (itinerary!!.estado == "Borrador") {
                            Button(
                                onClick = { /* Publicar */ },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AquaAccent)
                            ) { Text("Publicar") }
                        }
                    }
                }

                item {
                    Text("Actividades", style = MaterialTheme.typography.titleMedium, color = IndigoPrimary)
                }

                if (activities.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text("No hay actividades", modifier = Modifier.padding(32.dp), color = Color.Gray)
                        }
                    }
                } else {
                    items(activities) { activity ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(activity.nombre, style = MaterialTheme.typography.titleMedium, color = IndigoPrimary)
                                    Text(activity.hora, color = Color.Gray)
                                    Text(activity.descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                                IconButton(onClick = { editingActivityId = activity.id; showActivityDialog = true }) {
                                    Icon(Icons.Default.Edit, null, tint = IndigoPrimary)
                                }
                                IconButton(onClick = { /* Eliminar */ }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar Itinerario", color = Color.White)
                    }
                }
            }
        }
    }

    // DIÁLOGO ACTIVIDAD
    if (showActivityDialog) {
        ActivityFormDialog(
            userId = userId,
            itineraryId = itineraryId,
            activityId = editingActivityId,
            onDismiss = { showActivityDialog = false; editingActivityId = null },
            onSaveSuccess = { editingActivityId = null }
        )
    }

    // DIÁLOGO ELIMINAR
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar itinerario") },
            text = { Text("¿Estás seguro? Se eliminarán todas las actividades.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        db.collection("usuarios").document(userId)
                            .collection("itinerarios").document(itineraryId)
                            .delete()
                            .addOnSuccessListener { navController.popBackStack("home", false) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}