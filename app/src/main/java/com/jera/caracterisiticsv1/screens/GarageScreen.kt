package com.jera.caracterisiticsv1.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.ui.components.BrandCard
import com.jera.caracterisiticsv1.ui.components.BrandInfo
import com.jera.caracterisiticsv1.ui.components.GarageCard
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.CarSortOrder
import com.jera.caracterisiticsv1.viewmodels.GarageSortOrder
import com.jera.caracterisiticsv1.viewmodels.GarageViewModel

private enum class GarageViewMode { BRANDS, CARS }

@Composable
fun GarageScreen(
    navController: NavHostController,
    garageViewModel: GarageViewModel = hiltViewModel()
) {
    val selectedBrand      by garageViewModel.selectedBrand.collectAsState()
    val brands             by garageViewModel.brands.collectAsState()
    val brandSearchQuery   by garageViewModel.brandSearchQuery.collectAsState()
    val brandSortOrder     by garageViewModel.brandSortOrder.collectAsState()
    val cars               by garageViewModel.carsForSelectedBrand.collectAsState()
    val carSearchQuery     by garageViewModel.carSearchQuery.collectAsState()
    val carSortOrder       by garageViewModel.carSortOrder.collectAsState()
    val allModels          by garageViewModel.models.collectAsState()
    val allCarsSorted      by garageViewModel.allCarsSorted.collectAsState()
    val allCarsSearchQuery by garageViewModel.allCarsSearchQuery.collectAsState()
    val allCarsSortOrder   by garageViewModel.allCarsSortOrder.collectAsState()

    var viewMode by remember { mutableStateOf(GarageViewMode.BRANDS) }

    BackHandler(enabled = selectedBrand != null) { garageViewModel.clearSelectedBrand() }

    Surface(modifier = Modifier.fillMaxSize(), color = CyberDark) {
        if (selectedBrand != null) {
            CarsLevel(
                brandName      = selectedBrand!!,
                cars           = cars,
                searchQuery    = carSearchQuery,
                sortOrder      = carSortOrder,
                onSearchChange = { garageViewModel.setCarSearchQuery(it) },
                onSortChange   = { garageViewModel.setCarSortOrder(it) },
                onBack         = { garageViewModel.clearSelectedBrand() }
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                GarageHeader(
                    title    = "// GARAGE",
                    subtitle = "${allModels.size} UNIDADES  ·  ${brands.size} MARCAS",
                    onShowcaseClick = { navController.navigate(com.jera.caracterisiticsv1.navigation.AppScreens.ShowcaseScreen.route) }
                )
                ViewToggleBar(current = viewMode, onChange = { viewMode = it })
                Crossfade(
                    targetState   = viewMode,
                    animationSpec = tween(300),
                    label         = "garage_mode"
                ) { mode ->
                    when (mode) {
                        GarageViewMode.BRANDS -> BrandsContent(
                            brands         = brands,
                            searchQuery    = brandSearchQuery,
                            sortOrder      = brandSortOrder,
                            onSearchChange = { garageViewModel.setBrandSearchQuery(it) },
                            onSortChange   = { garageViewModel.setBrandSortOrder(it) },
                            onBrandClick   = { garageViewModel.selectBrand(it) }
                        )
                        GarageViewMode.CARS -> AllCarsContent(
                            cars           = allCarsSorted,
                            searchQuery    = allCarsSearchQuery,
                            sortOrder      = allCarsSortOrder,
                            onSearchChange = { garageViewModel.setAllCarsSearchQuery(it) },
                            onSortChange   = { garageViewModel.setAllCarsSortOrder(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewToggleBar(current: GarageViewMode, onChange: (GarageViewMode) -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceColor)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ToggleTab(
                label    = "MARCAS",
                icon     = Icons.Default.Star,
                selected = current == GarageViewMode.BRANDS,
                onClick  = { onChange(GarageViewMode.BRANDS) },
                modifier = Modifier.weight(1f)
            )
            ToggleTab(
                label    = "COCHES",
                icon     = Icons.Default.Build,
                selected = current == GarageViewMode.CARS,
                onClick  = { onChange(GarageViewMode.CARS) },
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AccentPrimary.copy(alpha = 0.6f))
        )
    }
}

@Composable
private fun ToggleTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg     by animateColorAsState(if (selected) AccentPrimary.copy(.2f) else SurfaceVariant, tween(200), label = "bg")
    val border by animateColorAsState(if (selected) AccentPrimary else SurfaceLight.copy(.3f), tween(200), label = "br")
    val txt    by animateColorAsState(if (selected) AccentPrimary else SurfaceLight, tween(200), label = "tx")

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 9.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = txt, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text          = label,
            color         = txt,
            fontFamily    = Poppins,
            fontWeight    = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize      = 12.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun BrandsContent(
    brands: List<BrandInfo>,
    searchQuery: String,
    sortOrder: GarageSortOrder,
    onSearchChange: (String) -> Unit,
    onSortChange: (GarageSortOrder) -> Unit,
    onBrandClick: (String) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        GarageSearchBar(
            value         = searchQuery,
            placeholder   = "Buscar marca…",
            onValueChange = onSearchChange,
            modifier      = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
        BrandSortChips(selected = sortOrder, onSelected = onSortChange)
        Spacer(Modifier.height(4.dp))
        if (brands.isEmpty()) {
            GarageEmptyState("Sin marcas\nCaptura tu primer vehículo")
        } else {
            LazyVerticalGrid(
                columns               = GridCells.Fixed(3),
                modifier              = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                contentPadding        = PaddingValues(vertical = 10.dp),
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

@Composable
private fun AllCarsContent(
    cars: List<CarModel>,
    searchQuery: String,
    sortOrder: CarSortOrder,
    onSearchChange: (String) -> Unit,
    onSortChange: (CarSortOrder) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        GarageSearchBar(
            value         = searchQuery,
            placeholder   = "Buscar por modelo, marca…",
            onValueChange = onSearchChange,
            modifier      = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
        CarSortChips(selected = sortOrder, onSelected = onSortChange)
        Spacer(Modifier.height(4.dp))
        if (cars.isEmpty()) {
            GarageEmptyState("Sin coches\nCaptura tu primer vehículo")
        } else {
            LazyVerticalGrid(
                columns               = GridCells.Fixed(3),
                modifier              = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                contentPadding        = PaddingValues(vertical = 8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cars, key = { it.id }) { car ->
                    CompactCarItem(car = car)
                }
            }
        }
    }
}

@Composable
private fun CompactCarItem(car: CarModel) {
    var showDialog by remember { mutableStateOf(false) }
    val ctx        = LocalContext.current
    val shape      = RoundedCornerShape(10.dp)

    Column(
        modifier = Modifier
            .clip(shape)
            .background(SurfaceColor)
            .border(1.dp, SurfaceLight.copy(alpha = 0.25f), shape)
            .clickable { showDialog = true }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(SurfaceVariant)
        ) {
            if (!car.path.isNullOrBlank()) {
                AsyncImage(
                    model              = ImageRequest.Builder(ctx).data(Uri.parse(car.path)).crossfade(true).build(),
                    contentDescription = "${car.make_name} ${car.model_name}",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter            = painterResource(R.drawable.car_in_garage),
                    contentDescription = null,
                    tint               = SurfaceLight.copy(alpha = 0.4f),
                    modifier           = Modifier.size(32.dp).align(Alignment.Center)
                )
            }
        }
        Column(Modifier.padding(horizontal = 6.dp, vertical = 5.dp)) {
            Text(
                text       = car.model_name,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize   = 10.sp,
                color      = TextColor,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text       = car.make_name,
                fontFamily = Poppins,
                fontSize   = 9.sp,
                color      = AccentPrimary,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            if (!car.years.isNullOrBlank()) {
                Text(car.years, fontFamily = Poppins, fontSize = 8.sp, color = TextSecondary, maxLines = 1)
            }
        }
    }

    if (showDialog) {
        CarDetailDialog(car = car, onDismiss = { showDialog = false })
    }
}

@Composable
private fun CarDetailDialog(car: CarModel, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceColor)
                .border(1.5.dp, AccentPrimary.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(SurfaceVariant)
                ) {
                    if (!car.path.isNullOrBlank()) {
                        AsyncImage(
                            model              = ImageRequest.Builder(ctx).data(Uri.parse(car.path)).crossfade(true).build(),
                            contentDescription = null,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            painter            = painterResource(R.drawable.car_in_garage),
                            contentDescription = null,
                            tint               = SurfaceLight.copy(0.3f),
                            modifier           = Modifier.size(64.dp).align(Alignment.Center)
                        )
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${car.make_name} ${car.model_name}", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextColor)
                    if (!car.years.isNullOrBlank()) DInfoRow("Año", car.years)
                    if (!car.specsBodyType.isNullOrBlank()) DInfoRow("Carrocería", car.specsBodyType!!)
                    if (!car.specsFuelType.isNullOrBlank()) DInfoRow("Combustible", car.specsFuelType!!)
                    if (!car.specsEngineType.isNullOrBlank()) DInfoRow("Motor", car.specsEngineType!!)
                    if (!car.specsGearbox.isNullOrBlank()) DInfoRow("Caja", car.specsGearbox!!)
                    if (!car.specsDriveWheels.isNullOrBlank()) DInfoRow("Tracción", car.specsDriveWheels!!)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick  = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = AccentPrimary.copy(0.2f), contentColor = AccentPrimary)
                    ) {
                        Text("CERRAR", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DInfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontFamily = Poppins, fontSize = 12.sp, color = TextColor)
    }
}

@Composable
private fun CarsLevel(
    brandName: String,
    cars: List<CarModel>,
    searchQuery: String,
    sortOrder: CarSortOrder,
    onSearchChange: (String) -> Unit,
    onSortChange: (CarSortOrder) -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier          = Modifier.fillMaxWidth().background(SurfaceColor).padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AccentPrimary) }
            Text(brandName.uppercase(), fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextColor, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
            Text("${cars.size}", fontFamily = Poppins, fontSize = 13.sp, color = AccentPrimary)
            Spacer(Modifier.width(8.dp))
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(AccentPrimary.copy(0.5f)))
        GarageSearchBar(value = searchQuery, placeholder = "Buscar en $brandName…", onValueChange = onSearchChange, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
        CarSortChips(selected = sortOrder, onSelected = onSortChange)
        Spacer(Modifier.height(4.dp))
        if (cars.isEmpty()) {
            GarageEmptyState("Sin resultados")
        } else {
            LazyColumn(
                modifier       = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cars, key = { it.id }) { GarageCard(model = it) }
            }
        }
    }
}

@Composable
private fun GarageHeader(title: String, subtitle: String, onShowcaseClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceColor).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = AccentPrimary, letterSpacing = 2.sp)
            Text(subtitle, fontFamily = Poppins, fontSize = 11.sp, color = TextSecondary, letterSpacing = 0.5.sp)
        }
        // Botón Expositor
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(CyberYellow.copy(alpha = 0.15f))
                .border(1.dp, CyberYellow.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .clickable { onShowcaseClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "⭐ EXPOSITOR",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = CyberYellow,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun GarageSearchBar(value: String, placeholder: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    val shape = RoundedCornerShape(12.dp)
    BasicTextField(
        value           = value,
        onValueChange   = onValueChange,
        singleLine      = true,
        textStyle       = androidx.compose.ui.text.TextStyle(color = TextColor, fontFamily = Poppins, fontSize = 13.sp),
        cursorBrush     = SolidColor(AccentPrimary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        modifier        = modifier.fillMaxWidth().clip(shape).background(SurfaceVariant).border(1.dp, SurfaceLight.copy(0.3f), shape).padding(horizontal = 12.dp, vertical = 10.dp),
        decorationBox   = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(placeholder, fontFamily = Poppins, fontSize = 13.sp, color = TextSecondary)
                    inner()
                }
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }, modifier = Modifier.size(20.dp)) {
                        Text("✕", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    )
}

@Composable
private fun BrandSortChips(selected: GarageSortOrder, onSelected: (GarageSortOrder) -> Unit) {
    val opts = listOf(GarageSortOrder.A_Z to "A–Z", GarageSortOrder.Z_A to "Z–A", GarageSortOrder.MOST_CARS to "Más coches", GarageSortOrder.LEAST_CARS to "Menos coches")
    LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(opts) { (order, label) ->
            val active = selected == order
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (active) AccentPrimary.copy(.2f) else SurfaceVariant)
                    .border(1.dp, if (active) AccentPrimary else SurfaceLight.copy(.3f), RoundedCornerShape(20.dp))
                    .clickable { onSelected(order) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(label, fontFamily = Poppins, fontSize = 11.sp, color = if (active) AccentPrimary else SurfaceLight)
            }
        }
    }
}

@Composable
private fun CarSortChips(selected: CarSortOrder, onSelected: (CarSortOrder) -> Unit) {
    val opts = listOf(CarSortOrder.NAME_AZ to "A–Z", CarSortOrder.NAME_ZA to "Z–A", CarSortOrder.YEAR_DESC to "Año ↓", CarSortOrder.YEAR_ASC to "Año ↑", CarSortOrder.PROB_DESC to "Precisión")
    LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(opts) { (order, label) ->
            val active = selected == order
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (active) AccentPrimary.copy(.2f) else SurfaceVariant)
                    .border(1.dp, if (active) AccentPrimary else SurfaceLight.copy(.3f), RoundedCornerShape(20.dp))
                    .clickable { onSelected(order) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(label, fontFamily = Poppins, fontSize = 11.sp, color = if (active) AccentPrimary else SurfaceLight)
            }
        }
    }
}

@Composable
private fun GarageEmptyState(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                painter            = painterResource(R.drawable.car_in_garage),
                contentDescription = null,
                tint               = AccentPrimary.copy(.4f),
                modifier           = Modifier.size(56.dp)
            )
            text.lines().forEach {
                Text(it, fontFamily = Poppins, fontSize = 13.sp, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}
