package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import com.google.firebase.Timestamp

data class Itinerary(
    val id: String = "",
    val destino: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val estado: String = "Borrador",
    val createdAt: Timestamp = Timestamp.now()
)