package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
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
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.ColorTokens
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.ErrorRed
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoPrimary
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.IndigoSecondary
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.SurfaceGray
import kotlin.math.roundToInt

@Composable
fun ItineraryScreen(
    itinerary: Itinerary,
    proposals: List<CandidateItinerary>,
    connectedMembers: List<ConnectedMember>,
    modifier: Modifier = Modifier,
) {
    val summary = itinerary.summary

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            HeaderSection(summary = summary)
        }

        if (connectedMembers.isNotEmpty()) {
            item {
                CollaborationBar(connectedMembers = connectedMembers)
            }
        }

        item {
            TimelineSection(activities = itinerary.activities)
        }

        item {
            MiniMapSection()
        }

        item {
            HorizontalDivider()
        }

        item {
            ProposalsSection(proposals = proposals)
        }
    }
}

@Composable
private fun HeaderSection(summary: ItinerarySummary) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Itinerario del ${summary.dateLabel}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        )
        Text(
            text = "Zona horaria: ${summary.timeZone} · ${summary.window}",
            style = MaterialTheme.typography.bodyMedium.copy(color = ColorTokens.muted),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricChip(
                label = "Actividades",
                value = "${summary.activitiesCount}",
            )
            MetricChip(
                label = "Duración",
                value = minutesToLabel(summary.totalDurationMinutes),
            )
            MetricChip(
                label = "Traslados",
                value = minutesToLabel(summary.totalTravelMinutes),
            )
            MetricChip(
                label = "Costo",
                value = summary.formattedCost,
            )
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(color = ColorTokens.muted),
            )
        }
    }
}

@Composable
private fun CollaborationBar(connectedMembers: List<ConnectedMember>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Colaborando ahora",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            connectedMembers.take(4).forEach { member ->
                AvatarBadge(initials = member.initials, isActive = member.isActive)
            }
            if (connectedMembers.size > 4) {
                val remainder = connectedMembers.size - 4
                AvatarBadge(initials = "+$remainder", isActive = false)
            }
        }
    }
}

@Composable
private fun AvatarBadge(initials: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isActive) IndigoPrimary else SurfaceGray),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(color = White),
            )
        }
        if (isActive) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = CircleShape,
                color = AquaAccent,
                modifier = Modifier.size(8.dp),
                content = {},
            )
        }
    }
}

@Composable
private fun TimelineSection(activities: List<ItineraryActivity>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Timeline",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            activities.forEachIndexed { index, item ->
                ActivityRow(
                    index = index,
                    item = item,
                    hasConnector = index < activities.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun ActivityRow(index: Int, item: ItineraryActivity, hasConnector: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TimeRangeLabel(start = item.startTime, end = item.endTime)
            if (hasConnector && item.travelMinutes != null) {
                TravelConnector(minutes = item.travelMinutes)
            }
        }

        ActivityCard(activity = item)
    }
}

@Composable
private fun TimeRangeLabel(start: String, end: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = start,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        )
        Text(
            text = end,
            style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted),
        )
    }
}

@Composable
private fun TravelConnector(minutes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(
            modifier = Modifier
                .width(2.dp)
                .height(8.dp)
                .background(IndigoSecondary),
        )
        Surface(shape = RoundedCornerShape(12.dp), color = IndigoSecondary.copy(alpha = 0.1f)) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                text = "${minutes} min traslado",
                style = MaterialTheme.typography.labelSmall.copy(color = IndigoSecondary),
            )
        }
        Spacer(
            modifier = Modifier
                .width(2.dp)
                .height(8.dp)
                .background(IndigoSecondary),
        )
    }
}

@Composable
private fun ActivityCard(activity: ItineraryActivity) {
    val badgeColors = remember { badgeColors() }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Text(
                        text = activity.locationLabel,
                        style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                ActivityTypePill(type = activity.type)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBlock(label = "Duración", value = minutesToLabel(activity.durationMinutes))
                StatBlock(label = "Costo", value = activity.formattedCost)
                StatBlock(label = "Ventana", value = activity.windowRange)
            }

            if (activity.badges.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(activity.badges) { badge ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(text = badge.label)
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = badgeColors[badge.kind] ?: MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }
                }
            }

            if (!activity.notes.isNullOrBlank()) {
                Text(
                    text = activity.notes,
                    style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted),
                )
            }
        }
    }
}

private fun badgeColors(): Map<ActivityBadgeKind, Color> = mapOf(
    ActivityBadgeKind.Conflict to ErrorRed.copy(alpha = 0.1f),
    ActivityBadgeKind.Info to IndigoPrimary.copy(alpha = 0.1f),
    ActivityBadgeKind.Positive to AquaAccent.copy(alpha = 0.12f),
)

@Composable
private fun ActivityTypePill(type: ActivityType) {
    Surface(
        color = type.tint.copy(alpha = 0.15f),
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            text = type.label.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(color = type.tint, fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = ColorTokens.muted),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun MiniMapSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Mapa del recorrido",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(IndigoPrimary.copy(alpha = 0.9f), AquaAccent.copy(alpha = 0.8f)),
                        ),
                    )
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Mini mapa (mock)",
                        style = MaterialTheme.typography.titleMedium.copy(color = White, fontWeight = FontWeight.SemiBold),
                    )
                    Text(
                        text = "Placeholder para reemplazar con mapa real",
                        style = MaterialTheme.typography.bodySmall.copy(color = White.copy(alpha = 0.8f)),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProposalsSection(proposals: List<CandidateItinerary>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reorganizar con IA",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                )
                Text(
                    text = "Genera 3 propuestas optimizadas y elige la mejor para tu grupo",
                    style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted),
                )
            }
            Button(onClick = { }) {
                Text(text = "Reorganizar")
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            items(proposals) { proposal ->
                ProposalCard(candidate = proposal)
            }
        }
    }
}

@Composable
private fun ProposalCard(candidate: CandidateItinerary) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .width(240.dp)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = candidate.label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = candidate.rationale,
                style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            ProposalMetric(label = "Costo", value = candidate.summary.formattedCost)
            ProposalMetric(label = "Traslados", value = minutesToLabel(candidate.summary.travelMinutes))
            ProposalMetric(label = "Variedad", value = candidate.summary.diversityScore)
            ProposalMetric(label = "Huecos", value = minutesToLabel(candidate.summary.idleMinutes))
            Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Seleccionar")
            }
        }
    }
}

@Composable
private fun ProposalMetric(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = ColorTokens.muted),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
    }
}

// region Models & mock data

@Immutable
data class Itinerary(
    val summary: ItinerarySummary,
    val activities: List<ItineraryActivity>,
)

@Immutable
data class ItinerarySummary(
    val dateLabel: String,
    val timeZone: String,
    val window: String,
    val totalDurationMinutes: Int,
    val totalTravelMinutes: Int,
    val activitiesCount: Int,
    val totalCost: Double,
    val currencyCode: String,
) {
    val formattedCost: String
        get() = "%s %.2f".format(currencyCode, totalCost)
}

@Immutable
data class ItineraryActivity(
    val id: String,
    val title: String,
    val type: ActivityType,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val averageCost: Double,
    val currencyCode: String,
    val locationLabel: String,
    val windowRange: String,
    val travelMinutes: Int?,
    val badges: List<ActivityBadge>,
    val notes: String?,
) {
    val formattedCost: String
        get() = "%s %.0f".format(currencyCode, averageCost)
}

@Immutable
data class ActivityBadge(
    val label: String,
    val kind: ActivityBadgeKind,
)

enum class ActivityBadgeKind { Conflict, Info, Positive }

@Immutable
data class CandidateItinerary(
    val id: String,
    val label: String,
    val summary: ProposalSummary,
    val rationale: String,
)

@Immutable
data class ProposalSummary(
    val travelMinutes: Int,
    val cost: Double,
    val currency: String,
    val idleMinutes: Int,
    val diversity: Double,
) {
    val formattedCost: String get() = "%s %.2f".format(currency, cost)
    val diversityScore: String
        get() = "${(diversity * 100).roundToInt()}%"
}

@Immutable
data class ConnectedMember(
    val id: String,
    val name: String,
    val isActive: Boolean,
) {
    val initials: String = name.split(" ").take(2).joinToString(separator = "") { part ->
        part.firstOrNull()?.uppercase() ?: ""
    }
}

@Immutable
enum class ActivityType(val label: String, val tint: Color) {
    Recreational(label = "Recreacional", tint = Color(0xFF2E7D32)),
    Food(label = "Comida", tint = Color(0xFFF57C00)),
    Touristic(label = "Turística", tint = Color(0xFF1565C0)),
}

val mockItinerary = Itinerary(
    summary = ItinerarySummary(
        dateLabel = "12 de octubre, 2025",
        timeZone = "America/Guatemala",
        window = "09:00 - 22:00",
        totalDurationMinutes = 540,
        totalTravelMinutes = 55,
        activitiesCount = 6,
        totalCost = 585.0,
        currencyCode = "GTQ",
    ),
    activities = listOf(
        ItineraryActivity(
            id = "act_045",
            title = "Desayuno en Café del Centro",
            type = ActivityType.Food,
            startTime = "09:00",
            endTime = "10:00",
            durationMinutes = 60,
            averageCost = 90.0,
            currencyCode = "GTQ",
            locationLabel = "Zona 1, Ciudad de Guatemala",
            windowRange = "08:00-11:00",
            travelMinutes = 8,
            badges = listOf(
                ActivityBadge(label = "Reservar mesa", kind = ActivityBadgeKind.Info),
            ),
            notes = "Sugerido probar el menú chapín."
        ),
        ItineraryActivity(
            id = "act_123",
            title = "Museo Nacional de Historia",
            type = ActivityType.Touristic,
            startTime = "10:15",
            endTime = "12:00",
            durationMinutes = 105,
            averageCost = 75.0,
            currencyCode = "GTQ",
            locationLabel = "Zona 1, Palacio Nacional",
            windowRange = "09:00-17:00",
            travelMinutes = 12,
            badges = listOf(
                ActivityBadge(label = "Incluye exposición temporal", kind = ActivityBadgeKind.Info),
            ),
            notes = null,
        ),
        ItineraryActivity(
            id = "act_267",
            title = "Recorrido guiado por Barrio Yurrita",
            type = ActivityType.Touristic,
            startTime = "12:30",
            endTime = "14:00",
            durationMinutes = 90,
            averageCost = 110.0,
            currencyCode = "GTQ",
            locationLabel = "Zona 2, Barrio Histórico",
            windowRange = "10:00-18:00",
            travelMinutes = 18,
            badges = listOf(
                ActivityBadge(label = "Muy lejos", kind = ActivityBadgeKind.Conflict),
            ),
            notes = "Considerar transporte privado para ahorrar tiempo.",
        ),
        ItineraryActivity(
            id = "act_301",
            title = "Almuerzo en Mercado 24",
            type = ActivityType.Food,
            startTime = "14:20",
            endTime = "15:40",
            durationMinutes = 80,
            averageCost = 120.0,
            currencyCode = "GTQ",
            locationLabel = "Zona 4, Distrito Creativo",
            windowRange = "12:00-16:00",
            travelMinutes = 10,
            badges = listOf(
                ActivityBadge(label = "Reservar menú degustación", kind = ActivityBadgeKind.Info),
            ),
            notes = null,
        ),
        ItineraryActivity(
            id = "act_350",
            title = "Tarde libre en Cuatro Grados Norte",
            type = ActivityType.Recreational,
            startTime = "16:00",
            endTime = "18:00",
            durationMinutes = 120,
            averageCost = 80.0,
            currencyCode = "GTQ",
            locationLabel = "Zona 4",
            windowRange = "15:00-22:00",
            travelMinutes = 6,
            badges = listOf(
                ActivityBadge(label = "Opción flexible", kind = ActivityBadgeKind.Positive),
            ),
            notes = "Tiempo libre para explorar tiendas locales.",
        ),
        ItineraryActivity(
            id = "act_400",
            title = "Cena en Rooftop Mirador",
            type = ActivityType.Food,
            startTime = "19:00",
            endTime = "21:00",
            durationMinutes = 120,
            averageCost = 110.0,
            currencyCode = "GTQ",
            locationLabel = "Zona 10, Mirador City",
            windowRange = "18:00-23:00",
            travelMinutes = null,
            badges = listOf(
                ActivityBadge(label = "Excede presupuesto", kind = ActivityBadgeKind.Conflict),
            ),
            notes = "Verificar disponibilidad de menú vegetariano.",
        ),
    ),
)

val mockProposals = listOf(
    CandidateItinerary(
        id = "cand_equilibrada",
        label = "Equilibrada",
        summary = ProposalSummary(
            travelMinutes = 42,
            cost = 540.0,
            currency = "GTQ",
            idleMinutes = 30,
            diversity = 0.82,
        ),
        rationale = "Balancea variedad gastronómica y cultural, manteniendo traslados moderados.",
    ),
    CandidateItinerary(
        id = "cand_menos_traslado",
        label = "Menor desplazamiento",
        summary = ProposalSummary(
            travelMinutes = 28,
            cost = 565.0,
            currency = "GTQ",
            idleMinutes = 20,
            diversity = 0.68,
        ),
        rationale = "Agrupa actividades por cercanía para minimizar tráfico en horas pico.",
    ),
    CandidateItinerary(
        id = "cand_mas_economica",
        label = "Más económica",
        summary = ProposalSummary(
            travelMinutes = 50,
            cost = 470.0,
            currency = "GTQ",
            idleMinutes = 45,
            diversity = 0.78,
        ),
        rationale = "Sustituye opciones premium por experiencias locales de bajo costo.",
    ),
)

val mockConnectedMembers = listOf(
    ConnectedMember(id = "user_1", name = "Andrea Gómez", isActive = true),
    ConnectedMember(id = "user_2", name = "Luis Pérez", isActive = true),
    ConnectedMember(id = "user_3", name = "María Hernández", isActive = false),
    ConnectedMember(id = "user_4", name = "Carlos Díaz", isActive = true),
    ConnectedMember(id = "user_5", name = "Fernanda R.", isActive = false),
)

// endregion

private fun minutesToLabel(minutes: Int): String {
    val hours = minutes / 60
    val remainder = minutes % 60
    return when {
        hours > 0 && remainder > 0 -> "%dh %02d".format(hours, remainder)
        hours > 0 -> "%dh".format(hours)
        else -> "%d min".format(remainder)
    }
}

