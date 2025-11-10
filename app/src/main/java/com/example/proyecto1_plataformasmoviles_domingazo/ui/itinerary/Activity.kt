package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import com.google.firebase.Timestamp

data class Activity(
    val id: String = "",
    val nombre: String = "",
    val hora: String = "",
    val descripcion: String = "",
    val createdAt: Timestamp = Timestamp.now()
)