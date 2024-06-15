package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jera.caracterisiticsv1.viewmodels.CameraViewModel
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected
import com.jera.caracterisiticsv1.ui.components.ModelDetectedComponent
import com.jera.caracterisiticsv1.utilities.ResourceState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.components.Analysing
import com.jera.caracterisiticsv1.ui.theme.Poppins

@Composable
fun ResultsScreen(navController: NavHostController, cameraViewModel: CameraViewModel = hiltViewModel()){

    println("----------------------------------------RESULTS_SCREEN---------------------------------------------------------------------")
    val modelsDetected by cameraViewModel.modelsDetected.collectAsState(ResourceState.NotInitialized())

    println(modelsDetected)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0x34, 0x3A, 0x40, 0xFF)
    )
    {
        when(modelsDetected){
            is ResourceState.NotInitialized ->{
                Analysing()
            }
            is ResourceState.Loading ->{
                Analysing()
            }
            is ResourceState.Success ->{
                ResultsContent(modelsDetected = (modelsDetected as ResourceState.Success).data, navController)
            }
            is ResourceState.Error ->{
                println("Error")
            }
        }
    }
}

@Composable
fun ResultsContent(modelsDetected: List<ModelDetected>, navController: NavHostController) {

    Column(modifier = Modifier.padding(48.dp, 48.dp)) {
        Text(text = "¡Modelo encontrado!",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xE9, 0xEC, 0xEF, 0xFF),
            fontFamily = Poppins,
            fontSize = 24.sp
        )
        ModelDetectedComponent(modelsDetected[0])
    }
    Column(
        modifier = Modifier.padding(top = 600.dp)
    ) {
        Text(text = "¿Que deseas hacer?",
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 50.dp),
            color = Color(0xE9, 0xEC, 0xEF, 0xFF),
            fontFamily = Poppins,
            fontSize = 24.sp
        )
        ButtonRow(navController)
    }

    /*
    LazyRow(
        modifier = modifier
            .padding(16.dp)
            .background(Color(0x34, 0x3A, 0x40, 0xFF))
            .fillMaxWidth(),
        ) {
        items(modelsDetected) { model ->
            println(model)
            ModelDetectedComponent(model)
        }
    }*/
}

@Composable
fun ButtonRow(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Botón de Home
        SmallButtonWithIcon(
            text = "Home",
            icon = painterResource(id = R.drawable.home),
            onClick = { navController.navigate(AppScreens.MainScreen.route) }
        )

        // Botón de Repetir
        SmallButtonWithIcon(
            text = "Repetir",
            icon = painterResource(id = R.drawable.reload) ,
            onClick = { navController.navigate(AppScreens.CameraScreen.route) }
        )

        // Botón de Guardar
        SmallButtonWithIcon(
            text = "Guardar",
            icon = painterResource(id = R.drawable.car_in_garage),
            onClick = { /* Acción del botón Guardar */ }
        )
    }
}

@Composable
fun SmallButtonWithIcon(text: String, icon: Painter, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .size(90.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xE9, 0xEC, 0xEF, 0xFF)),
        shape = RoundedCornerShape(10)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp) ,
                tint = Color(0x34, 0x3A, 0x40, 0xFF)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = Color(0x34, 0x3A, 0x40, 0xFF),
                fontSize = 10.sp,
                fontFamily = Poppins
            )
        }
    }
}

/*
@Preview(showBackground = true, backgroundColor = 0x343A40FF)
@Composable
fun previewResultsContent(){
    val model = ModelDetected( "Ferrari","F50", "1995-1997", 0.9653,
        mutableListOf(
            "https://upload.wikimedia.org/wikipedia/commons/a/a5/1999_Ferrari_F50.jpg",
            "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F50-1067_49.jpg",
            "https://static.wikia.nocookie.net/hypercarss/images/9/9b/5402EC17-CBF7-4EBE-8F40-1D3A148C3464.jpeg/revision/latest?cb=20180411122943",
            "https://static0.topspeedimages.com/wordpress/wp-content/uploads/jpg/201309/ferrari-f50.jpg",
            "https://www.ultimatecarpage.com/images/car/186/Ferrari-F50-67449.jpg"))
    val model2 = ModelDetected("Ferrari", "F430", "2004-2009", 0.0345,
        mutableListOf(
            "https://upload.wikimedia.org/wikipedia/commons/1/17/Ferrari_F430_front_20080605.jpg",
            "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F430-733_19.jpg",
            "https://www.supercars.net/blog/wp-content/uploads/2016/04/2005_Ferrari_F43024.jpg",
            "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F430-733_34.jpg",
            "https://www.supercars.net/blog/wp-content/uploads/2016/04/2005_Ferrari_F43025.jpg"
        )
    )
    val models = mutableListOf<ModelDetected>(model, model2)
    ResultsContent(modelsDetected = models)
}*/
