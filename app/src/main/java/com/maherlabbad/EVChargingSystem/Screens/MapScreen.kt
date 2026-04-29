package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InteractiveMapScreen() {
    // Tema Renkleri (HTML'den eşleştirildi)
    val primaryColor = Color(0xFF0058BC) // VoltCharge Blue
    val primaryContainer = Color(0xFFE0E8FF)
    val secondaryColor = Color(0xFF006E28) // Available (Green)
    val tertiaryColor = Color(0xFF9E3D00) // Occupied (Yellow/Orange)
    val errorColor = Color(0xFFBA1A1A) // Offline (Red)
    val surfaceColor = Color(0xFFF9F9FF)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)

    Scaffold(
        bottomBar = {
            VoltChargeBottomNavBar(primaryColor)
        },
        containerColor = Color(0xFFD8D9E5) // Harita yüklenene kadar arkaplan rengi
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Harita Arkaplanı (İleride buraya GoogleMap() composable'ı gelecek)
            // Şimdilik temsili bir grid veya düz renk bırakıyoruz
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE6E8F3))
            ) {
                // Temsili GPS Noktası (Blue Dot)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-20).dp, y = 20.dp)
                        .size(48.dp)
                        .background(primaryColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(12.dp).background(primaryColor, CircleShape))
                    }
                }

                // Temsili İstasyon Pinleri
                MapPin(modifier = Modifier.align(Alignment.TopCenter).offset(x = (-60).dp, y = 150.dp), color = secondaryColor, icon = Icons.Default.EvStation)
                MapPin(modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-80).dp, y = (-50).dp), color = tertiaryColor, icon = Icons.Default.EvStation)
                MapPin(modifier = Modifier.align(Alignment.BottomStart).offset(x = 80.dp, y = (-200).dp), color = errorColor, icon = Icons.Default.EvStation, alpha = 0.7f)
            }

            // 2. Üst Bar (TopAppBar - Glassmorphism hissi)
            Surface(
                color = surfaceColor.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { /* Menü */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = primaryColor)
                    }
                    Text(
                        text = "VoltCharge",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        letterSpacing = (-0.5).sp
                    )
                    // Kullanıcı Profili Placeholder
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(primaryContainer, CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = primaryColor, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // 3. Yüzen Arama Çubuğu ve Filtre (Floating Search & Filter)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = surfaceColor,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, outlineVariant),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = onSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search destinations, stations...", color = outlineVariant, fontSize = 16.sp)
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = surfaceColor,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, outlineVariant),
                    modifier = Modifier.size(48.dp)
                ) {
                    IconButton(onClick = { /* Filtre */ }) {
                        Icon(Icons.Default.Tune, contentDescription = "Filter", tint = onSurfaceVariant)
                    }
                }
            }

            // 4. Yüzen Aksiyon Butonları (Sağ Alt - Harita Kontrolleri)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 220.dp), // Kartın üstünde kalması için
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = { /* Katmanlar */ },
                    containerColor = surfaceColor,
                    contentColor = onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Layers, contentDescription = "Layers")
                }
                FloatingActionButton(
                    onClick = { /* Konumuma Git */ },
                    containerColor = surfaceColor,
                    contentColor = primaryColor,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                }
            }

            // 5. İstasyon Özet Kartı (Bottom Sheet - Floating)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = surfaceColor,
                shadowElevation = 12.dp,
                border = BorderStroke(1.dp, outlineVariant),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Drag Handle
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.width(40.dp).height(4.dp).background(outlineVariant, RoundedCornerShape(50)))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // İstasyon Başlığı ve Mesafe
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("VoltCharge Plaza", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(secondaryColor, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Available • 24/7", fontSize = 14.sp, color = onSurfaceVariant)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = primaryContainer,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = primaryColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("1.2 mi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Konektörler (Yatay Kaydırılabilir)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ConnectorCard(kw = "150kW", status = "4/6 OPEN", tint = secondaryColor, active = true)
                        ConnectorCard(kw = "50kW", status = "0/2 OPEN", tint = onSurfaceVariant, active = false)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Aksiyon Butonları
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { /* Detaylar */ },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = onSurfaceVariant)
                        ) {
                            Text("Details", fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { /* Rota Oluştur */ },
                            modifier = Modifier.weight(2f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Route", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapPin(modifier: Modifier = Modifier, color: Color, icon: ImageVector, alpha: Float = 1f) {
    Box(modifier = modifier.alpha(alpha), contentAlignment = Alignment.TopCenter) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color, CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ConnectorCard(kw: String, status: String, tint: Color, active: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (active) tint.copy(alpha = 0.5f) else Color(0xFFC1C6D7)),
        color = if (active) Color(0xFFECEDF9) else Color(0xFFF9F9FF),
        modifier = Modifier.width(130.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.EvStation, contentDescription = null, tint = tint)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(kw, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tint)
            }
        }
    }
}

@Composable
fun VoltChargeBottomNavBar(primaryColor: Color) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White.copy(alpha = 0.95f)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            NavigationBarItem(
                selected = true,
                onClick = { },
                icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                label = { Text("Map", fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = primaryColor.copy(alpha = 0.1f)
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Default.EvStation, contentDescription = "Charging") },
                label = { Text("Charging") }
            )
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                label = { Text("Wallet") }
            )
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Default.History, contentDescription = "Activity") },
                label = { Text("Activity") }
            )
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") }
            )
        }
    }
}