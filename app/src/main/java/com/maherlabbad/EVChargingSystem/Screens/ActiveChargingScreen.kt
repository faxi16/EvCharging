package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActiveChargingScreen() {
    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val primaryContainer = Color(0xFF0070EB)
    val primaryFixedDim = Color(0xFFADC6FF)
    val secondaryColor = Color(0xFF006E28) // Yeşil - Şarj aktif
    val secondaryContainer = Color(0xFF6FFB85)
    val errorColor = Color(0xFFBA1A1A) // Kırmızı - Durdurma
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F3FE)
    val surfaceContainer = Color(0xFFECEDF9)
    val surfaceVariant = Color(0xFFE0E2ED)
    val outlineVariant = Color(0xFFC1C6D7)
    val outline = Color(0xFF717786)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            // Şeffaf, ortası rozetli Top Bar
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
                    IconButton(
                        onClick = { /* Küçült */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(surfaceContainerLow, CircleShape)
                    ) {
                        Icon(Icons.Default.ExpandMore, contentDescription = "Minimize", tint = onSurface)
                    }

                    // Charging Pill (Rozet)
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = secondaryContainer.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, secondaryContainer.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Charging...", color = secondaryColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }

                    IconButton(onClick = { /* Menü */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = outline)
                    }
                }
            }
        },
        bottomBar = {
            // Yüzen gradient arkaplanlı Durdurma Butonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, surfaceColor, surfaceColor)
                        )
                    )
                    .padding(16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = { /* Şarjı Bitir (FR14) */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = errorColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop Charging", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Dairesel Şarj Göstergesi
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = surfaceContainerLowest,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Dairesel Progress Çizimi (Canvas)
                    val progress = 0.78f // %78
                    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                            // Arka plan çemberi (Gri)
                            drawArc(
                                color = surfaceVariant,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // İlerleme çemberi (Yeşil)
                            drawArc(
                                color = secondaryColor,
                                startAngle = -90f,
                                sweepAngle = 360f * progress,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        // İç Metinler
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("78%", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = onSurface)
                            Text("64 kW output", fontSize = 14.sp, color = onSurfaceVariant)
                        }
                    }

                    // Alt İstasyon Durum Rozeti
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = surfaceContainerLow,
                        border = BorderStroke(1.dp, surfaceVariant),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(secondaryColor, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("STATION DC-04 ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. İstatistik Kartları (Energy & Time)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceContainerLowest,
                border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.2f)),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min) // Dikey çizginin tam boy olmasını sağlar
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sol Taraf: Tüketilen Enerji
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(primaryColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = primaryColor)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Energy Delivered", fontSize = 12.sp, color = outline)
                            Text("32.4 kWh", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurface)
                        }
                    }

                    // Orta Çizgi
                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(outlineVariant.copy(alpha = 0.3f)))

                    // Sağ Taraf: Kalan Süre
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Text("Est. Time Left", fontSize = 12.sp, color = outline)
                        Text("1h 15m", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Canlı Maliyet (Live Cost)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceContainerLowest,
                border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.2f)),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(surfaceContainerLowest, surfaceContainerLow)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(surfaceVariant, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Live Cost", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
                        }
                        Text("₺14.80", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. İstasyon Aksiyonları Ayracı
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(outlineVariant.copy(alpha = 0.3f)))
                Text("STATION ACTIONS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = outline, modifier = Modifier.padding(horizontal = 12.dp))
                Box(modifier = Modifier.weight(1f).height(1.dp).background(outlineVariant.copy(alpha = 0.3f)))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Aksiyon Butonları (Grid Yapısı)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // QR Kod Tarayıcı (Tam Genişlik)
                Surface(
                    onClick = { /* QR Kod Okuyucu Aç */ },
                    shape = RoundedCornerShape(16.dp),
                    color = primaryColor,
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Scan QR Code", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Text("Identify new station", fontSize = 14.sp, color = primaryFixedDim)
                            }
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = primaryFixedDim)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    // Kabloyu Tak (Yarı Genişlik)
                    Surface(
                        onClick = { /* Handshake Simülasyonu */ },
                        shape = RoundedCornerShape(16.dp),
                        color = surfaceContainerLowest,
                        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f).height(110.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(surfaceContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Handshake, contentDescription = null, tint = onSurfaceVariant, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Plug in Cable", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
                        }
                    }

                    // Şarjı Başlat (Yarı Genişlik)
                    Surface(
                        onClick = { /* Şarjı Başlat */ },
                        shape = RoundedCornerShape(16.dp),
                        color = surfaceContainerLowest,
                        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f).height(110.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(surfaceContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Power, contentDescription = null, tint = onSurfaceVariant, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Start Charging", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // Alttaki durdurma butonu içeriği kapatmasın diye
        }
    }
}