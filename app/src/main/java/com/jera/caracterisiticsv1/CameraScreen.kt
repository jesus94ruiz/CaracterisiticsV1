package com.jera.caracterisiticsv1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavHostController

class CameraScreen {

}

@Composable
fun CameraScreen(navController: NavHostController){
    Column(
        modifier = Modifier
            .background(Color(0x34, 0x3A, 0x40, 0xFF))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.size(30.dp))
        ButtonGallery()
        Spacer(Modifier.size(40.dp))
        ButtonCamera()
        Spacer(Modifier.size(40.dp))
        ButtonGarage()
    }
    Column()
    {
        Header()
    }
}