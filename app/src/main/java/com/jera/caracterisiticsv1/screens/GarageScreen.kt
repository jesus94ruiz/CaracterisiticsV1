package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.ui.components.GarageCard
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.GarageViewModel

// ─── Colores usados en GarageScreen ────────────────────────────────────────
// Fondo:              CyberDark    (#110015)
// Título:             CyberYellow  (#fff04c)
// Separador:          AccentPrimary (#ff7037) con alpha
// Grid items:         GarageCard (ya estilizado)
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun GarageScreen(
    navController: NavHostController,
    garageViewModel: GarageViewModel = hiltViewModel()
) {
    val models by garageViewModel.models.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CyberDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera cyberpunk
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceColor)
                    .border(
                        width = 1.dp,
                        color = AccentPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
                    )
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = "// GARAGE",
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "${models.size} UNIDADES",
                    color = NeonAmber.copy(alpha = 0.7f),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            // Línea separadora neón
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AccentPrimary.copy(alpha = 0.6f))
            )
            if (models.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GARAGE VACÍO\nCaptura tu primer vehículo",
                        color = SurfaceLight,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(all = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    columns = GridCells.Fixed(2),
                ) {
                    items(models) {
                        GarageCard(model = it)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GaragePreview() {
    val car1 = CarModel(make_name = "Porsche", model_name = "911", years = "1963-1973", probability = 0.9996, path = "")
    val car2 = CarModel(make_name = "Ferrari", model_name = "F40", years = "1987-1992", probability = 0.9985, path = "")
    val car3 = CarModel(make_name = "Lamborghini", model_name = "Countach", years = "1974-1990", probability = 0.9970, path = "")
    val car4 = CarModel(make_name = "McLaren", model_name = "F1", years = "1992-1998", probability = 0.9960, path = "")
    val models = listOf(car1, car2, car3, car4)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CyberDark
    ) {
        LazyVerticalGrid(
            modifier = Modifier,
            contentPadding = PaddingValues(all = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            columns = GridCells.Fixed(2),
        ) {
            items(models) {
                GarageCard(model = it)
            }
        }
    }
}
