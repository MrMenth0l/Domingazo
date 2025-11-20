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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
    val colorScheme = MaterialTheme.colorScheme

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
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = colorScheme.primary) }
        return
    }

    if (error != null || itinerary == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(error ?: "Itinerario no encontrado", color = colorScheme.error)
        }
        return
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(itinerary!!.destino, color = colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { showMap = !showMap }) {
                        Icon(
                            imageVector = if (showMap) Icons.Default.List else Icons.Default.Map,
                            contentDescription = if (showMap) "Ver lista" else "Ver mapa",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.surface,
                    navigationIconContentColor = colorScheme.primary,
                    titleContentColor = colorScheme.onSurface,
                    actionIconContentColor = colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            if (!showMap) {
                FloatingActionButton(
                    onClick = { showActivityDialog = true; editingActivityId = null },
                    containerColor = colorScheme.secondary,
                    contentColor = colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.Add, "Nueva actividad")
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary),
                    border = BorderStroke(1.dp, colorScheme.primary)
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
                    Polyline(points = route, color = colorScheme.primary, width = 8f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ruta: Ciudad de Guatemala to ${itinerary!!.destino}", style = MaterialTheme.typography.titleMedium, color = colorScheme.onSurface)
                        Text("Distancia: ~45 km | Tiempo: ~1h 15min", color = colorScheme.onSurfaceVariant)
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
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Destino", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                            Text(itinerary!!.destino, style = MaterialTheme.typography.titleLarge, color = colorScheme.primary)
                            Spacer(Modifier.height(12.dp))
                            Text("Fechas", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                            Text("${itinerary!!.fechaInicio} to ${itinerary!!.fechaFin}", color = colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(12.dp))
                            Text("Estado", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                            val published = itinerary!!.estado == "Publicado"
                            AssistChip(
                                onClick = {},
                                label = { Text(itinerary!!.estado) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (published) colorScheme.secondaryContainer else colorScheme.tertiaryContainer,
                                    labelColor = if (published) colorScheme.onSecondaryContainer else colorScheme.onTertiaryContainer
                                )
                            )
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                navController.navigate("edit/$userId/$itineraryId")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary),
                            border = BorderStroke(1.dp, colorScheme.primary)
                        ) {
                            Text("Editar")
                        }

                        if (itinerary!!.estado == "Borrador") {
                            Button(
                                onClick = { /* Publicar */ },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                            ) {
                                Text("Publicar", color = colorScheme.onPrimary)
                            }
                        }
                    }
                }

                item {
                    Text("Actividades", style = MaterialTheme.typography.titleMedium, color = colorScheme.onSurface)
                }

                if (activities.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                        ) {
                            Text("No hay actividades", modifier = Modifier.padding(32.dp), color = colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(activities) { activity ->
                        Card(colors = CardDefaults.cardColors(containerColor = colorScheme.surface)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(activity.nombre, style = MaterialTheme.typography.titleMedium, color = colorScheme.onSurface)
                                    Text(activity.hora, color = colorScheme.onSurfaceVariant)
                                    Text(activity.descripcion, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { editingActivityId = activity.id; showActivityDialog = true }) {
                                    Icon(Icons.Default.Edit, null, tint = colorScheme.primary)
                                }
                                IconButton(onClick = { /* Eliminar */ }) {
                                    Icon(Icons.Default.Delete, null, tint = colorScheme.error)
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar Itinerario", color = colorScheme.onError)
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
                            .addOnSuccessListener {
                                // Navegar a home del usuario y limpiar el stack
                                navController.navigate("home/$userId") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                            .addOnFailureListener {
                                // Opcional: mostrar error
                            }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
