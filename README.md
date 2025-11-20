Domingazo – App de itinerarios en Jetpack Compose
=================================================

Aplicación móvil en Kotlin/Jetpack Compose para planificar y gestionar itinerarios de viaje. Incluye autenticación con Firebase, CRUD de itinerarios/actividades, subida de imágenes, mapa interactivo y modo oscuro persistente.


📌 Resumen rápido
-----------------
- Autenticación email/contraseña con Firebase Authentication; registro y login/redirección automática.
- Lista de itinerarios (Firestore) con creación/edición/eliminación, actividades asociadas y estado Borrador/Publicado.
- Detalle con imagen, fechas, actividades, conmutador de vista lista/mapa (Google Maps Compose).
- Selector y subida de imágenes a Firebase Storage directamente desde el formulario.
- Modo oscuro/ligero con Material 3; preferencia persistida en DataStore.
- Snackbar de feedback, validaciones de formularios y navegación segura con NavHost.


🧭 Flujo y rutas de navegación
------------------------------
- `login` → inicio de sesión; redirige a `home/{userId}` al autenticar.
- `register` → alta de usuario y redirección a `home/{userId}`.
- `home/{userId}` → lista de itinerarios del usuario; accesos a detalle, creación y ajustes.
- `create/{userId}` → formulario de nuevo itinerario.
- `detail/{userId}/{itineraryId}` → detalle con actividades, mapa y opciones de edición/eliminación.
- `edit/{userId}/{itineraryId}` → edición de itinerario existente.
- `settings` → perfil, toggle de modo oscuro y logout.
El NavHost valida que el `userId` solicitado coincida con el usuario autenticado y, si no, reenvía al destino correcto o a `login`.


🛠️ Stack principal
------------------
- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose
- Firebase: Authentication, Firestore, Storage
- DataStore (preferencias) para modo oscuro
- Coil para carga de imágenes
- Google Maps Compose


📂 Estructura relevante
-----------------------
- `app/src/main/java/com/example/proyecto1_plataformasmoviles_domingazo/MainActivity.kt` – arranque, tema y NavGraph.
- `ui/navigation/NavGraph.kt` – rutas y validaciones de sesión.
- `ui/login`, `ui/register` – auth con Firebase.
- `ui/home` – listado de itinerarios.
- `ui/itinerary` – detalle, formulario, actividades y selector de imágenes.
- `ui/settings` – perfil, modo oscuro y logout.
- `ui/theme` – colores, tipografía y persistencia de modo oscuro (DataStore).


🚀 Cómo correr el proyecto
--------------------------
1) Requisitos
   - Android Studio Iguana o superior.
   - JDK 17.
   - Dispositivo/emulador Android 10+.

2) Clonar
```bash
git clone https://github.com/MrMenth0l/Domingazo.git
cd Domingazo2
```

3) Configurar Firebase
   - Crea un proyecto en Firebase y habilita Authentication (Email/Password), Firestore y Storage.
   - Descarga `google-services.json` y colócalo en `app/google-services.json` (el archivo ya está versionado; actualízalo si usas otro proyecto).

4) Sincronizar y compilar
   - Abre el proyecto en Android Studio y sincroniza Gradle, o bien ejecuta:
```bash
./gradlew assembleDebug
```

5) Ejecutar
   - Desde Android Studio, selecciona un dispositivo/emulador y pulsa Run.


🌙 Modo oscuro
--------------
- El modo oscuro se controla desde `Settings`. La preferencia se guarda en DataStore y se aplica en el arranque siguiente.
- La paleta se normalizó en `ui/theme/Color.kt` y se expone vía `MaterialTheme.colorScheme`.


🔐 Notas de seguridad
---------------------
- Firestore/Storage requieren reglas adecuadas para proteger los datos por usuario. Ajusta las reglas en Firebase Console antes de publicar.
- No compartas `google-services.json` de entornos productivos.


🧪 Pruebas rápidas
------------------
- Login/Register: usa un correo de prueba y verifica redirección a Home.
- Crear/editar itinerario: ingresa fechas válidas (`yyyy-MM-dd`), sube una imagen y guarda; debería aparecer en la lista.
- Detalle: alterna lista/mapa y prueba eliminar itinerario; debe volver a `home/{userId}`.
- Modo oscuro: activa en Settings y reinicia la app para comprobar persistencia.


🧭 Solución de problemas
------------------------
- **Error de destino de navegación/deeplink**: si se abre con un `userId` distinto al autenticado, NavHost redirige automáticamente a `home/{uid}` o `login`.
- **Fallos de gradle sync**: verifica versión de JDK (17) y que el plugin de Google Services esté aplicado tras actualizar `google-services.json`.
- **Carga de imágenes**: requiere permisos de lectura de galería en el dispositivo y reglas de Storage que permitan la subida autenticada.


👥 Autores
----------
- Diego Quan
- Javier Alvarado
- Yehosua Hércules

Universidad del Valle de Guatemala – Curso Plataformas Móviles (Ciclo 2, 2025)
