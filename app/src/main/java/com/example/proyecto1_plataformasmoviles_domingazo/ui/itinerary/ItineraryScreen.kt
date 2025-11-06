package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.collections.take
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(itineraryId: String, userId: String, onBackClick: () -> Unit) {
    var itinerary by remember { mutableStateOf<Itinerary?>(null) }
    var loading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(itineraryId) {
        try {
            val doc = db.collection("usuarios").document(userId).collection("itinerarios").document(itineraryId).get().await()
            if (doc.exists()) {
                val data = doc.data!!
                val summary = ItinerarySummary(
                    dateLabel = data["fechaInicio"] as String + " – " + data["fechaFin"] as String,
                    timeZone = "America/Guatemala",
                    window = "09:00 - 22:00",
                    totalDurationMinutes = 540,
                    totalTravelMinutes = 55,
                    activitiesCount = 6,
                    totalCost = 585.0,
                    currencyCode = "GTQ"
                )
                itinerary = Itinerary(summary, mockItinerary.activities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            loading = false
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        return
    }

    if (itinerary == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Itinerario no encontrado") }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itinerario Detalle") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxWidth().background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(16.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { HeaderSection(itinerary!!.summary) }
            item { CollaborationBar(mockConnectedMembers) }
            item { TimelineSection(itinerary!!.activities) }
            item { MiniMapSection() }
            item { ProposalsSection(mockProposals) }
        }
    }
}

// === COMPOSABLES PRIVADOS ===

@Composable
private fun HeaderSection(summary: ItinerarySummary) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Itinerario del ${summary.dateLabel}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold))
        Text("Zona horaria: ${summary.timeZone} · ${summary.window}", style = MaterialTheme.typography.bodyMedium.copy(color = ColorTokens.muted))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricChip("Actividades", "${summary.activitiesCount}")
            MetricChip("Duración", minutesToLabel(summary.totalDurationMinutes))
            MetricChip("Traslados", minutesToLabel(summary.totalTravelMinutes))
            MetricChip("Costo", summary.formattedCost)
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(label, style = MaterialTheme.typography.labelMedium.copy(color = ColorTokens.muted))
        }
    }
}

@Composable
private fun CollaborationBar(connectedMembers: List<ConnectedMember>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Colaborando ahora", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            connectedMembers.take(4).forEach { AvatarBadge(it.initials, it.isActive) }
            if (connectedMembers.size > 4) AvatarBadge("+${connectedMembers.size - 4}", false) }
    }
}


@Composable
private fun AvatarBadge(initials: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(if (isActive) IndigoPrimary else SurfaceGray), Alignment.Center) {
            Text(initials, style = MaterialTheme.typography.titleMedium.copy(color = White))
        }
        if (isActive) {
            Spacer(Modifier.height(4.dp))
            Surface(shape = CircleShape, color = AquaAccent, modifier = Modifier.size(8.dp)) {}
        }
    }
}

@Composable
private fun TimelineSection(activities: List<ItineraryActivity>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Timeline", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            activities.forEachIndexed { index, item ->
                ActivityRow(index, item, index < activities.lastIndex)
            }
        }
    }
}

@Composable
private fun ActivityRow(index: Int, item: ItineraryActivity, hasConnector: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TimeRangeLabel(item.startTime, item.endTime)
            if (hasConnector && item.travelMinutes != null) TravelConnector(item.travelMinutes)
        }
        ActivityCard(item)
    }
}

@Composable
private fun TimeRangeLabel(start: String, end: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(start, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
        Text(end, style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted))
    }
}

@Composable
private fun TravelConnector(minutes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.width(2.dp).height(8.dp).background(IndigoSecondary))
        Surface(shape = RoundedCornerShape(12.dp), color = IndigoSecondary.copy(alpha = 0.1f)) {
            Text("$minutes min traslado", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(color = IndigoSecondary))
        }
        Spacer(Modifier.width(2.dp).height(8.dp).background(IndigoSecondary))
    }
}

@Composable
private fun ActivityCard(activity: ItineraryActivity) {
    val badgeColors = remember { badgeColors() }
    ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(activity.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                    Text(activity.locationLabel, style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                ActivityTypePill(activity.type)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBlock("Duración", minutesToLabel(activity.durationMinutes))
                StatBlock("Costo", activity.formattedCost)
                StatBlock("Ventana", activity.windowRange)
            }
            if (activity.badges.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(activity.badges) { badge ->
                        AssistChip(
                            onClick = {},
                            label = { Text(badge.label) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = badgeColors[badge.kind] ?: MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
            if (!activity.notes.isNullOrBlank()) {
                Text(activity.notes, style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted))
            }
        }
    }
}

private fun badgeColors(): Map<ActivityBadgeKind, Color> = mapOf(
    ActivityBadgeKind.Conflict to ErrorRed.copy(alpha = 0.1f),
    ActivityBadgeKind.Info to IndigoPrimary.copy(alpha = 0.1f),
    ActivityBadgeKind.Positive to AquaAccent.copy(alpha = 0.12f)
)

@Composable
private fun ActivityTypePill(type: ActivityType) {
    Surface(color = type.tint.copy(alpha = 0.15f), shape = RoundedCornerShape(999.dp)) {
        Text(type.label.uppercase(), Modifier.padding(horizontal = 14.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium.copy(color = type.tint, fontWeight = FontWeight.Medium))
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = ColorTokens.muted))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
    }
}

@Composable
private fun MiniMapSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Mapa del recorrido", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
        Card(shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(2.dp)) {
            Box(
                Modifier.background(Brush.linearGradient(listOf(IndigoPrimary.copy(0.9f), AquaAccent.copy(0.8f))))
                    .fillMaxWidth().height(180.dp),
                Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mini mapa (mock)", style = MaterialTheme.typography.titleMedium.copy(color = White, fontWeight = FontWeight.SemiBold))
                    Text("Placeholder para mapa real", style = MaterialTheme.typography.bodySmall.copy(color = White.copy(alpha = 0.8f)))
                }
            }
        }
    }
}

@Composable
private fun ProposalsSection(proposals: List<CandidateItinerary>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Reorganizar con IA", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                Text("Genera 3 propuestas optimizadas", style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted))
            }
            Button(onClick = {}) { Text("Reorganizar") }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
            items(proposals) { ProposalCard(it) }
        }
    }
}

@Composable
private fun ProposalCard(candidate: CandidateItinerary) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.width(240.dp).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(candidate.label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(candidate.rationale, style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted), maxLines = 3, overflow = TextOverflow.Ellipsis)
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
            ProposalMetric("Costo", candidate.summary.formattedCost)
            ProposalMetric("Traslados", minutesToLabel(candidate.summary.travelMinutes))
            ProposalMetric("Variedad", candidate.summary.diversityScore)
            ProposalMetric("Huecos", minutesToLabel(candidate.summary.idleMinutes))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Seleccionar") }
        }
    }
}

@Composable
private fun ProposalMetric(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
    }
}

// === MODELOS Y MOCKS ===

@Immutable data class Itinerary(val summary: ItinerarySummary, val activities: List<ItineraryActivity>)
@Immutable data class ItinerarySummary(
    val dateLabel: String, val timeZone: String, val window: String,
    val totalDurationMinutes: Int, val totalTravelMinutes: Int, val activitiesCount: Int,
    val totalCost: Double, val currencyCode: String
) { val formattedCost: String get() = "%s %.2f".format(currencyCode, totalCost) }

@Immutable data class ItineraryActivity(
    val id: String, val title: String, val type: ActivityType, val startTime: String, val endTime: String,
    val durationMinutes: Int, val averageCost: Double, val currencyCode: String, val locationLabel: String,
    val windowRange: String, val travelMinutes: Int?, val badges: List<ActivityBadge>, val notes: String?
) { val formattedCost: String get() = "%s %.0f".format(currencyCode, averageCost) }

@Immutable data class ActivityBadge(val label: String, val kind: ActivityBadgeKind)
enum class ActivityBadgeKind { Conflict, Info, Positive }

@Immutable data class CandidateItinerary(val id: String, val label: String, val summary: ProposalSummary, val rationale: String)
@Immutable data class ProposalSummary(
    val travelMinutes: Int, val cost: Double, val currency: String, val idleMinutes: Int, val diversity: Double
) {
    val formattedCost: String get() = "%s %.2f".format(currency, cost)
    val diversityScore: String get() = "${(diversity * 100).roundToInt()}%"
}

@Immutable data class ConnectedMember(val id: String, val name: String, val isActive: Boolean) {
    val initials: String = name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
}

@Immutable enum class ActivityType(val label: String, val tint: Color) {
    Recreational("Recreacional", Color(0xFF2E7D32)),
    Food("Comida", Color(0xFFF57C00)),
    Touristic("Turística", Color(0xFF1565C0))
}

val mockItinerary = Itinerary(
    summary = ItinerarySummary("12 Oct – 15 Oct", "America/Guatemala", "09:00 - 22:00", 540, 55, 6, 585.0, "GTQ"),
    activities = listOf(
        ItineraryActivity(
            id = "1", title = "Desayuno", type = ActivityType.Food, startTime = "09:00", endTime = "10:00",
            durationMinutes = 60, averageCost = 90.0, currencyCode = "GTQ", locationLabel = "Café Centro",
            windowRange = "08:00-11:00", travelMinutes = 8, badges = listOf(ActivityBadge("Reservar", ActivityBadgeKind.Info)), notes = null
        )
        // ... (agrega más si quieres)
    )
)

val mockProposals = listOf(
    CandidateItinerary("1", "Equilibrada", ProposalSummary(42, 540.0, "GTQ", 30, 0.82), "Balancea todo.")
)

val mockConnectedMembers = listOf(
    ConnectedMember("1", "Andrea G.", true),
    ConnectedMember("2", "Luis P.", true)
)

private fun minutesToLabel(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return when { h > 0 && m > 0 -> "${h}h ${m}min"; h > 0 -> "${h}h"; else -> "$m min" }
}