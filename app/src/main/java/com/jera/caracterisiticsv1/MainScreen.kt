package com.jera.caracterisiticsv1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun MainScreen(){
    Column(
        modifier = Modifier.
        background(Color(0x13, 0x18, 0x20,0xFF)).
        fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home")
    }
}

/*@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MainScreenPreview(){
    MainScreen()
}*/

