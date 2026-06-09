package com.jera.caracterisiticsv1.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.ShowcaseViewModel

@Composable
fun ShowcaseScreen(
    navController: NavController,
    viewModel: ShowcaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar atrás tras guardar con éxito
    LaunchedEffect(uiState.savedSuccess) {
        if (uiState.savedSuccess) {
            viewModel.clearSavedSuccess()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDark)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceColor)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = AccentPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "// EXPOSITOR",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AccentPrimary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Elige hasta 3 coches para mostrar · ${uiState.selectedCarIds.size}/3 seleccionados",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            // Botón Guardar
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    color = CyberYellow,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(28.dp).padding(end = 8.dp)
                )
            } else {
                Button(
                    onClick = { viewModel.saveShowcase() },
                    enabled = uiState.selectedCarIds.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberYellow.copy(alpha = 0.15f),
                        contentColor = CyberYellow,
                        disabledContainerColor = SurfaceVariant,
                        disabledContentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("GUARDAR", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        // Línea divisoria
        Box(Modifier.fillMaxWidth().height(1.dp).background(AccentPrimary.copy(alpha = 0.4f)))

        // ── Error ──────────────────────────────────────────────────────────────
        uiState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x22FF4444))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = error,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color(0xFFFF6666)
                )
            }
        }

        // ── Contenido ─────────────────────────────────────────────────────────
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = AccentPrimary, strokeWidth = 2.dp, modifier = Modifier.size(40.dp))
                        Text("CARGANDO TU GARAJE...", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CyberYellow, letterSpacing = 2.sp)
                    }
                }
            }

            uiState.allCars.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(painterResource(R.drawable.car_in_garage), null, tint = AccentPrimary.copy(0.4f), modifier = Modifier.size(56.dp))
                        Text("Aún no tienes coches", fontFamily = Poppins, fontSize = 13.sp, color = TextSecondary)
                        Text("¡Captura tu primer vehículo!", fontFamily = Poppins, fontSize = 12.sp, color = AccentPrimary)
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.allCars, key = { it.id }) { car ->
                        ShowcaseCarCard(
                            car = car,
                            isSelected = uiState.selectedCarIds.contains(car.id),
                            onClick = { viewModel.toggleCarSelection(car.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowcaseCarCard(
    car: CarModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val ctx = LocalContext.current
    val shape = RoundedCornerShape(10.dp)
    val borderColor = if (isSelected) CyberYellow else SurfaceLight.copy(alpha = 0.2f)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (isSelected) CyberYellow.copy(alpha = 0.1f) else SurfaceColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(SurfaceVariant)
            ) {
                if (!car.path.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(ctx).data(Uri.parse(car.path)).crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.car_in_garage),
                        contentDescription = null,
                        tint = SurfaceLight.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp).align(Alignment.Center)
                    )
                }

                // Indicador de selección
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(CyberYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = CyberDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Column(Modifier.padding(horizontal = 6.dp, vertical = 5.dp)) {
                Text(
                    text = car.model_name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isSelected) CyberYellow else TextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = car.make_name,
                    fontFamily = Poppins,
                    fontSize = 9.sp,
                    color = AccentPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
