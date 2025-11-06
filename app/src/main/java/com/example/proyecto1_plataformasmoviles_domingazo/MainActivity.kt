package com.example.proyecto1_plataformasmoviles_domingazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.proyecto1_plataformasmoviles_domingazo.ui.navigation.NavGraph
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.Proyecto1PlataformasmovilesDomingazoTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            Proyecto1PlataformasmovilesDomingazoTheme {
                val auth = FirebaseAuth.getInstance()
                val userId = auth.currentUser?.uid
                NavGraph(startDestination = if (userId == null) "auth" else "home", userId = userId)
            }
        }
    }
}