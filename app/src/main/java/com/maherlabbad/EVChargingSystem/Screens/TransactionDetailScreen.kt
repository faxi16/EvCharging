package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransactionDetailsScreen() {
    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val secondaryContainer = Color(0xFFD7FFDF)
    val onSecondaryContainer = Color(0xFF006E28)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainer = Color(0xFFECEDF9)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)
    val outline = Color(0xFF717786)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            // Şeffaf Gölgeli TopAppBar
            Surface(
                color = surfaceContainerLowest.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Geri Dön */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurface)
                    }
                    Text(
                        text = "Transaction Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurface,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // Ortalamak için sağa boşluk eklendi
                }
            }
        },
        bottomBar = {
            // Alt Sabit Butonlar
            Surface(
                color = surfaceContainerLowest,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Download PDF Butonu
                    Button(
                        onClick = { /* PDF İndir (FR18) */ },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download PDF", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Share Receipt Butonu
                    OutlinedButton(
                        onClick = { /* Fişi Paylaş */ },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, outlineVariant),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Receipt", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Dijital Fatura Kartı
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = surfaceContainerLowest,
                border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.5f)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Üst Mavi Dekoratif Çizgi
                    Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(primaryColor))

                    // Başlık (Success & Amount)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(64.dp).background(secondaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = onSecondaryContainer, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "PAYMENT SUCCESSFUL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "₺12.50",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "VoltCharge Plaza",
                            fontSize = 14.sp,
                            color = outline
                        )
                    }

                    // Kesik Çizgi Ayracı
                    DashedDivider(color = outlineVariant)

                    // Fatura Detayları
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ReceiptDetailRow(label = "Station", value = "VoltCharge Plaza")
                        ReceiptDetailRow(label = "Date", value = "25 Oct 2026") // Proje tarihine uyarlandı
                        ReceiptDetailRow(label = "Time", value = "14:30")
                        ReceiptDetailRow(label = "Energy", value = "45 kWh")
                        ReceiptDetailRow(label = "Duration", value = "42 mins")
                        ReceiptDetailRow(label = "Transaction ID", value = "#VC-882910")
                    }

                    // Kesik Çizgi Ayracı
                    DashedDivider(color = outlineVariant)

                    // Alt Kısım: Toplam Tutar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(surfaceContainer)
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
                        Text("₺12.50", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Alttaki butonların arkasında kalmaması için
        }
    }
}

// Fatura Satırları için Helper Composable
@Composable
fun ReceiptDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF414755))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF181C23))
    }
}

// Kesik Çizgi Çizen Composable (Dashed Divider)
@Composable
fun DashedDivider(color: Color) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxWidth().height(1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
        )
    }
}