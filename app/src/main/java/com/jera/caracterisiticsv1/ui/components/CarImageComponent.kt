package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.jera.caracterisiticsv1.ui.theme.*

/**
 * Componente que muestra un carrusel horizontal de imágenes del coche
 * obtenidas a través de CarImagesAPI.
 *
 * @param imageUrls Lista de URLs de imágenes a mostrar.
 * @param modifier Modifier opcional.
 */
@Composable
fun CarImageComponent(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    if (imageUrls.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(SurfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay imágenes disponibles",
                color = TextSecondary,
                fontFamily = Poppins,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(imageUrls.filter { it.isNotBlank() }) { url ->
            CarImageItem(url = url)
        }
    }
}

@Composable
private fun CarImageItem(url: String) {
    val context = LocalContext.current

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Imagen del coche",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(300.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, AccentPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
        loading = {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp)
                    .background(SurfaceColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = CyberYellow,
                    strokeWidth = 2.dp
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp)
                    .background(SurfaceColor)
                    .border(1.dp, CyberRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⚠",
                        color = CyberRed,
                        fontSize = 28.sp
                    )
                    Text(
                        text = "Imagen no disponible",
                        color = TextSecondary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    )
}
