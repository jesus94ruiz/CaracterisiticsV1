package com.jera.caracterisiticsv1.screens

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.ui.components.ImageCard
import com.jera.caracterisiticsv1.ui.theme.BackgroundColor
import com.jera.caracterisiticsv1.ui.theme.Poppins
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.jera.caracterisiticsv1.ui.components.Analysing
import com.jera.caracterisiticsv1.utilities.ResourceState
import com.jera.caracterisiticsv1.viewmodels.CameraViewModel

@Composable
fun GalleryScreen(navController: NavHostController, cameraViewModel: CameraViewModel = hiltViewModel()) {

    val apiResponse by cameraViewModel.model.collectAsState(ResourceState.NotInitialized())
    val googleApiResponse by cameraViewModel.model.collectAsState(ResourceState.NotInitialized())

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            selectedImageUri = it
        }
    )
    when(googleApiResponse){
        is ResourceState.NotInitialized ->{
            if(selectedImageUri == null){
            LaunchedEffect(Unit) {
                pickPhotoLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
            }
            GalleryContent(pickPhotoLauncher, selectedImageUri)
        }
        is ResourceState.Loading ->{
            Analysing()
        }
        is ResourceState.Success ->{
            ResultsScreen(navController = navController, "gallery")
        }
        is ResourceState.Error ->{
            ResultsScreen(navController = navController, "gallery")
        }
    }

}

@Composable
fun GalleryContent(
    pickPhotoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    selectedImageUri: Uri?,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor
    ) {
        Column(
            modifier = Modifier.padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedImageUri == null) {
                Text(
                    text = "¿Desea analizar una imagen de la galería?",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xE9, 0xEC, 0xEF, 0xFF),
                    fontFamily = Poppins,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(36.dp))
                Button(
                    onClick = {
                        pickPhotoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }, modifier = Modifier
                        .padding(6.dp)
                        .size(100.dp, 64.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(
                            0xE9,
                            0xEC,
                            0xEF,
                            0xFF
                        )
                    ),
                    shape = RoundedCornerShape(10)
                ) {
                    Text(
                        text = "Abrir Galería",
                        color = Color(0x34, 0x3A, 0x40, 0xFF),
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = "¿Desea analizar esta imagen?",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xE9, 0xEC, 0xEF, 0xFF),
                    fontFamily = Poppins,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(36.dp))
                ImageCard(selectedImageUri.toString())
                confirmationButtons(pickPhotoLauncher, selectedImageUri!!)
            }
        }
    }
}

@Composable
fun confirmationButtons(
    pickPhotoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    selectedImageUri: Uri,
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    println(selectedImageUri)
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(modifier = Modifier
                .padding(6.dp)
                .size(120.dp, 64.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(
                        0xE9,
                        0xEC,
                        0xEF,
                        0xFF
                    )

                ),
                shape = RoundedCornerShape(10),
                onClick = {
                    pickPhotoLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text(
                    text = "Elegir otra foto",
                    color = Color(0x34, 0x3A, 0x40, 0xFF),
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center
                )
            }
            Button(
                modifier = Modifier
                    .padding(6.dp)
                    .size(100.dp, 64.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(
                        0xE9,
                        0xEC,
                        0xEF,
                        0xFF
                    )
                ),
                shape = RoundedCornerShape(10),
                onClick = {
                        cameraViewModel.getModelfromGallery(context,selectedImageUri)
                }
            ) {
                Text(
                    text = "Analizar",
                    color = Color(0x34, 0x3A, 0x40, 0xFF),
                    fontSize = 12.sp,
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}