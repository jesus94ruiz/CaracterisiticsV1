package com.jera.caracterisiticsv1.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.ui.theme.*

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
            .background(BackgroundColor)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "ANALIZANDO...",
            color = CyberYellow,
            fontSize = 26.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.splashscreenrueda),
            contentDescription = "LogoRueda",
            modifier = Modifier.graphicsLayer {
                this.rotationZ = angle
            }
        )
    }
}
