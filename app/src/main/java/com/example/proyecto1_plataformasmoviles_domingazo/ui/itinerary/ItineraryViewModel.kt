package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItineraryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ItineraryUiState())
    val uiState: StateFlow<ItineraryUiState> = _uiState

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _uiState.value = ItineraryUiState(
            itinerary = mockItinerary,
            proposals = mockProposals,
            connectedMembers = mockConnectedMembers
        )
    }
}

data class ItineraryUiState(
    val itinerary: Itinerary? = null,
    val proposals: List<CandidateItinerary> = emptyList(),
    val connectedMembers: List<ConnectedMember> = emptyList()
)
