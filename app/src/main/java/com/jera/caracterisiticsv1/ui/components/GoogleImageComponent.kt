package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun GoogleImageComponent(imageUrls: List<String>) {
    println("------------------------GOOGLE IMAGE COMPONENT-----------------------------------")
    ImageCarousel(imageUrls)
}

@Composable
fun ImageCarousel(imageUrls: List<String>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(imageUrls) { imageUrl ->
            ImageCard(imageUrl = imageUrl)
        }
    }
}

@Composable
fun ImageCard(imageUrl: String) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .size(300.dp, 169.dp)
    ) {
        // IMPORTANTE:
        // Brave (y otros buscadores) devuelven en "url" la página HTML, NO la imagen directa.
        // Coil intenta decodificar ese HTML como imagen y Skia falla con:
        // "Failed to create image decoder ... 'unimplemented'".
        //
        // Hay que pasar aquí la URL de la imagen directa (jpg/png/webp), típicamente:
        // - Brave: result.properties.url (o thumbnail.src)
        // - Google: item.link
        //
        // Como fallback defensivo, evitamos intentar cargar páginas HTML.
        val isProbablyHtmlPage = imageUrl.startsWith("http") &&
            (imageUrl.endsWith("/") || imageUrl.contains("/wallpapers/"))

        AsyncImage(
            model = if (isProbablyHtmlPage) {
                null
            } else {
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .size(Size.ORIGINAL)
                    .crossfade(true)
                    .build()
            },
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
    }
}
