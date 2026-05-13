package com.maherlabbad.EVChargingSystem.Screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.maherlabbad.EVChargingSystem.CurrentLocation
import com.maherlabbad.EVChargingSystem.Viewmodels.FavoriteStationsViewModel

@Composable
fun FavoriteStationsScreen(
    onBack: () -> Unit,
    onNavigateToStationDetail: (String) -> Unit,
    viewModel: FavoriteStationsViewModel = viewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Kullanıcının anlık konumunu tutacağımız değişkenler
    val userLat by CurrentLocation.latitude.collectAsState()
    val userLng by CurrentLocation.longitude.collectAsState()

    // Sayfa açıldığında verileri yükle
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }


    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val secondaryColor = Color(0xFF006E28)
    val secondaryContainer = Color(0xFFD7FFDF)
    val onSecondaryContainer = Color(0xFF00531C)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F3FE)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)
    val outline = Color(0xFF717786)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            Surface(
                color = surfaceContainerLowest.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VoltCharge",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        letterSpacing = (-0.5).sp
                    )
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No saved stations found.", color = onSurfaceVariant)
            }
        } else {
            // LazyColumn ile dinamik listeleme
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Başlık
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text("Saved Locations", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Manage your favorite stations", fontSize = 14.sp, color = onSurfaceVariant)
                    }
                }

                items(favorites) { favData ->
                    val distance = calculateDistanceToStation(
                        userLat = userLat,
                        userLng = userLng,
                        stationLocationStr = favData.station.location
                    )
                    FavoriteStationCard(
                        name = favData.station.name,
                        distance = distance, // Konum (Location) mantığı eklenince dinamik hesaplanabilir
                        availableUnits = favData.availableUnits,
                        totalUnits = favData.totalUnits,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        secondaryContainer = secondaryContainer,
                        onSecondaryContainer = onSecondaryContainer,
                        surfaceContainerLowest = surfaceContainerLowest,
                        surfaceContainerLow = surfaceContainerLow,
                        outlineVariant = outlineVariant,
                        outline = outline,
                        onSurface = onSurface,
                        onSurfaceVariant = onSurfaceVariant,
                        onCardClick = { onNavigateToStationDetail(favData.station.stationID) },
                        onRemoveFavorite = { viewModel.removeFavorite(favData.station.stationID) },
                        location = favData.station.location
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteStationCard(
    name: String, distance: String, availableUnits: Int, totalUnits: Int,
    primaryColor: Color, secondaryColor: Color, secondaryContainer: Color, onSecondaryContainer: Color,
    surfaceContainerLowest: Color, surfaceContainerLow: Color, outlineVariant: Color, outline: Color,
    onSurface: Color, onSurfaceVariant: Color,
    onCardClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    location: String
) {
    val context = LocalContext.current
    val isAvailable = availableUnits > 0
    val opacity = if (isAvailable) 1f else 0.75f

    val badgeBgColor = if (isAvailable) secondaryContainer.copy(alpha = 0.5f) else outlineVariant.copy(alpha = 0.3f)
    val badgeTextColor = if (isAvailable) onSecondaryContainer else onSurfaceVariant
    val dotColor = if (isAvailable) secondaryColor else outline

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceContainerLowest,
        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(opacity)
            .clickable { onCardClick() }
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            // Sağ Üst Kalp İkonu (Tıklanabilir - Favoriden Çıkar)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .background(surfaceContainerLowest.copy(alpha = 0.8f), CircleShape)
                    .border(1.dp, outlineVariant.copy(alpha = 0.2f), CircleShape)
                    .clickable { onRemoveFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Remove Favorite", tint = primaryColor, modifier = Modifier.size(18.dp))
            }

            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(surfaceContainerLow)
                            .border(1.dp, outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = outlineVariant, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f).padding(end = 24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(distance, fontSize = 13.sp, color = onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = badgeBgColor
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(dotColor, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$availableUnits/$totalUnits Available",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        val coordinates = location.split(",")
                        if (coordinates.size == 2) {
                            val lat = coordinates[0].trim().toDoubleOrNull() ?: 0.0
                            val lng = coordinates[1].trim().toDoubleOrNull() ?: 0.0
                            val uri = "google.navigation:q=${lat},${lng}&mode=d".toUri() // mode=d (Driving)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            // Eğer telefonda maps yoksa çökmesin diye kontrol
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                // Maps yoksa tarayıcıdan aç
                                context.startActivity(Intent(Intent.ACTION_VIEW,
                                    "https://maps.google.com/?daddr=${lat},${lng}".toUri()))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Get Directions", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}