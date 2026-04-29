package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FavoriteStationsScreen() {
    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val secondaryColor = Color(0xFF006E28)
    val secondaryContainer = Color(0xFFD7FFDF)
    val onSecondaryContainer = Color(0xFF00531C)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F3FE)
    val surfaceContainerHighest = Color(0xFFE0E2ED)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)
    val outline = Color(0xFF717786)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            // Beyaz Saydam TopAppBar
            Surface(
                color = surfaceContainerLowest.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(surfaceContainerHighest, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = onSurfaceVariant, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "VoltCharge",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    IconButton(onClick = { /* Bildirimler */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = outline)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Başlık ve Filtre
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Saved Locations", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Manage your favorite stations", fontSize = 14.sp, color = onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = primaryColor.copy(alpha = 0.1f),
                    modifier = Modifier.clickable { /* Filtre Menüsü */ }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Filter", color = primaryColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 1. İstasyon (Müsait)
            FavoriteStationCard(
                name = "Downtown Hub Plaza",
                distance = "2.4 km away",
                availableUnits = 3,
                totalUnits = 4,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                surfaceContainerLowest = surfaceContainerLowest,
                surfaceContainerLow = surfaceContainerLow,
                outlineVariant = outlineVariant,
                outline = outline,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant
            )

            // 2. İstasyon (Müsait)
            FavoriteStationCard(
                name = "Westside Metro Mall",
                distance = "5.1 km away",
                availableUnits = 8,
                totalUnits = 12,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                surfaceContainerLowest = surfaceContainerLowest,
                surfaceContainerLow = surfaceContainerLow,
                outlineVariant = outlineVariant,
                outline = outline,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant
            )

            // 3. İstasyon (Dolu/Pasif)
            FavoriteStationCard(
                name = "FreshMarket Local",
                distance = "8.7 km away",
                availableUnits = 0,
                totalUnits = 2,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                surfaceContainerLowest = surfaceContainerLowest,
                surfaceContainerLow = surfaceContainerLow,
                outlineVariant = outlineVariant,
                outline = outline,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(100.dp)) // Alttaki NavBar için boşluk
        }
    }
}

@Composable
fun FavoriteStationCard(
    name: String, distance: String, availableUnits: Int, totalUnits: Int,
    primaryColor: Color, secondaryColor: Color, secondaryContainer: Color, onSecondaryContainer: Color,
    surfaceContainerLowest: Color, surfaceContainerLow: Color, outlineVariant: Color, outline: Color,
    onSurface: Color, onSurfaceVariant: Color
) {
    val isAvailable = availableUnits > 0
    val opacity = if (isAvailable) 1f else 0.75f // Dolu ise kartı hafif soluk yapıyoruz

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
            .clickable { /* Detay Sayfasına Git */ }
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            // Sağ Üst Kalp İkonu (Absolute)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .background(surfaceContainerLowest.copy(alpha = 0.8f), CircleShape)
                    .border(1.dp, outlineVariant.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = primaryColor, modifier = Modifier.size(18.dp))
            }

            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Resim Placeholder
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(surfaceContainerLow)
                            .border(1.dp, outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // İleride Coil ile buraya Image() eklenebilir
                        Icon(Icons.Default.Image, contentDescription = null, tint = outlineVariant, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // İstasyon Detayları
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 24.dp), // Kalp ikonu için sağdan boşluk
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

                        // Availability Badge
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

                // Yol Tarifi (Get Directions) Butonu
                OutlinedButton(
                    onClick = { /* Harita/Navigasyon Aç */ },
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