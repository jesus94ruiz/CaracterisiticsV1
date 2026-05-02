package com.jera.caracterisiticsv1.screens

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.components.Analysing
import com.jera.caracterisiticsv1.ui.components.ImageCard
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.utilities.ResourceState
import com.jera.caracterisiticsv1.viewmodels.CameraViewModel

// ─── Colores usados en GalleryScreen ───────────────────────────────────────
// Fondo:              CyberDark    (#110015)
// Textos principales: CyberYellow  (#fff04c)
// Textos secundarios: NeonAmber    (#ffc545)
// Botones fondo:      SurfaceColor (#1a0020)
// Botones borde:      AccentPrimary (#ff7037)
// Botón analizar:     CyberYellow borde
// Íconos tint:        AccentPrimary
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun GalleryScreen(
    navController: NavHostController,
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    val apiResponse by cameraViewModel.model.collectAsState(ResourceState.NotInitialized())
    val googleApiResponse by cameraViewModel.model.collectAsState(ResourceState.NotInitialized())

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { selectedImageUri = it }
    )

    when (googleApiResponse) {
        is ResourceState.NotInitialized -> {
            if (selectedImageUri == null) {
                LaunchedEffect(Unit) {
                    pickPhotoLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }
            GalleryContent(pickPhotoLauncher, selectedImageUri, navController)
        }
        is ResourceState.Loading -> {
            Analysing()
        }
        is ResourceState.Success -> {
            ResultsScreen(navController = navController, "gallery")
        }
        is ResourceState.Error -> {
            ResultsScreen(navController = navController, "gallery")
        }
    }
}

@Composable
fun GalleryContent(
    pickPhotoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    selectedImageUri: Uri?,
    navController: NavHostController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CyberDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedImageUri == null) {
                // Sin imagen seleccionada
                Text(
                    text = "¿Analizar una imagen de la galería?",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                CyberButton(
                    label = "Abrir Galería",
                    width = 180.dp,
                    borderColor = AccentPrimary,
                    onClick = {
                        pickPhotoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            } else {
                // Con imagen seleccionada
                Text(
                    text = "¿Analizar esta imagen?",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Imagen con borde cyberpunk
                Box(
                    modifier = Modifier
                        .border(1.dp, AccentPrimary, RoundedCornerShape(8.dp))
                ) {
                    ImageCard(selectedImageUri.toString())
                }
                Spacer(modifier = Modifier.height(16.dp))
                confirmationButtons(pickPhotoLauncher, selectedImageUri!!)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Separador
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(1.dp)
                    .background(SurfaceLight)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "¿O volver al mapa?",
                color = NeonAmber,
                fontFamily = Poppins,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate(AppScreens.MapScreen.route) },
                modifier = Modifier
                    .height(48.dp)
                    .width(140.dp)
                    .border(1.dp, CyberMagenta, RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(backgroundColor = SurfaceColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Mapa",
                    modifier = Modifier.size(18.dp),
                    tint = CyberMagenta
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Mapa",
                    color = CyberMagenta,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun confirmationButtons(
    pickPhotoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    selectedImageUri: Uri,
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CyberButton(
            label = "Otra foto",
            width = 130.dp,
            borderColor = SurfaceLight,
            onClick = {
                pickPhotoLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
        CyberButton(
            label = "Analizar",
            width = 130.dp,
            borderColor = CyberYellow,
            textColor = CyberYellow,
            onClick = {
                cameraViewModel.getModelfromGallery(context, selectedImageUri)
            }
        )
    }
}

/** Botón reutilizable con estilo cyberpunk. */
@Composable
private fun CyberButton(
    label: String,
    width: androidx.compose.ui.unit.Dp,
    borderColor: androidx.compose.ui.graphics.Color = AccentPrimary,
    textColor: androidx.compose.ui.graphics.Color = CyberYellow,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(52.dp)
            .width(width)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(backgroundColor = SurfaceColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 14.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
