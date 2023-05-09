package com.jera.caracterisiticsv1

import android.window.SplashScreen
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.navigation.AppScreens
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavHostController){
    LaunchedEffect(key1 = true){
        delay( 1000)
        navController.popBackStack()
        navController.navigate(AppScreens.MainScreen.route)
    }
    Splash()
}

@Composable
fun Splash() {
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
        Image(painter = painterResource(id = R.drawable.splashscreen), contentDescription = "Logo")
        Image(painter = painterResource(id = R.drawable.splashscreenrueda) , contentDescription = "LogoRueda",
            Modifier.graphicsLayer{
                this.rotationZ = angle })
    }
}