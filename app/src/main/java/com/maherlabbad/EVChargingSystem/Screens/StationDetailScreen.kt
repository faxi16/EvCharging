package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StationDetailScreen() {
    // Tema Renkleri
    val primaryColor = Color(0xFF0058BC)
    val primaryContainer = Color(0xFF0070EB)
    val secondaryColor = Color(0xFF006E28)
    val errorColor = Color(0xFFBA1A1A)
    val errorContainer = Color(0xFFFFDAD6)
    val onErrorContainer = Color(0xFF93000A)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceVariant = Color(0xFFE0E2ED)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineColor = Color(0xFF717786)
    val outlineVariant = Color(0xFFC1C6D7)

    Scaffold(
        bottomBar = {
            // Sabit Alt Buton (Confirm Booking)
            Surface(
                shadowElevation = 24.dp,
                color = surfaceColor.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp).padding(bottom = 8.dp)) {
                    Button(
                        onClick = { /* Rezervasyon Onay İşlemi */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Confirm Booking", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Üst Görsel Alanı (Hero Image)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(surfaceVariant) // Coil eklendiğinde buraya Image gelecek
            ) {
                // Geri Butonu
                IconButton(
                    onClick = { /* Geri Dön */ },
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 24.dp)
                        .size(40.dp)
                        .background(surfaceColor.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurface)
                }

                // Available Badge (Sol Alt)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = surfaceColor.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(secondaryColor, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Available", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = onSurface)
                    }
                }
            }

            // 2. Ana İçerik Kartı (Yukarı taşarak overlapping yapar)
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = surfaceColor,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 250.dp) // Görselin üzerine binmesi için
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .verticalScroll(rememberScrollState())
                ) {
                    // Başlık ve Fiyat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("VoltCharge Downtown Hub", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = onSurface)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = onSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("124 Tech Valley Road, 1.2 km away", fontSize = 14.sp, color = onSurfaceVariant)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                            Text("₺8.50", fontSize = 20.sp, fontWeight = FontWeight.Black, color = primaryColor)
                            Text("per kW/h", fontSize = 14.sp, color = outlineColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Konektörler Modülü
                    Text("Connectors", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Seçili (Aktif) Konektör
                        ConnectorItemCard(
                            type = "CCS2", kw = "150 kW", isSelected = true,
                            statusText = null, statusColor = null, primaryColor = primaryColor, outlineVariant = outlineVariant
                        )
                        // Arızalı/Dolu Konektör
                        ConnectorItemCard(
                            type = "CHAdeMO", kw = "50 kW", isSelected = false,
                            statusText = "In Use", statusColor = errorColor, primaryColor = primaryColor, outlineVariant = outlineVariant
                        )
                        // Normal Konektör
                        ConnectorItemCard(
                            type = "Type 2", kw = "22 kW", isSelected = false,
                            statusText = null, statusColor = null, primaryColor = primaryColor, outlineVariant = outlineVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = surfaceVariant)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Saat Seçimi Modülü
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Select Time Slot", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
                        Text("Max 2h", fontSize = 14.sp, color = outlineColor)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gün Sekmeleri
                    Surface(
                        color = Color(0xFFECEDF9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            Surface(
                                color = surfaceColor,
                                shape = RoundedCornerShape(6.dp),
                                shadowElevation = 1.dp,
                                modifier = Modifier.weight(1f).padding(2.dp)
                            ) {
                                Text("Today", textAlign = TextAlign.Center, color = primaryColor, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                            }
                            Text(
                                "Tomorrow",
                                textAlign = TextAlign.Center,
                                color = onSurfaceVariant,
                                modifier = Modifier.weight(1f).padding(vertical = 8.dp).clickable { }
                            )
                        }
                    }

                    // Saat Grid'i (2 Satır, 4 Sütun)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            TimeSlotItem("10:00", state = "Disabled", modifier = Modifier.weight(1f), primaryColor = primaryColor, outlineVariant = outlineVariant)
                            TimeSlotItem("10:30", state = "Disabled", modifier = Modifier.weight(1f), primaryColor = primaryColor, outlineVariant = outlineVariant)
                            TimeSlotItem("11:00", state = "Selected", modifier = Modifier.weight(1f), primaryColor = primaryColor, outlineVariant = outlineVariant)
                            TimeSlotItem("11:30", state = "Selected", modifier = Modifier.weight(1f), primaryColor = primaryColor, outlineVariant = outlineVariant)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            TimeSlotItem("12:00", state = "Available", modifier = Modifier.weight(1f), primaryColor = primaryColor, outlineVariant = outlineVariant)
                            TimeSlotItem("12:30", state = "Available", modifier = Modifier.weight(1f), primaryColor = primaryColor, outlineVariant = outlineVariant)
                            TimeSlotItem("13:00", state = "Available", modifier = Modifier.weight(1f), primaryColor = primaryColor, outlineVariant = outlineVariant)
                            TimeSlotItem("13:30", state = "Available", modifier = Modifier.weight(1f), primaryColor = primaryColor, outlineVariant = outlineVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Minimum Bakiye Uyarı Çubuğu (DR05)
                    Surface(
                        color = errorContainer,
                        border = BorderStroke(1.dp, errorColor.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = onErrorContainer, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Min 50 TL balance required for booking. Current balance might be insufficient.",
                                color = onErrorContainer,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp)) // Alt bar için ekstra boşluk
                }
            }
        }
    }
}

@Composable
fun ConnectorItemCard(
    type: String, kw: String, isSelected: Boolean, statusText: String?,
    statusColor: Color?, primaryColor: Color, outlineVariant: Color
) {
    val borderColor = if (isSelected) primaryColor else outlineVariant
    val iconTint = if (isSelected) primaryColor else outlineVariant
    val opacity = if (statusText == "In Use") 0.6f else 1f

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        color = Color.White,
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp).fillMaxWidth().alpha(opacity)
        ) {
            Icon(Icons.Default.EvStation, contentDescription = null, tint = iconTint, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(type, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = Color(0xFFECEDF9),
                shape = RoundedCornerShape(50),
            ) {
                Text(kw, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontWeight = FontWeight.SemiBold)
            }
            if (statusText != null && statusColor != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(statusText, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TimeSlotItem(time: String, state: String, modifier: Modifier = Modifier, primaryColor: Color, outlineVariant: Color) {
    val bgColor = when (state) {
        "Selected" -> primaryColor.copy(alpha = 0.1f)
        "Disabled" -> outlineVariant.copy(alpha = 0.2f)
        else -> Color.White
    }
    val borderColor = when (state) {
        "Selected" -> primaryColor
        "Disabled" -> outlineVariant.copy(alpha = 0.5f)
        else -> outlineVariant
    }
    val textColor = when (state) {
        "Selected" -> primaryColor
        "Disabled" -> outlineVariant
        else -> Color(0xFF181C23)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(if (state == "Selected") 2.dp else 1.dp, borderColor),
        modifier = modifier.height(40.dp).clickable(enabled = state != "Disabled") { }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(time, color = textColor, fontWeight = if (state == "Selected") FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
        }
    }
}