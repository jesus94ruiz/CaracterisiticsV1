package com.jera.caracterisiticsv1.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.AuthViewModel

// ─── Colores usados en SettingsScreen ───────────────────────────────────────
// Fondo:              CyberDark      (#110015)
// Header bar:         SurfaceColor   (#1a0020)
// Título:             CyberYellow    (#fff04c)
// Ítem fondo:         SurfaceColor   (#1a0020)
// Ítem borde:         AccentPrimary  (#ff7037) 20% alpha
// Ítem texto:         CyberWhite     (#ffffff)
// Ítem hover/index:   NeonPurple     (#a3306f) / AccentOrange (#ff9b3e)
// Separador:          AccentPrimary  (#ff7037) 30% alpha
// ────────────────────────────────────────────────────────────────────────────

private enum class SettingDialog {
    PERMISOS,
    UBICACION_FOTOS,
    YA_LOGUEADO,
    CERRAR_SESION,
    INFORMACION,
    REFERENCIA_API,
    CREDITOS
}

@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()
    var activeDialog by remember { mutableStateOf<SettingDialog?>(null) }

    val settingsOptions = listOf(
        "Permisos",
        "Ubicación para guardar fotos",
        "Iniciar sesión",
        "Cerrar sesión",
        "Información",
        "Referencia API IA",
        "Créditos"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CyberDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceColor)
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = "// AJUSTES",
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            // Línea separadora neón
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AccentPrimary.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(settingsOptions) { index, item ->
                    SettingItem(
                        text = item,
                        index = index,
                        onClick = {
                            when (item) {
                                "Permisos" -> activeDialog = SettingDialog.PERMISOS
                                "Ubicación para guardar fotos" -> activeDialog = SettingDialog.UBICACION_FOTOS
                                "Iniciar sesión" -> {
                                    if (uiState.isLoggedIn) {
                                        activeDialog = SettingDialog.YA_LOGUEADO
                                    } else {
                                        navController.navigate(AppScreens.LoginScreen.route)
                                    }
                                }
                                "Cerrar sesión" -> activeDialog = SettingDialog.CERRAR_SESION
                                "Información" -> activeDialog = SettingDialog.INFORMACION
                                "Referencia API IA" -> activeDialog = SettingDialog.REFERENCIA_API
                                "Créditos" -> activeDialog = SettingDialog.CREDITOS
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    // ── Diálogos ─────────────────────────────────────────────────────────────
    when (activeDialog) {
        SettingDialog.PERMISOS -> PermisosDialog(onDismiss = { activeDialog = null })
        SettingDialog.UBICACION_FOTOS -> UbicacionFotosDialog(onDismiss = { activeDialog = null })
        SettingDialog.YA_LOGUEADO -> YaLogueadoDialog(
            userName = uiState.user?.displayName ?: uiState.user?.email ?: "Usuario",
            onDismiss = { activeDialog = null }
        )
        SettingDialog.CERRAR_SESION -> CerrarSesionDialog(
            onConfirm = {
                authViewModel.signOut()
                activeDialog = null
                navController.navigate(AppScreens.LoginScreen.route) {
                    popUpTo(AppScreens.MainScreen.route) { inclusive = true }
                }
            },
            onDismiss = { activeDialog = null }
        )
        SettingDialog.INFORMACION -> InformacionDialog(onDismiss = { activeDialog = null })
        SettingDialog.REFERENCIA_API -> ReferenciaApiDialog(onDismiss = { activeDialog = null })
        SettingDialog.CREDITOS -> CreditosDialog(onDismiss = { activeDialog = null })
        null -> Unit
    }
}

// ─── Item de ajuste ──────────────────────────────────────────────────────────

@Composable
fun SettingItem(text: String, index: Int = 0, onClick: () -> Unit = {}) {
    val accentColor = if (index % 2 == 0) AccentPrimary else NeonPurple

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceColor)
            .border(
                width = 1.dp,
                color = AccentPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .background(color = accentColor, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = CyberWhite,
            fontFamily = Poppins,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = ">",
            color = AccentOrange.copy(alpha = 0.6f),
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

// ─── Diálogo base con estilo cyberpunk ───────────────────────────────────────

@Composable
private fun CyberDialog(
    title: String,
    onDismiss: () -> Unit,
    confirmLabel: String = "Cerrar",
    onConfirm: (() -> Unit)? = null,
    confirmColor: Color = AccentPrimary,
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .border(1.dp, AccentPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        title = {
            Text(
                text = title,
                color = CyberYellow,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm ?: onDismiss) {
                Text(
                    text = confirmLabel,
                    color = confirmColor,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = if (onConfirm != null) {
            {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancelar",
                        color = TextSecondary,
                        fontFamily = Poppins,
                        fontSize = 13.sp
                    )
                }
            }
        } else null,
        containerColor = SurfaceColor,
        titleContentColor = CyberYellow,
        textContentColor = CyberWhite
    )
}

// ─── 1. Permisos ─────────────────────────────────────────────────────────────

@Composable
private fun PermisosDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    val permisos = listOf(
        Triple(
            "Cámara",
            Manifest.permission.CAMERA,
            "Necesaria para escanear coches en tiempo real."
        ),
        Triple(
            "Ubicación",
            Manifest.permission.ACCESS_FINE_LOCATION,
            "Usada para marcar en el mapa dónde se capturó cada coche."
        ),
        Triple(
            "Almacenamiento (lectura)",
            Manifest.permission.READ_EXTERNAL_STORAGE,
            "Permite importar fotos desde la galería del dispositivo."
        )
    )

    CyberDialog(
        title = "// PERMISOS",
        onDismiss = onDismiss,
        confirmLabel = "Ir a Ajustes",
        onConfirm = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
            onDismiss()
        },
        confirmColor = AccentPrimary
    ) {
        Text(
            text = "La app requiere los siguientes permisos para funcionar correctamente:",
            color = TextSecondary,
            fontFamily = Poppins,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))

        permisos.forEach { (nombre, permiso, descripcion) ->
            val concedido = ContextCompat.checkSelfPermission(
                context, permiso
            ) == PackageManager.PERMISSION_GRANTED

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberDark)
                    .border(
                        1.dp,
                        if (concedido) NeonGreen.copy(alpha = 0.4f) else ErrorColor.copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Indicador de estado
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .size(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (concedido) NeonGreen else ErrorColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = nombre,
                        color = CyberWhite,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = descripcion,
                        color = CyberWhite.copy(alpha = 0.65f),
                        fontFamily = Poppins,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Text(
                        text = if (concedido) "✓ Concedido" else "✗ No concedido",
                        color = if (concedido) NeonGreen else ErrorColor,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Text(
            text = "Pulsa 'Ir a Ajustes' para gestionar los permisos en la configuración del sistema.",
            color = CyberWhite.copy(alpha = 0.5f),
            fontFamily = Poppins,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    }
}

// ─── 2. Ubicación para guardar fotos ────────────────────────────────────────

@Composable
private fun UbicacionFotosDialog(onDismiss: () -> Unit) {
    CyberDialog(
        title = "// UBICACIÓN DE FOTOS",
        onDismiss = onDismiss
    ) {
        InfoRow(
            emoji = "📱",
            title = "Almacenamiento interno de la app",
            description = "Las fotos capturadas con la cámara se guardan en el almacenamiento privado de la app (filesDir/captured_cars/). No aparecen en la galería del sistema, pero son accesibles desde la pantalla Galería dentro de la app."
        )
        InfoRow(
            emoji = "💾",
            title = "Almacenamiento externo (exportación)",
            description = "Al guardar manualmente una captura, se copia a la carpeta privada de la app en el almacenamiento externo (Android/data/com.jera.caracterisiticsv1/files/Pictures/)."
        )
        InfoRow(
            emoji = "☁️",
            title = "Firebase Storage",
            description = "Si tienes sesión iniciada, las imágenes del Showcase se sincronizan en la nube y son accesibles desde cualquier dispositivo."
        )
        InfoRow(
            emoji = "🔒",
            title = "Privacidad",
            description = "Las fotos son privadas por defecto. Solo las imágenes que compartes explícitamente en el Showcase son visibles para otros usuarios."
        )
    }
}

// ─── 3. Ya logueado ──────────────────────────────────────────────────────────

@Composable
private fun YaLogueadoDialog(userName: String, onDismiss: () -> Unit) {
    CyberDialog(
        title = "// SESIÓN ACTIVA",
        onDismiss = onDismiss
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberDark)
                .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "✓", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Sesión iniciada como:",
                    color = CyberWhite.copy(alpha = 0.65f),
                    fontFamily = Poppins,
                    fontSize = 11.sp
                )
                Text(
                    text = userName,
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        Text(
            text = "Tu progreso, colección y logros están sincronizados en la nube. Para cambiar de cuenta, cierra sesión primero.",
            color = CyberWhite.copy(alpha = 0.6f),
            fontFamily = Poppins,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
    }
}

// ─── 4. Cerrar sesión ────────────────────────────────────────────────────────

@Composable
private fun CerrarSesionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    CyberDialog(
        title = "// CERRAR SESIÓN",
        onDismiss = onDismiss,
        confirmLabel = "Cerrar sesión",
        onConfirm = onConfirm,
        confirmColor = ErrorColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberDark)
                .border(1.dp, ErrorColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "⚠", color = ErrorColor, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "¿Seguro que quieres cerrar sesión?",
                color = CyberWhite,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Text(
            text = "Tu progreso local se conserva en el dispositivo, pero necesitarás volver a iniciar sesión para sincronizar datos con la nube y acceder al modo social.",
            color = CyberWhite.copy(alpha = 0.6f),
            fontFamily = Poppins,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
    }
}

// ─── 5. Información ──────────────────────────────────────────────────────────

@Composable
private fun InformacionDialog(onDismiss: () -> Unit) {
    CyberDialog(
        title = "// INFORMACIÓN",
        onDismiss = onDismiss
    ) {
        // Cabecera de la app
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberDark)
                .border(1.dp, AccentPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Caracteristics",
                color = AccentPrimary,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 3.sp
            )
            Text(
                text = "// GARAGE SOCIAL",
                color = CyberYellow,
                fontFamily = Poppins,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Versión 1.0",
                color = CyberWhite.copy(alpha = 0.5f),
                fontFamily = Poppins,
                fontSize = 11.sp
            )
        }

        Text(
            text = "¿Qué es Caracteristics?",
            color = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Text(
            text = "Caracteristics es una aplicación de reconocimiento de vehículos mediante inteligencia artificial. Apunta la cámara a cualquier coche, y la IA identificará su marca y modelo al instante.",
            color = CyberWhite.copy(alpha = 0.8f),
            fontFamily = Poppins,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        Text(
            text = "Características principales",
            color = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        val features = listOf(
            "🔍  Reconocimiento de modelos por IA",
            "🏎️  Fichas técnicas detalladas de cada vehículo",
            "🗺️  Mapa de capturas geolocalizadas",
            "🏆  Sistema de XP, niveles y logros",
            "🚗  Garaje personal con tu colección",
            "👥  Leaderboard y modo social",
            "📸  Galería de fotos de tus capturas"
        )
        features.forEach { feature ->
            Text(
                text = feature,
                color = CyberWhite.copy(alpha = 0.75f),
                fontFamily = Poppins,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// ─── 6. Referencia API IA ────────────────────────────────────────────────────

@Composable
private fun ReferenciaApiDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    CyberDialog(
        title = "// REFERENCIA API IA",
        onDismiss = onDismiss
    ) {
        Text(
            text = "Caracteristics utiliza las siguientes APIs externas para ofrecer reconocimiento de vehículos, imágenes de referencia y fichas técnicas:",
            color = TextSecondary,
            fontFamily = Poppins,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        // API 1 – Carnet.ai
        ApiCard(
            emoji = "🤖",
            name = "Carnet.ai",
            url = "https://carnet.ai/",
            accentColor = AccentPrimary,
            description = "Motor de reconocimiento de vehículos mediante inteligencia artificial. A partir de la foto tomada con la cámara, identifica la marca, modelo y año del coche con alta precisión.",
            onOpenUrl = { uriHandler.openUri("https://carnet.ai/") }
        )

        // API 2 – CarImagesAPI
        ApiCard(
            emoji = "🖼️",
            name = "CarImages API",
            url = "https://carimagesapi.com/",
            accentColor = NeonPurple,
            description = "Proporciona imágenes de referencia de alta calidad para los vehículos detectados. Permite mostrar una foto oficial del modelo junto a los resultados del reconocimiento.",
            onOpenUrl = { uriHandler.openUri("https://carimagesapi.com/") }
        )

        // API 3 – CarSpecsAPI
        ApiCard(
            emoji = "📋",
            name = "CarSpecs API",
            url = "https://carspecsapi.com/",
            accentColor = CyberYellow,
            description = "Fuente de especificaciones técnicas detalladas: motor, potencia, transmisión, consumo, dimensiones y más. Alimenta las fichas técnicas que se muestran en la pantalla de resultados.",
            onOpenUrl = { uriHandler.openUri("https://carspecsapi.com/") }
        )
    }
}

// ─── 7. Créditos ─────────────────────────────────────────────────────────────

@Composable
private fun CreditosDialog(onDismiss: () -> Unit) {
    CyberDialog(
        title = "// CRÉDITOS",
        onDismiss = onDismiss
    ) {
        // Autor
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberDark)
                .border(1.dp, AccentPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "Desarrollado por",
                color = CyberWhite.copy(alpha = 0.5f),
                fontFamily = Poppins,
                fontSize = 11.sp
            )
            Text(
                text = "Jesús Ruiz",
                color = CyberYellow,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Trabajo de Fin de Grado · 2026",
                color = AccentPrimary,
                fontFamily = Poppins,
                fontSize = 11.sp
            )
        }

        Text(
            text = "Tecnologías utilizadas",
            color = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        val techs = listOf(
            Pair("📱  Android + Jetpack Compose", "UI declarativa nativa"),
            Pair("💉  Hilt / Dagger", "Inyección de dependencias"),
            Pair("🔥  Firebase Auth & Firestore", "Autenticación y base de datos en la nube"),
            Pair("☁️  Firebase Storage", "Almacenamiento de imágenes en la nube"),
            Pair("🌐  Retrofit + OkHttp", "Llamadas a APIs REST"),
            Pair("🗄️  Room Database", "Persistencia local"),
            Pair("🗺️  Google Maps SDK", "Mapas y geolocalización"),
            Pair("📷  CameraX", "Captura de imágenes en tiempo real"),
            Pair("🤖  Carnet.ai", "Reconocimiento de vehículos por IA"),
            Pair("🖼️  CarImages API", "Imágenes de referencia de vehículos"),
            Pair("📋  CarSpecs API", "Especificaciones técnicas de vehículos"),
            Pair("🔄  Coroutines + Flow", "Programación asíncrona reactiva"),
            Pair("🧭  Navigation Compose", "Navegación entre pantallas")
        )

        techs.forEach { (tech, desc) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberDark)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tech,
                        color = CyberWhite,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = desc,
                        color = CyberWhite.copy(alpha = 0.55f),
                        fontFamily = Poppins,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ─── Componentes auxiliares ──────────────────────────────────────────────────

@Composable
private fun InfoRow(emoji: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberDark)
            .border(1.dp, AccentPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = CyberWhite,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = description,
                color = CyberWhite.copy(alpha = 0.65f),
                fontFamily = Poppins,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun ApiCard(
    emoji: String,
    name: String,
    url: String,
    accentColor: Color,
    description: String,
    onOpenUrl: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberDark)
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Encabezado con emoji y nombre
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                color = accentColor,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        // Descripción
        Text(
            text = description,
            color = CyberWhite.copy(alpha = 0.7f),
            fontFamily = Poppins,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
        // URL clicable
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = accentColor,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 11.sp
                    )
                ) {
                    append(url)
                }
            },
            modifier = Modifier.clickable { onOpenUrl() },
            fontFamily = Poppins
        )
    }
}
