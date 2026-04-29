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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun MyVehiclesScreen() {
    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val primaryContainer = Color(0xFFE0E8FF)
    val secondaryColor = Color(0xFF006E28)
    val tertiaryColor = Color(0xFF9E3D00)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F3FE)
    val surfaceContainer = Color(0xFFECEDF9)
    val surfaceContainerHigh = Color(0xFFE6E8F3)
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
                        IconButton(
                            onClick = { /* Geri Dön */ },
                            modifier = Modifier
                                .size(36.dp)
                                .background(surfaceContainerHigh, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceVariant, modifier = Modifier.size(20.dp))
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
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = primaryColor)
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

            // Başlık
            Column {
                Text("My Vehicles", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Manage your registered EVs", fontSize = 14.sp, color = onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 1. Araç Kartı (Default)
            VehicleCard(
                brandModel = "Tesla Model 3",
                plate = "35 ABC 123",
                capacity = "60 kWh",
                connector = "Type-2",
                isDefault = true,
                connectorColor = secondaryColor,
                primaryColor = primaryColor,
                surfaceContainerLowest = surfaceContainerLowest,
                surfaceContainerLow = surfaceContainerLow,
                surfaceContainer = surfaceContainer,
                outlineVariant = outlineVariant,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
                primaryContainer = primaryContainer
            )

            // 2. Araç Kartı (İkincil)
            VehicleCard(
                brandModel = "Renault Zoe",
                plate = "34 XYZ 789",
                capacity = "52 kWh",
                connector = "CCS",
                isDefault = false,
                connectorColor = tertiaryColor,
                primaryColor = primaryColor,
                surfaceContainerLowest = surfaceContainerLowest,
                surfaceContainerLow = surfaceContainerLow,
                surfaceContainer = surfaceContainer,
                outlineVariant = outlineVariant,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
                primaryContainer = primaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Yeni Araç Ekle Butonu (Dashed Border)
            val stroke = Stroke(
                width = 4f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(surfaceContainerLow.copy(alpha = 0.5f))
                    .drawBehind {
                        drawRoundRect(
                            color = outlineVariant.copy(alpha = 0.6f),
                            style = stroke,
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    }
                    .clickable { /* Yeni Araç Ekleme Ekranına Git */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = primaryColor, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("+ Add New Vehicle", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Alttaki NavBar için boşluk
        }
    }
}

@Composable
fun VehicleCard(
    brandModel: String, plate: String, capacity: String, connector: String,
    isDefault: Boolean, connectorColor: Color, primaryColor: Color,
    surfaceContainerLowest: Color, surfaceContainerLow: Color, surfaceContainer: Color,
    outlineVariant: Color, onSurface: Color, onSurfaceVariant: Color, primaryContainer: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceContainerLowest,
        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().clickable { /* Araç Detayı */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Kısım: Araç İkonu/Resmi
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(surfaceContainerLow, RoundedCornerShape(12.dp))
                    .border(1.dp, outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Şimdilik Placeholder Icon
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = onSurfaceVariant, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Sağ Kısım: Araç Bilgileri
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(text = brandModel, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Özel Plaka Tasarımı (License Plate)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = surfaceContainerLowest,
                            border = BorderStroke(1.dp, outlineVariant),
                            shadowElevation = 1.dp
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.width(8.dp).height(24.dp).background(primaryColor)) // TR Mavi Şerit Hissi
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = plate,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }

                    if (isDefault) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = primaryContainer,
                            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "Default Vehicle",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Etiketler (Chips) - Batarya ve Konektör
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = surfaceContainer,
                        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = primaryColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(capacity, fontSize = 13.sp, color = onSurfaceVariant)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = surfaceContainer,
                        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EvStation, contentDescription = null, tint = connectorColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(connector, fontSize = 13.sp, color = onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}