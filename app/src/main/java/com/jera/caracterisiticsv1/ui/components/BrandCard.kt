package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.ui.theme.*

data class BrandInfo(
    val name: String,
    val carCount: Int,
    val logoRes: Int?
)

@Composable
fun BrandCard(
    brand: BrandInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .border(1.dp, AccentPrimary.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        backgroundColor = SurfaceColor,
        elevation = 6.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo o inicial de la marca
                if (brand.logoRes != null) {
                    Image(
                        painter = painterResource(id = brand.logoRes),
                        contentDescription = brand.name,
                        modifier = Modifier
                            .size(56.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = AccentPrimary.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = brand.name.take(2).uppercase(),
                            fontFamily = Poppins,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberYellow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Nombre de la marca
                Text(
                    text = brand.name,
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFEEEEEE),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Badge con número de coches
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .background(
                        color = CyberAmber,
                        shape = CircleShape
                    )
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = brand.carCount.toString(),
                    fontFamily = Poppins,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }
        }
    }
}
