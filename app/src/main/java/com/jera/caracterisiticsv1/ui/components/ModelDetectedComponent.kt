package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected
import com.jera.caracterisiticsv1.ui.theme.Poppins

@Composable
fun ModelDetectedComponent(model: ModelDetected) {

    val probabilityText = String.format("Al %.2f%% de probabilidad", model.probability * 100)

    Column(
    ) {
        Text(
            text = "${model.make_name}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xE9, 0xEC, 0xEF, 0xFF),
            fontFamily = Poppins,
            fontSize = 44.sp,
            textAlign = TextAlign.Center,

        )
        Text(
            text = "${model.model_name}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xE9, 0xEC, 0xEF, 0xFF),
            fontFamily = Poppins,
            fontSize = 44.sp,
            textAlign = TextAlign.Center,

            )
        Text(
            text = "${model.years}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xE9, 0xEC, 0xEF, 0xFF),
            fontFamily = Poppins,
            fontSize = 24.sp
        )
        GoogleImageComponent(imageUrls = model.searchedImages)
        Text(text = probabilityText,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xE9, 0xEC, 0xEF, 0xFF),
            fontFamily = Poppins,
            fontSize = 12.sp)
    }
}

@Preview(showBackground = true, backgroundColor = 0x343A40FF)
@Composable
fun previewModelDetectedComponent(){
    val model = ModelDetected( "Ferrari","F50", "1995-1997", 0.9653,
        mutableListOf(
            "https://upload.wikimedia.org/wikipedia/commons/a/a5/1999_Ferrari_F50.jpg",
            "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F50-1067_49.jpg",
            "https://static.wikia.nocookie.net/hypercarss/images/9/9b/5402EC17-CBF7-4EBE-8F40-1D3A148C3464.jpeg/revision/latest?cb=20180411122943",
            "https://static0.topspeedimages.com/wordpress/wp-content/uploads/jpg/201309/ferrari-f50.jpg",
            "https://www.ultimatecarpage.com/images/car/186/Ferrari-F50-67449.jpg"))
    val model2 = ModelDetected("Ferrari", "F430", "2004-2009", 0.0345,
        mutableListOf(
            "https://upload.wikimedia.org/wikipedia/commons/1/17/Ferrari_F430_front_20080605.jpg",
            "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F430-733_19.jpg",
            "https://www.supercars.net/blog/wp-content/uploads/2016/04/2005_Ferrari_F43024.jpg",
            "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F430-733_34.jpg",
            "https://www.supercars.net/blog/wp-content/uploads/2016/04/2005_Ferrari_F43025.jpg"
        )
    )
    ModelDetectedComponent(model = model)
}


