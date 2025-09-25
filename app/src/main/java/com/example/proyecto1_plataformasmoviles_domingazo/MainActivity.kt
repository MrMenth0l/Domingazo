package com.example.proyecto1_plataformasmoviles_domingazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.ItineraryScreen
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.mockConnectedMembers
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.mockItinerary
import com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary.mockProposals
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.ProyectoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProyectoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ItineraryDemo()
                }
            }
        }
    }
}

@Composable
private fun ItineraryDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ItineraryScreen(
            itinerary = mockItinerary,
            proposals = mockProposals,
            connectedMembers = mockConnectedMembers,
        )
    }
}
