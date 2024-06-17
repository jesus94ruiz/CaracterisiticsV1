package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected
import com.jera.caracterisiticsv1.ui.theme.BackgroundColor
import com.jera.caracterisiticsv1.viewmodels.GarageViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController

@Composable
fun GarageScreen(navController: NavHostController, garageViewModel: GarageViewModel= hiltViewModel()){

    val models by garageViewModel.models.collectAsState()
    println("GarageScreen---------------------------------------")
    println(models)

    Surface(color = BackgroundColor) {
        Column() {
            CarModelCard(model = models[0])
        }
    }
}

@Composable
fun CarModelCard(model: CarModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Make: ${model.make_name}")
            Text(text = "Model: ${model.model_name}")
            Text(text = "Years: ${model.years}")
            Text(text = "Probability: ${model.probability}")
            Text(text = "File Path: ${model.path}")
        }
    }
}