package com.jera.caracterisiticsv1.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.ui.components.BrandCard
import com.jera.caracterisiticsv1.ui.components.BrandInfo
import com.jera.caracterisiticsv1.ui.components.GarageCard
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.CarSortOrder
import com.jera.caracterisiticsv1.viewmodels.GarageSortOrder
import com.jera.caracterisiticsv1.viewmodels.GarageViewModel

// ────────────────────────────────────────────────────────────────────────────
//  GarageScreen  –  dos niveles: Marcas → Coches de la marca
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun GarageScreen(
    navController: NavHostController,
    garageViewModel: GarageViewModel = hiltViewModel()
) {
    val selectedBrand    by garageViewModel.selectedBrand.collectAsState()
    val brands           by garageViewModel.brands.collectAsState()
    val brandSearchQuery by garageViewModel.brandSearchQuery.collectAsState()
    val brandSortOrder   by garageViewModel.brandSortOrder.collectAsState()
    val cars             by garageViewModel.carsForSelectedBrand.collectAsState()
    val carSearchQuery   by garageViewModel.carSearchQuery.collectAsState()
    val carSortOrder     by garageViewModel.carSortOrder.collectAsState()
    val allModels        by garageViewModel.models.collectAsState()

    // Volver atrás desde el nivel de coches
    BackHandler(enabled = selectedBrand != null) {
        garageViewModel.clearSelectedBrand()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = CyberDark) {
        Crossfade(
            targetState = selectedBrand,
            label = "garage_level"
        ) { brand ->
            if (brand == null) {
                // ── Nivel 1: Cuadrícula de marcas ────────────────────────────
                BrandsLevel(
                    brands          = brands,
                    totalCars       = allModels.size,
                    searchQuery     = brandSearchQuery,
                    sortOrder       = brandSortOrder,
                    onSearchChange  = { garageViewModel.setBrandSearchQuery(it) },
                    onSortChange    = { garageViewModel.setBrandSortOrder(it) },
                    onBrandClick    = { garageViewModel.selectBrand(it) }
                )
            } else {
                // ── Nivel 2: Coches de la marca ──────────────────────────────
                CarsLevel(
                    brandName      = brand,
                    cars           = cars,
                    searchQuery    = carSearchQuery,
                    sortOrder      = carSortOrder,
                    onSearchChange = { garageViewModel.setCarSearchQuery(it) },
                    onSortChange   = { garageViewModel.setCarSortOrder(it) },
                    onBack         = { garageViewModel.clearSelectedBrand() }
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
//  Nivel 1 – Marcas
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun BrandsLevel(
    brands: List<BrandInfo>,
    totalCars: Int,
    searchQuery: String,
    sortOrder: GarageSortOrder,
    onSearchChange: (String) -> Unit,
    onSortChange: (GarageSortOrder) -> Unit,
    onBrandClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // Cabecera
        GarageHeader(
            title    = "// GARAGE",
            subtitle = "$totalCars UNIDADES  ·  ${brands.size} MARCAS"
        )

        // Barra de búsqueda
        SearchBar(
            value       = searchQuery,
            placeholder = "Buscar marca…",
            onValueChange = onSearchChange,
            modifier    = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        // Chips de ordenación
        SortChipsRow(
            options = listOf(
                GarageSortOrder.A_Z        to "A–Z",
                GarageSortOrder.Z_A        to "Z–A",
                GarageSortOrder.MOST_CARS  to "Más coches",
                GarageSortOrder.LEAST_CARS to "Menos coches"
            ),
            selected   = sortOrder,
            onSelected = onSortChange
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (brands.isEmpty()) {
            EmptyState(text = "Sin marcas\nCaptura tu primer vehículo")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(brands, key = { it.name }) { brand ->
                    BrandCard(brand = brand, onClick = { onBrandClick(brand.name) })
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
//  Nivel 2 – Coches de una marca
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun CarsLevel(
    brandName: String,
    cars: List<com.jera.caracterisiticsv1.data.domain.model.CarModel>,
    searchQuery: String,
    sortOrder: CarSortOrder,
    onSearchChange: (String) -> Unit,
    onSortChange: (CarSortOrder) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // Cabecera con botón atrás
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceColor)
                .border(
                    1.dp,
                    AccentPrimary.copy(alpha = 0.5f),
                    RoundedCornerShape(0.dp)
                )
                .padding(vertical = 12.dp, horizontal = 12.dp)
        ) {
            // Botón atrás
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(AccentPrimary.copy(alpha = 0.15f))
                    .clickable { onBack() }
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint               = AccentPrimary,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text       = brandName.uppercase(),
                    color      = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    textAlign  = TextAlign.Center
                )
            }

            Text(
                text       = "${cars.size} coches",
                color      = NeonAmber.copy(alpha = 0.7f),
                fontFamily = Poppins,
                fontSize   = 11.sp,
                modifier   = Modifier.align(Alignment.CenterEnd)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AccentPrimary.copy(alpha = 0.6f))
        )

        // Búsqueda de coches
        SearchBar(
            value         = searchQuery,
            placeholder   = "Buscar por modelo, tipo, combustible…",
            onValueChange = onSearchChange,
            modifier      = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        // Chips ordenación coches
        SortChipsRow(
            options = listOf(
                CarSortOrder.NAME_AZ   to "A–Z",
                CarSortOrder.NAME_ZA   to "Z–A",
                CarSortOrder.YEAR_DESC to "Año ↓",
                CarSortOrder.YEAR_ASC  to "Año ↑",
                CarSortOrder.PROB_DESC to "Precisión"
            ),
            selected   = sortOrder,
            onSelected = onSortChange
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (cars.isEmpty()) {
            EmptyState(text = "Sin resultados\nIntenta otra búsqueda")
        } else {
            LazyColumn(
                modifier        = Modifier.fillMaxSize(),
                contentPadding  = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cars, key = { it.id }) { car ->
                    GarageCard(model = car)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
//  Componentes reutilizables
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun GarageHeader(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor)
            .border(1.dp, AccentPrimary.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Text(
            text       = title,
            color      = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize   = 20.sp,
            modifier   = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text       = subtitle,
            color      = NeonAmber.copy(alpha = 0.7f),
            fontFamily = Poppins,
            fontSize   = 11.sp,
            modifier   = Modifier.align(Alignment.CenterEnd)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AccentPrimary.copy(alpha = 0.6f))
    )
}

@Composable
private fun SearchBar(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceVariant)
            .border(1.dp, AccentPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector        = Icons.Default.Search,
                contentDescription = null,
                tint               = AccentPrimary.copy(alpha = 0.6f),
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text       = placeholder,
                        color      = SurfaceLight,
                        fontFamily = Poppins,
                        fontSize   = 13.sp
                    )
                }
                BasicTextField(
                    value         = value,
                    onValueChange = onValueChange,
                    singleLine    = true,
                    textStyle     = androidx.compose.ui.text.TextStyle(
                        color      = CyberWhite,
                        fontFamily = Poppins,
                        fontSize   = 13.sp
                    ),
                    cursorBrush = SolidColor(CyberYellow),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (value.isNotEmpty()) {
                Icon(
                    imageVector        = Icons.Default.Clear,
                    contentDescription = "Limpiar",
                    tint               = AccentPrimary.copy(alpha = 0.7f),
                    modifier           = Modifier
                        .size(18.dp)
                        .clickable { onValueChange("") }
                )
            }
        }
    }
}

@Composable
private fun <T> SortChipsRow(
    options: List<Pair<T, String>>,
    selected: T,
    onSelected: (T) -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(end = 6.dp)
    ) {
        items(options.size) { index ->
            val (value, label) = options[index]
            val isSelected = selected == value
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) AccentPrimary.copy(alpha = 0.25f)
                        else SurfaceVariant
                    )
                    .border(
                        1.dp,
                        if (isSelected) AccentPrimary else SurfaceLight.copy(alpha = 0.4f),
                        RoundedCornerShape(50)
                    )
                    .clickable { onSelected(value) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text       = label,
                    color      = if (isSelected) AccentPrimary else SurfaceLight,
                    fontFamily = Poppins,
                    fontSize   = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "🚗",
                fontSize   = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text       = text,
                color      = SurfaceLight,
                fontFamily = Poppins,
                fontSize   = 14.sp,
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}
