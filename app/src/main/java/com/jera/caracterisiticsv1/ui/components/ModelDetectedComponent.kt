package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected
import com.jera.caracterisiticsv1.ui.theme.*

@Composable
fun ModelDetectedComponent(model: ModelDetected) {

    println("---------------------ModelsComponent-------------------")
    println(model)

    val probabilityText = String.format("Al %.2f%% de probabilidad", model.probability * 100)

    Column {
        Text(
            text = model.make_name,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 44.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = model.model_name,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = CyberOrange,
            fontFamily = Poppins,
            fontSize = 44.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = model.years,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = CyberAmber,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        CarImageComponent(imageUrls = model.searchedImages)
        Text(
            text = probabilityText,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = CyberGreen,
            fontFamily = Poppins,
            fontSize = 12.sp
        )
    }
}
