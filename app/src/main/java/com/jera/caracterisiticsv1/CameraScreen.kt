package com.jera.caracterisiticsv1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jera.caracterisiticsv1.ui.theme.Poppins

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavHostController) {


    // Permission
    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val hasCameraPermission = cameraPermissionState.status.isGranted
    val requestPermissionState = cameraPermissionState::launchPermissionRequest


    println("CameraScreen Access")

    if(hasCameraPermission){
        cameraContent()
        println("hasCameraPermission")
    } else{
        requestPermission(requestPermissionState)
        println("NOhasCameraPermission")
    }
}

@Composable
private  fun cameraContent(){

}

@Composable
private fun requestPermission(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .background(Color(0x34, 0x3A, 0x40, 0xFF))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Por favor conceda los permisos necesarios para poder utilizar la cámara",
            textAlign = TextAlign.Center,
            color = Color(0xE9, 0xEC, 0xEF, 0xFF),
            fontFamily = Poppins,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .height(40.dp)
                .width(200.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xE9, 0xEC, 0xEF, 0xFF)),
            shape = RoundedCornerShape(5),
        ) {
            Text(
                text = "Conceder permisos",
                color = Color(0x34, 0x3A, 0x40, 0xFF),
                fontFamily = Poppins,
            )
        }
    }
}