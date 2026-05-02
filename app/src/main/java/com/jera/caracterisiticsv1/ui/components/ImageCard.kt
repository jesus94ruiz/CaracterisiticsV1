package com.jera.caracterisiticsv1.ui.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jera.caracterisiticsv1.ui.theme.AccentPrimary
import com.jera.caracterisiticsv1.ui.theme.CyberAmber

/**
 * Muestra la imagen local seleccionada de la galería del dispositivo.
 * Estilo cyberpunk con bordes iluminados.
 *
 * @param imageUrl URI de la imagen en formato String.
 */
@Composable
fun ImageCard(imageUrl: String) {
    val context = LocalContext.current
    val cardShape = RoundedCornerShape(12.dp)

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(Uri.parse(imageUrl))
            .crossfade(true)
            .build(),
        contentDescription = "Imagen seleccionada",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(cardShape)
            .border(
                width = 1.dp,
                color = AccentPrimary.copy(alpha = 0.7f),
                shape = cardShape
            )
    )
}
