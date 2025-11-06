package com.example.proyecto1_plataformasmoviles_domingazo.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (isRegister) "Registrarse" else "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), enabled = !loading)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(password, { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), enabled = !loading)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                loading = true
                error = ""
                val task = if (isRegister) auth.createUserWithEmailAndPassword(email, password)
                else auth.signInWithEmailAndPassword(email, password)

                task.addOnCompleteListener {
                    loading = false
                    if (it.isSuccessful) onAuthSuccess()
                    else error = it.exception?.message ?: "Error"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            if (loading) CircularProgressIndicator(Modifier.size(20.dp)) else Text(if (isRegister) "Crear" else "Entrar")
        }

        TextButton(onClick = { isRegister = !isRegister }) {
            Text(if (isRegister) "¿Ya tienes cuenta?" else "¿No tienes cuenta?")
        }

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}