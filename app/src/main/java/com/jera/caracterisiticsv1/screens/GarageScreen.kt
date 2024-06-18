package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.ui.components.GarageCard
import com.jera.caracterisiticsv1.ui.theme.Poppins
import com.jera.caracterisiticsv1.ui.theme.TextColor

@Composable
fun GarageScreen(
    navController: NavHostController,
    garageViewModel: GarageViewModel = hiltViewModel()
) {

    val models by garageViewModel.models.collectAsState()
    println("GarageScreen---------------------------------------")
    println(models)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor
    ) {
        LazyVerticalGrid(
            modifier = Modifier,
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            columns = GridCells.Fixed(2),
        ) {
            items(models){
                GarageCard(model = it)
            }
        }
    }
}

@Preview
@Composable
fun preview (){
    val car1 = CarModel(
        make_name = "Porsche1",
        model_name = "911",
        years = "1963-1973",
        probability = 0.9996,
        path = "/data/user/0/com.jera.caracterisiticsv1/files/9IptVZHWr3TJ.jpg"
    )
    val car2 = CarModel(
        make_name = "Porsche2",
        model_name = "911",
        years = "1963-1973",
        probability = 0.9996,
        path = "/data/user/0/com.jera.caracterisiticsv1/files/9IptVZHWr3TJ.jpg"
    )
    val car3 = CarModel(
        make_name = "Porsche3",
        model_name = "911",
        years = "1963-1973",
        probability = 0.9996,
        path = "/data/user/0/com.jera.caracterisiticsv1/files/9IptVZHWr3TJ.jpg"
    )
    val car4 = CarModel(
        make_name = "Porsche4",
        model_name = "911",
        years = "1963-1973",
        probability = 0.9996,
        path = "/data/user/0/com.jera.caracterisiticsv1/files/9IptVZHWr3TJ.jpg"
    )
    val models = listOf<CarModel>(car3, car1, car4, car2)

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundColor
        ) {
            LazyVerticalGrid(
                modifier = Modifier,
                contentPadding = PaddingValues(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Fixed(2),
            ) {
                items(models){
                    GarageCard(model = it)
                }
            }
        }
}


