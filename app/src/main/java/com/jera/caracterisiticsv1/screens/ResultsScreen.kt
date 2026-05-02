package com.jera.caracterisiticsv1.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jera.caracterisiticsv1.viewmodels.CameraViewModel
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected
import com.jera.caracterisiticsv1.ui.components.ModelDetectedComponent
import com.jera.caracterisiticsv1.utilities.ResourceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.components.Analysing
import com.jera.caracterisiticsv1.ui.theme.*

// ─── Colores usados en ResultsScreen ───────────────────────────────────────
// Fondo surface:      CyberDark    (#110015)
// Textos principales: CyberYellow  (#fff04c)
// Textos error:       NeonOrange   (#ff9b3e)
// Botones fondo:      SurfaceColor (#1a0020)
// Botones borde:      AccentPrimary (#ff7037)
// Botón acción:       borde CyberYellow
// Íconos tint:        AccentPrimary / CyberMagenta
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResultsScreen(
    navController: NavHostController,
    origin: String,
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    val modelsDetected by cameraViewModel.modelsDetected.collectAsState(ResourceState.NotInitialized())
    val pageCount: Int = if (cameraViewModel.pageCount == 0) 1 else cameraViewModel.pageCount
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    )

    VerticalPager(
        state = pagerState,
        pageSize = PageSize.Fill,
        pageSpacing = 8.dp,
        pageCount = pageCount
    ) { page: Int ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = CyberDark
        ) {
            when (modelsDetected) {
                is ResourceState.NotInitialized, is ResourceState.Loading -> Analysing()
                is ResourceState.Success -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if ((modelsDetected as ResourceState.Success).data.isNotEmpty() && page <= pageCount)
                            ResultsContent(
                                model = (modelsDetected as ResourceState.Success).data[page],
                                navController,
                                origin
                            )
                        if (page < pageCount - 1)
                            moreResults()
                    }
                }
                is ResourceState.Error -> {
                    NoResultsContent(
                        error = (modelsDetected as ResourceState.Error).error,
                        navController,
                        origin
                    )
                }
            }
        }
    }
}

@Composable
fun ResultsContent(
    model: ModelDetected,
    navController: NavHostController,
    origin: String,
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.padding(36.dp)) {
        // Separador superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AccentPrimary.copy(alpha = 0.4f))
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "¡Modelo encontrado!",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        ModelDetectedComponent(model)
        Spacer(modifier = Modifier.height(40.dp))
        // Separador
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SurfaceLight)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "¿Qué deseas hacer?",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = NeonAmber,
            fontFamily = Poppins,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        ButtonRow(navController, model, origin)
    }
}

@Composable
fun ButtonRow(
    navController: NavHostController,
    model: ModelDetected,
    origin: String,
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionsGranted = remember { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted.value =
            permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CyberActionButton(
            text = "Mapa",
            icon = painterResource(id = R.drawable.map),
            borderColor = AccentPrimary,
            onClick = {
                navController.popBackStack()
                navController.navigate(AppScreens.MapScreen.route)
                cameraViewModel.resetResourceStates()
            }
        )
        CyberActionButton(
            text = "Repetir",
            icon = painterResource(id = R.drawable.reload),
            borderColor = NeonAmber,
            onClick = {
                if (origin == "camera") {
                    navController.popBackStack()
                    navController.navigate(AppScreens.CameraScreen.route)
                } else {
                    navController.popBackStack()
                    navController.navigate(AppScreens.GalleryScreen.route)
                }
                cameraViewModel.resetResourceStates()
            }
        )
        CyberActionButton(
            text = "Guardar",
            icon = painterResource(id = R.drawable.car_in_garage),
            borderColor = CyberYellow,
            onClick = {
                if (cameraViewModel.checkAndRequestPermissions(context, permissionsLauncher)) {
                    cameraViewModel.saveFileToStorage(model.file, context)
                    cameraViewModel.insertModel(model)
                    val originRoute = if (origin == "gallery") AppScreens.GalleryScreen.route
                    else AppScreens.CameraScreen.route
                    navController.navigate(AppScreens.MapScreen.route) {
                        popUpTo(originRoute) { inclusive = true }
                    }
                    navController.navigate(AppScreens.GarageScreen.route)
                    cameraViewModel.resetResourceStates()
                }
            }
        )
    }
}

/** Botón de acción con ícono estilo cyberpunk */
@Composable
fun CyberActionButton(
    text: String,
    icon: Painter,
    borderColor: androidx.compose.ui.graphics.Color = AccentPrimary,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .size(90.dp, 68.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(backgroundColor = SurfaceColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = borderColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = borderColor,
                fontSize = 10.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Alias para compatibilidad con llamadas anteriores
@Composable
fun SmallButtonWithIcon(text: String, icon: Painter, onClick: () -> Unit) {
    CyberActionButton(text = text, icon = icon, onClick = onClick)
}

@Composable
fun NoResultsContent(
    error: String,
    navController: NavHostController,
    origin: String,
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Borde superior de error
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(CyberRed)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error al detectar el modelo",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Error: $error",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = NeonOrange,
            fontFamily = Poppins,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Icon(
            painter = painterResource(id = R.drawable.flat_tire),
            contentDescription = "NotFound",
            modifier = Modifier.size(280.dp),
            tint = CyberMagenta
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SurfaceLight)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "¿Qué deseas hacer?",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = NeonAmber,
            fontFamily = Poppins,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CyberActionButton(
                text = "Mapa",
                icon = painterResource(id = R.drawable.home),
                borderColor = AccentPrimary,
                onClick = {
                    navController.popBackStack()
                    navController.navigate(AppScreens.MapScreen.route)
                    cameraViewModel.resetResourceStates()
                }
            )
            CyberActionButton(
                text = "Repetir",
                icon = painterResource(id = R.drawable.reload),
                borderColor = CyberYellow,
                onClick = {
                    if (origin == "camera") {
                        navController.popBackStack()
                        navController.navigate(AppScreens.CameraScreen.route)
                    } else {
                        navController.popBackStack()
                        navController.navigate(AppScreens.GalleryScreen.route)
                    }
                    cameraViewModel.resetResourceStates()
                }
            )
        }
    }
}

@Composable
fun moreResults() {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = "Otros modelos encontrados",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = NeonAmber.copy(alpha = 0.6f),
            fontFamily = Poppins,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Arrow Down",
            tint = AccentPrimary.copy(alpha = 0.6f),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}
