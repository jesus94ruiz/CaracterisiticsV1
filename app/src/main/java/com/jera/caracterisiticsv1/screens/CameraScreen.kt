package com.jera.caracterisiticsv1.screens

import android.content.Context
import android.view.ViewGroup
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.components.Analysing
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.utilities.ResourceState
import com.jera.caracterisiticsv1.viewmodels.CameraViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue

// ─── Colores usados en CameraScreen ────────────────────────────────────────
// Fondo visor:        CyberDark    (#110015) transparente sobre preview
// Botón disparo:      CyberYellow  (#fff04c) + borde AccentPrimary
// Botón retroceso:    SurfaceColor (#1a0020) + borde CyberMagenta
// Texto permiso:      CyberYellow
// Fondo permiso:      CyberDark
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavHostController, cameraViewModel: CameraViewModel = hiltViewModel()) {
    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val hasCameraPermission = cameraPermissionState.status.isGranted
    val requestPermissionState = cameraPermissionState::launchPermissionRequest

    val context = LocalContext.current
    val cameraController = remember { LifecycleCameraController(context) }
    val lifecycle = LocalLifecycleOwner.current

    val apiResponse by cameraViewModel.model.collectAsState(ResourceState.NotInitialized())

    if (hasCameraPermission) {
        when (apiResponse) {
            is ResourceState.NotInitialized -> {
                cameraContent(cameraController, lifecycle, context, navController, cameraViewModel)
            }
            is ResourceState.Loading -> {
                Analysing()
            }
            is ResourceState.Success -> {
                ResultsScreen(navController = navController, "camera")
            }
            is ResourceState.Error -> {
                ResultsScreen(navController = navController, "camera")
            }
        }
    } else {
        requestPermission(requestPermissionState)
    }
}

@Composable
private fun cameraContent(
    cameraController: LifecycleCameraController,
    lifecycle: LifecycleOwner,
    localContext: Context,
    navController: NavHostController,
    cameraViewModel: CameraViewModel
) {
    cameraController.bindToLifecycle(lifecycle)

    Box(modifier = Modifier.fillMaxSize()) {

        // Visor de cámara
        AndroidView(factory = { context ->
            PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }.also { it.controller = cameraController }
        })

        // Overlay HUD cyberpunk en esquinas
        CameraHudOverlay(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        )

        // Botón disparar
        FloatingActionButton(
            onClick = {
                val executor = ContextCompat.getMainExecutor(localContext)
                cameraViewModel.takePicture(cameraController, executor)
            },
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .border(2.dp, CyberYellow, CircleShape),
            backgroundColor = SurfaceColor,
            contentColor = CyberYellow,
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "Hacer foto",
                modifier = Modifier.size(32.dp),
                tint = CyberYellow
            )
        }

        // Botón retroceso
        FloatingActionButton(
            onClick = {
                navController.popBackStack()
                navController.navigate(AppScreens.MapScreen.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 16.dp)
                .height(44.dp)
                .width(64.dp)
                .border(1.dp, CyberMagenta, RoundedCornerShape(8.dp)),
            backgroundColor = SurfaceColor,
            contentColor = CyberMagenta,
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "Volver",
                tint = CyberMagenta
            )
        }
    }
}

/** Líneas decorativas en esquinas estilo HUD futurista. */
@Composable
private fun CameraHudOverlay(modifier: Modifier = Modifier) {
    val cornerColor = AccentPrimary.copy(alpha = 0.7f)
    val cornerSize = 24.dp
    val strokeWidth = 2.dp

    Box(modifier = modifier) {
        // Esquina superior izquierda
        Box(
            Modifier
                .align(Alignment.TopStart)
                .width(cornerSize)
                .height(strokeWidth)
                .background(cornerColor)
        )
        Box(
            Modifier
                .align(Alignment.TopStart)
                .width(strokeWidth)
                .height(cornerSize)
                .background(cornerColor)
        )
        // Esquina superior derecha
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .width(cornerSize)
                .height(strokeWidth)
                .background(cornerColor)
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .width(strokeWidth)
                .height(cornerSize)
                .background(cornerColor)
        )
        // Esquina inferior izquierda
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .width(cornerSize)
                .height(strokeWidth)
                .background(cornerColor)
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .width(strokeWidth)
                .height(cornerSize)
                .background(cornerColor)
        )
        // Esquina inferior derecha
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .width(cornerSize)
                .height(strokeWidth)
                .background(cornerColor)
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .width(strokeWidth)
                .height(cornerSize)
                .background(cornerColor)
        )
    }
}

@Composable
private fun requestPermission(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .background(CyberDark)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 32.dp),
            text = "Por favor conceda los permisos necesarios para poder utilizar la cámara",
            textAlign = TextAlign.Center,
            color = CyberYellow,
            fontFamily = Poppins,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .height(48.dp)
                .width(220.dp)
                .border(1.dp, AccentPrimary, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(backgroundColor = SurfaceColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Conceder permisos",
                color = CyberYellow,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
