package com.example.proyecto1_plataformasmoviles_domingazo.ui.itinerary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerDialog(
    userId: String,
    itineraryId: String,
    onDismiss: () -> Unit,
    onImageUploaded: (String) -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val storage = FirebaseStorage.getInstance()
    val colorScheme = MaterialTheme.colorScheme

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorScheme.surface,
        title = { Text("Imagen del Destino", color = colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(colorScheme.primary)
                ) {
                    Icon(Icons.Default.Image, null, tint = colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Seleccionar Imagen", color = colorScheme.onPrimary)
                }

                selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }

                if (uploading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = colorScheme.primary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedImageUri?.let { uri ->
                        uploading = true
                        scope.launch {
                            try {
                                val fileName = "${UUID.randomUUID()}.jpg"
                                val ref = storage.reference.child("images/itinerarios/$userId/$itineraryId/$fileName")
                                ref.putFile(uri).await()
                                val url = ref.downloadUrl.await().toString()
                                onImageUploaded(url)
                                onDismiss()
                            } catch (e: Exception) {
                                // Error
                            } finally {
                                uploading = false
                            }
                        }
                    }
                },
                enabled = selectedImageUri != null && !uploading,
                colors = ButtonDefaults.buttonColors(colorScheme.primary)
            ) {
                Text("Subir", color = colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
