Domingazo – Proyecto de Plataformas Móviles

Descripción

Domingazo es una aplicación móvil desarrollada en Kotlin con Jetpack Compose, cuyo objetivo es ayudar a los usuarios a organizar sus itinerarios de viajes o escapadas de fin de semana de forma rápida, visual y colaborativa.

Esta versión corresponde a la Primera Entrega del Proyecto (UI Screens) del curso Plataformas Móviles – Universidad del Valle de Guatemala.


Características principales

Interfaz 100% construida con Jetpack Compose.

Jetpack Navigation para la navegación entre pantallas.

Arquitectura base MVI (Model – View – Intent) con ViewModel y uiState.


Pantallas:

Home – Lista de itinerarios creados con diseño visual tipo tarjetas.

Itinerary Detail – Detalle completo con timeline, actividades, mapa y propuestas generadas por IA.

Settings – Perfil del usuario y preferencias básicas.

Diseño moderno con Material Design 3, colores personalizados y modo oscuro.

Uso de mock data para simular datos reales de itinerarios y actividades.


Estructura del proyecto

MainActivity
↓
NavGraph
├── HomeScreen
├── ItineraryScreen
└── SettingsScreen

ItineraryViewModel gestiona el estado (uiState) y la simulación de datos.

ItineraryScreen.kt implementa la UI detallada del itinerario.

La carpeta theme/ contiene los esquemas de color, tipografía y estilos.

La sección mockData simula la información del usuario, itinerarios y actividades.


Tecnologías utilizadas

Kotlin

Jetpack Compose

Jetpack Navigation

Material Design 3

ViewModel / State Management

Android Studio Giraffe+


Cómo ejecutar

Clonar el repositorio:
git clone https://github.com/dquan123/Domingazo.git

Abrir el proyecto en Android Studio.

Ejecutar en un emulador o dispositivo físico con Android 10 o superior.

La pantalla inicial muestra los itinerarios y permite navegar al detalle o ajustes.



Autores

Diego Quan
Javier Alvarado
Yehosua Hércules
Universidad del Valle de Guatemala
Curso: Plataformas Móviles – Ciclo 2, 2025


Estado actual

Esta versión corresponde a la fase de interfaz (UI Screens).
Las siguientes fases incluirán integración con APIs reales, almacenamiento local (Room/DataStore) y autenticación de usuarios.
