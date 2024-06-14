package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jera.caracterisiticsv1.data.ApiResponse.Detection
import com.jera.caracterisiticsv1.data.ApiResponse.Mmg
import com.jera.caracterisiticsv1.viewmodels.CameraViewModel
import androidx.navigation.NavHostController

@Composable
fun ResultsScreen(navController: NavHostController, cameraViewModel: CameraViewModel = hiltViewModel()){

    val detections = cameraViewModel.detections

    ResultsContent(detections = detections)
}

@Composable
fun ResultsContent(detections: List<Detection>) {
    println("----------------------------------------RESULTSCONTENT---------------------------------------------------------------------")
    LazyColumn(modifier = Modifier.padding(16.dp).background(Color(0x34, 0x3A, 0x40, 0xFF))) {
        items(detections) { detection ->
            println(detection)
            detection.mmg.forEach { mmg -> detectionInfo(mmg = mmg); println(mmg) }
        }
    }
}

@Composable
fun detectionInfo(mmg: Mmg) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Make: ${mmg.make_name}")
        Text(text = "Model: ${mmg.model_name}")
        Text(text = "Years: ${mmg.years}")
    }
}