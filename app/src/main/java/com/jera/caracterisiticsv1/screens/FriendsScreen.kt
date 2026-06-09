package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.repository.FirestoreRepository
import com.jera.caracterisiticsv1.repository.FirestoreUserProfile
import com.jera.caracterisiticsv1.repository.FriendData
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.FriendsViewModel

@Composable
fun FriendsScreen(
    navController: NavController,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDark)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceColor)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = AccentPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "// AMIGOS",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AccentPrimary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "${uiState.friends.size} siguiendo",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Box(Modifier.fillMaxWidth().height(1.dp).background(AccentPrimary.copy(alpha = 0.5f)))

        // ── Buscador por email ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceColor)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "AÑADIR AMIGO",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = CyberYellow,
                letterSpacing = 2.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchEmail,
                    onValueChange = { viewModel.onSearchEmailChange(it) },
                    placeholder = {
                        Text("Email de Google...", fontFamily = Poppins, fontSize = 13.sp, color = TextSecondary)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                        viewModel.searchByEmail()
                    }),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = TextColor,
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = SurfaceLight.copy(alpha = 0.3f),
                        cursorColor = AccentPrimary,
                        backgroundColor = SurfaceVariant,
                        placeholderColor = TextSecondary,
                        leadingIconColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        color = TextColor
                    )
                )
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.searchByEmail()
                    },
                    enabled = !uiState.isSearching,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary.copy(0.2f),
                        contentColor = AccentPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (uiState.isSearching) {
                        CircularProgressIndicator(color = AccentPrimary, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    } else {
                        Text("BUSCAR", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            uiState.searchError?.let {
                Text(it, fontFamily = Poppins, fontSize = 12.sp, color = Color(0xFFFF6666))
            }

            uiState.searchResult?.let { found ->
                SearchResultCard(
                    profile = found,
                    isAlreadyFollowing = viewModel.isFollowing(found.uid),
                    isLoading = uiState.isFollowLoading,
                    onFollow = { viewModel.followUser(found.uid) }
                )
            }
        }

        Box(Modifier.fillMaxWidth().height(1.dp).background(AccentPrimary.copy(alpha = 0.3f)))

        // ── Lista de amigos ───────────────────────────────────────────────────
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPrimary, strokeWidth = 2.dp, modifier = Modifier.size(40.dp))
                }
            }

            uiState.friends.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(Icons.Default.Person, null, tint = AccentPrimary.copy(0.4f), modifier = Modifier.size(56.dp))
                        Text("Aún no sigues a nadie", fontFamily = Poppins, fontSize = 13.sp, color = TextSecondary)
                        Text(
                            "Busca a tus amigos por su email de Google",
                            fontFamily = Poppins, fontSize = 12.sp, color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.friends, key = { it.profile.uid }) { friend ->
                        FriendCard(
                            friend = friend,
                            onUnfollow = { viewModel.unfollowUser(friend.profile.uid) }
                        )
                    }
                }
            }
        }
    }
}

// ─── Card de resultado de búsqueda ────────────────────────────────────────────
@Composable
private fun SearchResultCard(
    profile: FirestoreUserProfile,
    isAlreadyFollowing: Boolean,
    isLoading: Boolean,
    onFollow: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceVariant)
            .border(1.dp, AccentPrimary.copy(alpha = 0.5f), shape)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceColor),
            contentAlignment = Alignment.Center
        ) {
            if (profile.photoUrl.isNotBlank()) {
                AsyncImage(model = profile.photoUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Text(
                    text = profile.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AccentPrimary
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(profile.username, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextColor)
            Text("Nv. ${profile.level} · ${profile.carsCollected} coches", fontFamily = Poppins, fontSize = 11.sp, color = TextSecondary)
        }
        if (isAlreadyFollowing) {
            Text("Ya le sigues", fontFamily = Poppins, fontSize = 11.sp, color = TextSecondary)
        } else {
            Button(
                onClick = onFollow,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary.copy(0.2f), contentColor = AccentPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = AccentPrimary, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                } else {
                    Text("SEGUIR", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─── Card de amigo con expositor expandible ───────────────────────────────────
@Composable
private fun FriendCard(
    friend: FriendData,
    onUnfollow: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceColor)
            .border(1.dp, SurfaceLight.copy(alpha = 0.2f), shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant)
                    .border(1.dp, AccentPrimary.copy(0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (friend.profile.photoUrl.isNotBlank()) {
                    AsyncImage(model = friend.profile.photoUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Text(
                        text = friend.profile.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AccentPrimary
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.profile.username,
                    fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextColor,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Nv. ${friend.profile.level}  ·  ${friend.profile.carsCollected} coches  ·  ${friend.profile.totalXp} XP",
                    fontFamily = Poppins, fontSize = 11.sp, color = AccentPrimary
                )
            }
            Text(
                text = if (expanded) "▲" else "▼",
                fontFamily = Poppins, fontSize = 12.sp, color = TextSecondary
            )
        }

        if (expanded) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(SurfaceLight.copy(alpha = 0.15f)))

            if (friend.showcase.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Este usuario aún no ha configurado su expositor",
                        fontFamily = Poppins, fontSize = 12.sp, color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val slots: List<FirestoreRepository.ShowcaseCar?> =
                        friend.showcase + List(3 - friend.showcase.size) { null as FirestoreRepository.ShowcaseCar? }
                    slots.forEach { showcaseCar ->
                        if (showcaseCar != null) {
                            ShowcaseCarTile(car = showcaseCar, modifier = Modifier.weight(1f))
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(8.dp)).background(SurfaceVariant))
                        }
                    }
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(SurfaceLight.copy(alpha = 0.1f)))
            TextButton(onClick = onUnfollow, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Dejar de seguir", fontFamily = Poppins, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun ShowcaseCarTile(
    car: FirestoreRepository.ShowcaseCar,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(SurfaceVariant)
            .border(1.dp, AccentPrimary.copy(alpha = 0.3f), shape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(SurfaceColor),
            contentAlignment = Alignment.Center
        ) {
            if (car.storageUrl.isNotBlank()) {
                AsyncImage(
                    model = car.storageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.car_in_garage),
                    contentDescription = null,
                    tint = SurfaceLight.copy(alpha = 0.3f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp)) {
            Text(text = car.modelName, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = TextColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = car.makeName, fontFamily = Poppins, fontSize = 8.sp, color = AccentPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (car.years.isNotBlank()) {
                Text(car.years, fontFamily = Poppins, fontSize = 7.sp, color = TextSecondary, maxLines = 1)
            }
        }
    }
}
