package com.example.proyecto1_plataformasmoviles_domingazo.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.AquaAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Surface(
                shape = CircleShape,
                color = AquaAccent.copy(alpha = 0.2f),
                modifier = Modifier.size(120.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    tint = AquaAccent
                )
            }
            Text("Diego Quan", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text("diego.quan@uvg.edu.gt", color = Color.Gray)
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Button(onClick = { /* editar perfil */ }) { Text("Editar perfil") }
            Button(onClick = { /* cerrar sesión */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Cerrar sesión", color = Color.White)
            }
        }
    }
}
