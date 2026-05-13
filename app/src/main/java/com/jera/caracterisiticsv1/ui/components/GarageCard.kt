package com.jera.caracterisiticsv1.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private val LabelColor = Color(0xFF9E9E9E)   // gris medio para etiquetas
private val ValueColor = Color(0xFFEEEEEE)   // blanco suave para valores

/** Una fila con etiqueta + valor; si el valor es null/blank no se renderiza. */
@Composable
private fun SpecRow(label: String, value: String?, unit: String = "") {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 11.sp,
            color = LabelColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (unit.isBlank()) value else "$value $unit",
            fontFamily = Poppins,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ValueColor
        )
    }
}

/** Encabezado de sección de specs (ej: MOTOR, PRESTACIONES…). */
@Composable
private fun SpecSectionTitle(title: String) {
    Text(
        text = title,
        fontFamily = Poppins,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = CyberAmber,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
    Divider(color = CyberAmber.copy(alpha = 0.3f), thickness = 0.5.dp)
}

// ─────────────────────────────────────────────────────────────────────────────
// GarageCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GarageCard(model: CarModel) {
    var expanded by remember { mutableStateOf(false) }
    val hasSpecs = listOf(
        model.specsBodyType, model.specsPowerHp, model.specsEngineType,
        model.specsTorqueNm, model.specsAcceleration0100, model.specsTopSpeedKmh
    ).any { !it.isNullOrBlank() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = SurfaceColor,
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Imagen del coche ─────────────────────────────────────────────
            ImageCard(imageUrl = model.path)

            // ── Info básica ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Marca
                Text(
                    text = model.make_name.uppercase(),
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LabelColor,
                    letterSpacing = 1.5.sp
                )
                // Modelo
                Text(
                    text = model.model_name,
                    fontFamily = Poppins,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberYellow,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Año
                Text(
                    text = model.years,
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    color = CyberAmber
                )

                // ── Resumen compacto (si hay specs) ──────────────────────────
                if (hasSpecs) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = AccentPrimary.copy(alpha = 0.07f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SummaryChip(
                            label = "Tipo",
                            value = model.specsBodyType
                        )
                        SummaryChip(
                            label = "Potencia",
                            value = model.specsPowerHp?.let { "$it CV" }
                        )
                        SummaryChip(
                            label = "0-100",
                            value = model.specsAcceleration0100?.let { "${it}s" }
                        )
                    }
                }

                // ── Botón desplegable ─────────────────────────────────────────
                if (hasSpecs) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (expanded) "Ocultar especificaciones" else "Ver especificaciones",
                            fontFamily = Poppins,
                            fontSize = 11.sp,
                            color = AccentPrimary
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = AccentPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // ── Panel de specs (animado) ──────────────────────────────
                    AnimatedVisibility(
                        visible = expanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Divider(
                                color = AccentPrimary.copy(alpha = 0.2f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Carrocería
                            SpecSectionTitle("CARROCERÍA")
                            SpecRow("Tipo", model.specsBodyType)
                            SpecRow("Plazas", model.specsSeats)

                            // Motor
                            SpecSectionTitle("MOTOR")
                            SpecRow("Tipo motor", model.specsEngineType)
                            SpecRow("Cilindrada", model.specsDisplacementCm3, "cm³")
                            SpecRow("Cilindros", model.specsCylinders)
                            SpecRow("Potencia", model.specsPowerHp, "CV")
                            SpecRow("Par máx.", model.specsTorqueNm, "Nm")
                            SpecRow("Depósito", model.specsFuelTankL, "L")

                            // Prestaciones
                            SpecSectionTitle("PRESTACIONES")
                            SpecRow("0-100 km/h", model.specsAcceleration0100, "s")
                            SpecRow("Vel. máx.", model.specsTopSpeedKmh, "km/h")
                            SpecRow("Consumo mixto", model.specsConsumptionMixed, "L/100km")

                            // Transmisión
                            SpecSectionTitle("TRANSMISIÓN")
                            SpecRow("Caja de cambios", model.specsGearbox)
                            SpecRow("Tracción", model.specsDriveWheels)

                            // Dimensiones
                            SpecSectionTitle("DIMENSIONES")
                            SpecRow("Longitud", model.specsLengthMm, "mm")
                            SpecRow("Anchura", model.specsWidthMm, "mm")
                            SpecRow("Altura", model.specsHeightMm, "mm")
                            SpecRow("Batalla", model.specsWheelbaseMm, "mm")
                            SpecRow("Peso en vacío", model.specsCurbWeightKg, "kg")
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SummaryChip – pastilla con etiqueta + valor
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SummaryChip(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 9.sp,
            color = LabelColor
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = CyberYellow
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
fun PreviewGarageCard() {
    val car = CarModel(
        make_name = "Porsche",
        model_name = "911 Carrera S",
        years = "2019-2023",
        probability = 0.9996,
        path = "",
        specsBodyType = "Coupe",
        specsSeats = "4",
        specsEngineType = "Boxer",
        specsDisplacementCm3 = "2981",
        specsCylinders = "6",
        specsPowerHp = "450",
        specsTorqueNm = "530",
        specsFuelTankL = "67",
        specsAcceleration0100 = "3.5",
        specsTopSpeedKmh = "308",
        specsConsumptionMixed = "9.0",
        specsLengthMm = "4519",
        specsWidthMm = "1852",
        specsHeightMm = "1300",
        specsWheelbaseMm = "2450",
        specsCurbWeightKg = "1530",
        specsGearbox = "PDK 8 velocidades",
        specsDriveWheels = "Tracción trasera"
    )
    GarageCard(car)
}
