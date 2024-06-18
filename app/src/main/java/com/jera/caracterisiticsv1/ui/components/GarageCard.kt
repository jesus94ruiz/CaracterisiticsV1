package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.ui.theme.Poppins
import com.jera.caracterisiticsv1.ui.theme.TextColor


@Composable
fun GarageCard(model: CarModel) {
    Card(
        modifier = Modifier
            .size(320.dp),
        backgroundColor = Color.Transparent
    ) {
        Column(
            //modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageCard(imageUrl = model.path)
            Text(
                text = "${model.make_name}",
                fontFamily = Poppins,
                fontSize = 16.sp,
                color = TextColor

            )
            Text(
                text = "${model.model_name}",
                fontFamily = Poppins,
                fontSize = 16.sp,
                color = TextColor
            )
            Text(
                text = "${model.years}",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextColor
            )
        }
    }
}

@Preview
@Composable
fun previewGarageCard() {
    val car = CarModel(
        make_name = "Porsche",
        model_name = "911",
        years = "1963-1973",
        probability = 0.9996,
        path = "/data/user/0/com.jera.caracterisiticsv1/files/9IptVZHWr3TJ.jpg"
    )
    GarageCard(car)
}