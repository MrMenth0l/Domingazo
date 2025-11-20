Domingazo – App de itinerarios en Jetpack Compose
=================================================

Aplicación móvil en Kotlin/Jetpack Compose para planificar y gestionar itinerarios de viaje. Incluye autenticación con Firebase, CRUD de itinerarios y actividades, subida de imágenes, mapa interactivo y modo oscuro persistente.


Resumen rápido
--------------
- Autenticación email/contraseña con Firebase Authentication; registro, login y redirección automática a Home.
- Lista de itinerarios (Firestore) con crear/editar/eliminar, actividades asociadas y estado Borrador o Publicado.
- Detalle con imagen, fechas, actividades, selector lista/mapa (Google Maps Compose) y eliminación completa.
- Subida de imágenes a Firebase Storage desde el selector del formulario.
- Material 3 con tema claro/oscuro; preferencia persistida en DataStore.
- Snackbar de feedback, validaciones de formularios y navegación segura con NavHost.


Flujo y rutas de navegación
---------------------------
- `login` -> inicio de sesión; redirige a `home/{userId}` al autenticar.
- `register` -> alta de usuario y redirección a `home/{userId}`.
- `home/{userId}` -> lista de itinerarios del usuario; accesos a detalle, creación y ajustes.
- `create/{userId}` -> formulario de nuevo itinerario.
- `detail/{userId}/{itineraryId}` -> detalle con actividades, mapa y opciones de edición/eliminación.
- `edit/{userId}/{itineraryId}` -> edición de itinerario existente.
- `settings` -> perfil, modo oscuro y logout.
El NavHost valida que el `userId` solicitado coincide con el usuario autenticado; en caso contrario reenvía al destino correcto o a `login`.


Arquitectura y organización
---------------------------
- UI 100 % Compose con Material 3.
- Navegación declarativa con Navigation Compose.
- Estado simple con `remember` y flujos Firebase; se validan `userId` en cada destino protegido.
- Persistencia ligera con DataStore para modo oscuro.
- Carga de imágenes con Coil y Google Maps Compose para la vista de ruta.
- Carpeta `ui/theme` centraliza colores y tipografía; `SettingsRepository` encapsula DataStore.


Dependencias principales
------------------------
- Kotlin, Compose BOM y Material3
- Navigation Compose
- Firebase Authentication, Firestore, Storage
- DataStore Preferences
- Coil Compose
- Google Maps Compose


Estructura relevante
--------------------
- `app/src/main/java/com/example/proyecto1_plataformasmoviles_domingazo/MainActivity.kt` – arranque, tema y NavGraph.
- `ui/navigation/NavGraph.kt` – rutas, deep links y validaciones de sesión.
- `ui/login`, `ui/register` – autenticación con Firebase.
- `ui/home` – listado de itinerarios del usuario.
- `ui/itinerary` – detalle, formulario, actividades y selector de imágenes.
- `ui/settings` – perfil, modo oscuro y logout.
- `ui/theme` – colores, tipografía y persistencia de modo oscuro.


Configuración rápida
--------------------
Requisitos
- Android Studio Iguana o superior.
- JDK 17.
- Dispositivo/emulador Android 10+.

Clonar
```bash
git clone https://github.com/MrMenth0l/Domingazo.git
cd Domingazo2
```

Firebase
1) Crea un proyecto en Firebase y habilita Authentication (Email/Password), Firestore y Storage.  
2) Descarga `google-services.json` y colócalo en `app/google-services.json` (actualiza el existente si usas otro proyecto).  
3) Revisa reglas mínimas para desarrollo:
```txt
// Firestore (desarrollo)
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /usuarios/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
// Storage (desarrollo)
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /images/itinerarios/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

Compilar y ejecutar
```bash
./gradlew assembleDebug
```
Luego ejecuta desde Android Studio o con `./gradlew installDebug` sobre un dispositivo conectado.


Estructura de datos esperada
----------------------------
- Firestore
  - `usuarios/{userId}`
    - `itinerarios/{itineraryId}` con campos: `destino`, `fechaInicio`, `fechaFin`, `descripcion`, `estado`, `urlImagenDestino`, `createdAt`.
    - `actividades/{activityId}` con campos: `nombre`, `hora`, `descripcion`, `createdAt`.
- Storage
  - `images/itinerarios/{userId}/{itineraryId}/{fileName}.jpg`


Theming y modo oscuro
---------------------
- Paleta normalizada en `ui/theme/Color.kt` y expuesta vía `MaterialTheme.colorScheme`.
- La preferencia de modo oscuro se guarda en DataStore (`SettingsRepository`) y se lee al iniciar la app.
- Los componentes usan colores del tema para mantener consistencia en pantallas claras u oscuras.


Pruebas manuales sugeridas
--------------------------
- Auth: registra un usuario, cierra y abre la app; debe entrar directo a Home si la sesión sigue activa.
- CRUD itinerario: crea con fechas validas (yyyy-MM-dd), agrega imagen y verifica que aparezca en Home y en Detalle.
- Mapa: en detalle cambia entre lista y mapa y valida que el polyline se dibuja.
- Modo oscuro: activa en Settings, reinicia la app y confirma que persiste.
- Deep link: abre `home/{uid}` con usuario autenticado; debe resolver la ruta correcta o reenviar a login si no coincide.


Solución de problemas
---------------------
- Destino no encontrado o deeplink invalido: revisar que el `userId` del enlace coincide con el usuario autenticado; NavHost fuerza la corrección.
- Fallos de gradle sync: confirma JDK 17 y que Google Services esté aplicado tras actualizar `google-services.json`.
- Errores al subir imágenes: verifica permisos de galería en el dispositivo y reglas de Storage que acepten usuarios autenticados.
- Mapa sin carga: habilita Google Maps SDK en el proyecto de Google Cloud y coloca la API key en el `AndroidManifest` si es necesaria.


Roadmap corto
-------------
- Validar formularios con mensajes locales por campo.
- Añadir tests instrumentados de navegación y smoke tests de UI.
- Publicar reglas endurecidas para producción en Firestore y Storage.
- Soporte para compartir itinerarios por enlace.


Autores
-------
- Diego Quan
- Javier Alvarado
- Yehosua Hércules

Universidad del Valle de Guatemala – Curso Plataformas Móviles (Ciclo 2, 2025)
