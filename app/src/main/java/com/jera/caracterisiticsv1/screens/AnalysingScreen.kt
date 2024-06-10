package com.jera.caracterisiticsv1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.ui.theme.Poppins
import kotlinx.coroutines.delay
import java.net.URI
import java.io.File
import javax.inject.Inject


@Composable
fun AnalysingScreen(navController: NavHostController){
    LaunchedEffect(key1 = true){
        delay( 3000)
        navController.popBackStack()
        navController.navigate(AppScreens.MainScreen.route)
    }
    Analysing()
}

@Preview
@Composable
fun Analysing() {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 720F,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )
    Box(
        modifier = Modifier
            .background(Color(0x13, 0x18, 0x20, 0xFF))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            text = "Analizando...",
            color = Color(0xE9, 0xEC, 0xEF, 0xFF),
            fontSize = 26.sp ,
            fontFamily = Poppins,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)

        )
        Image(painter = painterResource(id = R.drawable.splashscreenrueda) , contentDescription = "LogoRueda",
            Modifier.graphicsLayer{
                this.rotationZ = angle })
    }
}