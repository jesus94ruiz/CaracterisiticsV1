package com.jera.caracterisiticsv1

import android.app.LauncherActivity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.theme.Poppins


//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MainScreen(navController: NavHostController){
    Column(
        modifier = Modifier
            .background(Color(0x34, 0x3A, 0x40, 0xFF))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.size(50.dp))
        ButtonCamera(navController)
        Spacer(Modifier.size(30.dp))
        ButtonGallery(navController)
        Spacer(Modifier.size(30.dp))
        ButtonGarage()
    }
    Column()
    {
        Header()
    }
}

@Composable
fun ButtonGallery(navController: NavHostController) {
    val context = LocalContext.current
    Button(onClick = {
        val galleryIntent = Intent(context, GalleryActivity::class.java)
        context.startActivity(galleryIntent)
    },
        modifier = Modifier
            .height(180.dp)
            .width(180.dp)
            .shadow(10.dp, shape = RectangleShape, spotColor = Color(0x00, 0x00, 0x00, 0xFF)),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xE9, 0xEC, 0xEF, 0xFF)),
        shape = RoundedCornerShape(5),
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.size(10.dp))
            Icon(
                painter = painterResource(id = R.drawable.gallery),
                contentDescription = null,
                modifier = Modifier.size(80.dp, 80.dp),
                tint = Color(0x34, 0x3A, 0x40, 0xFF)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Galería",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 26.sp ,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                color = Color(0x34, 0x3A, 0x40, 0xFF)
            )
        }
    }
}

@Composable
fun ButtonCamera(navController: NavHostController) {
    val context = LocalContext.current
    Button(onClick = {
            navController.navigate(AppScreens.CameraScreen.route)},
    /*      val cameraIntent = Intent(context, CameraActivity::class.java)
            context.startActivity(cameraIntent)},*/
        modifier = Modifier
            .height(180.dp)
            .width(180.dp)
            .shadow(10.dp, shape = RectangleShape, spotColor = Color(0x00, 0x00, 0x00, 0xFF)),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xE9, 0xEC, 0xEF, 0xFF)),
        shape = RoundedCornerShape(5),
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.size(10.dp))
            Icon(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = null,
                modifier = Modifier.size(80.dp, 80.dp),
                tint = Color(0x34, 0x3A, 0x40, 0xFF)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Cámara",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 26.sp ,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                color = Color(0x34, 0x3A, 0x40, 0xFF)
            )
        }
    }
}

@Composable
fun ButtonGarage() {
    Button(onClick = {},
        modifier = Modifier
            .height(180.dp)
            .width(180.dp)
            .shadow(10.dp, shape = RectangleShape, spotColor = Color(0x00, 0x00, 0x00, 0xFF)),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xE9, 0xEC, 0xEF, 0xFF)),
        shape = RoundedCornerShape(5),
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.size(10.dp))
            Icon(
                painter = painterResource(id = R.drawable.car_in_garage),
                contentDescription = null,
                modifier = Modifier.size(80.dp, 80.dp),
                tint = Color(0x34, 0x3A, 0x40, 0xFF)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Garaje",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 26.sp ,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                color = Color(0x34, 0x3A, 0x40, 0xFF)
            )
        }
    }
}
@Composable
fun Header(){
    TopAppBar(
        backgroundColor = Color(0xE9, 0xEC, 0xEF, 0xFF),
        elevation = 40.dp
    ) {
        Text(text = "Caracteristics",
            Modifier.padding(5.dp, 5.dp),
            fontSize = 28.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            color = Color(0x34, 0x3A, 0x40, 0xFF)
        )
    }
}

