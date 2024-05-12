package com.jera.caracterisiticsv1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    // Lista de opciones de configuración
    val settingsOptions = listOf(
        "Permisos",
        "Ubicación para guardar fotos",
        "Iniciar sesión",
        "Cerrar sesión",
        "Información",
        "Referencia Api IA",
        "Créditos"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Ajustes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn {
            items(settingsOptions) { item ->
                SettingItem(item)
                Divider()
            }
        }
    }
}

@Composable
fun SettingItem(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSettingClicked(text) }
            .padding(vertical = 12.dp)
    )
}

fun onSettingClicked(settingName: String) {
    // Aquí puedes añadir la lógica para manejar clics en cada elemento.
    // Por ejemplo, puedes mostrar un toast, abrir una nueva pantalla, etc.
    println("Clicked on setting: $settingName")  // Reemplaza esto con tu lógica
}