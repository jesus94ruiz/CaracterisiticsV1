package com.jera.caracterisiticsv1

import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jera.caracterisiticsv1.ui.theme.Poppins
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavHostController) {


    // Permission
    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val hasCameraPermission = cameraPermissionState.status.isGranted
    val requestPermissionState = cameraPermissionState::launchPermissionRequest

    // Context
    val context = LocalContext.current
    val cameraController = remember { LifecycleCameraController(context) }
    val lifecycle = LocalLifecycleOwner.current


    println("CameraScreen Access")

    if(hasCameraPermission){
        cameraContent(cameraController, lifecycle, context, navController)
        println("hasCameraPermission")
    } else{
        requestPermission(requestPermissionState)
        println("NOhasCameraPermission")
    }
}

@Composable
private fun cameraContent(
    cameraController: LifecycleCameraController,
    lifecycle: LifecycleOwner,
    localContext: Context,
    navController: NavHostController,
) {

    cameraController.bindToLifecycle(lifecycle)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
            previewView.controller = cameraController
            previewView
        });
        FloatingActionButton(
            onClick = {
                val executor = ContextCompat.getMainExecutor(localContext)
                takePicture(cameraController, executor)
            },
            modifier = Modifier
                .height(70.dp)
                .width(80.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            backgroundColor = Color(0xE9, 0xEC, 0xEF, 0xFF),
            contentColor = Color(0x34, 0x3A, 0x40, 0xFF),
            shape = RoundedCornerShape(50)
        ) {

            Icon(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "Hacer foto",
                tint = Color(0x34, 0x3A, 0x40, 0xFF)
            )
        }
        FloatingActionButton(
            onClick = {
                    navController.popBackStack()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 32.dp)
                .height(40.dp)
                .width(70.dp),
            backgroundColor = Color(0xE9, 0xEC, 0xEF, 0xFF),
            contentColor = Color(0x34, 0x3A, 0x40, 0xFF),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "Backstack"
            )
        }
    }
}

private fun takePicture(cameraController: LifecycleCameraController, executor: Executor) {
    val file = File.createTempFile("imagentest", ".jpg")
    val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()
    cameraController.takePicture(
        outputDirectory,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                println(outputFileResults.savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                println()
            }
        },
    )
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

