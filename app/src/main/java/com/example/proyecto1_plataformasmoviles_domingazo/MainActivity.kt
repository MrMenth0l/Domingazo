package com.example.proyecto1_plataformasmoviles_domingazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import com.example.proyecto1_plataformasmoviles_domingazo.ui.navigation.NavGraph
import com.example.proyecto1_plataformasmoviles_domingazo.ui.theme.Proyecto1PlataformasMovilesDomingazoTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            Proyecto1PlataformasMovilesDomingazoTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser
                    val startDestination = if (user != null) "home/${user.uid}" else "login"
                    NavGraph(startDestination = startDestination)
                }
            }
        }
    }
}