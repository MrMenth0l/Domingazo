package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Itinerary(
    val destino: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val descripcion: String = "",
    val estado: String = "Borrador",
    val urlImagenDestino: String = "",

    @PropertyName("createdAt")
    val createdAt: Timestamp = Timestamp.now(),

    var id: String = ""
)