package com.jera.caracterisiticsv1.ui.components
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size

/**
 * Componente específico para imágenes de Brave.
 *
 * IMPORTANTE:
 * - Brave devuelve en `result.url` una página HTML.
 * - La URL de imagen “real” para pintar suele estar en:
 *    - `result.thumbnail.src` (recomendado para UI porque es estable y suele ser jpg/png)
 *    - `result.properties.url` (si la quieres en alta resolución)
 *
 * Pásale aquí una lista de URLs DIRECTAS de imagen (por ejemplo, thumbnail.src).
 */
@Composable
fun BraveImageComponent(thumbnailUrls: List<String>) {
    // Si viene del VM con 5 posiciones (una por vista), pintamos con etiquetas fijas.
    // Si viene una lista “normal”, mantenemos el carrusel sin etiquetas.
    val viewLabels = listOf("3/4 front", "top", "side", "rear", "interior")
    if (thumbnailUrls.size == viewLabels.size) {
        BraveImageCarouselLabeled(urls = thumbnailUrls, labels = viewLabels)
    } else {
        BraveImageCarousel(thumbnailUrls)
    }
}

@Composable
private fun BraveImageCarousel(thumbnailUrls: List<String>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(thumbnailUrls) { url ->
            BraveImageCard(imageUrl = url)
        }
    }
}

@Composable
private fun BraveImageCarouselLabeled(
    urls: List<String>,
    labels: List<String>
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(urls.size) { index ->
            BraveImageCard(
                imageUrl = urls[index],
                label = labels.getOrNull(index)
            )
        }
    }
}

@Composable
private fun BraveImageCard(
    imageUrl: String,
    label: String? = null
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .size(300.dp, 169.dp)
    ) {
        if (imageUrl.isBlank()) {
            // Placeholder simple cuando una de las 5 búsquedas no devuelve resultado.
            Text(text = label ?: "Sin imagen")
        } else {
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    // Dejamos que Compose adapte el bitmap al contenedor; Size.ORIGINAL a veces
                    // fuerza escalados raros en combinación con Crop y provoca recortes/descentrado.
                    .crossfade(true)
                    .build(),
                contentDescription = label,
                // Fit = mantiene proporción y evita recortes; la imagen se verá completa.
                contentScale = ContentScale.Fit
            )
        }
    }
}
