package com.jera.caracterisiticsv1.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jera.caracterisiticsv1.data.ApiResponse.Detection
import com.jera.caracterisiticsv1.data.ApiResponse.Mmg
import com.jera.caracterisiticsv1.viewmodels.CameraViewModel
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.ui.components.Analysing
import com.jera.caracterisiticsv1.utilities.ResourceState

@Composable
fun ResultsScreen(navController: NavHostController, cameraViewModel: CameraViewModel = hiltViewModel()){
    println("----------------------------------------RESULTSSCREEN---------------------------------------------------------------------")
    val detections by cameraViewModel.model.collectAsState(initial = ResourceState.Loading())
    println(detections)
    val detections2 = cameraViewModel.detections
    when(detections){
       is ResourceState.Loading -> {
           Log.d("Loading", "IS LOADING")
           Analysing()
       }
       is ResourceState.Success-> {
           Log.d("Success", "Successful response")
           val response = (detections as ResourceState.Success).data
           Log.d("Success", "${response.detections}")
       }
       is ResourceState.Error -> {
           Log.d("Error", "Error fetching the response")
       }
    }
}

@Composable
fun ResultsContent(detections: List<Detection>) {
    println("----------------------------------------RESULTSCONTENT---------------------------------------------------------------------")
    LazyColumn(modifier = Modifier.padding(16.dp)) {
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