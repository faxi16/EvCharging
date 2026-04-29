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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WalletScreen() {
    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val primaryContainer = Color(0xFFE0E8FF)
    val secondaryColor = Color(0xFF006E28)
    val secondaryContainer = Color(0xFFD7FFDF)
    val errorColor = Color(0xFFBA1A1A)
    val errorContainer = Color(0xFFFFDAD6)
    val onErrorContainer = Color(0xFF93000A)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F3FE)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            // Özel TopAppBar (Gölgeli ve Saydam)
            Surface(
                color = surfaceColor.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp
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
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = onSurfaceVariant)
                    }
                    Text(
                        text = "VoltCharge",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        letterSpacing = (-0.5).sp
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(surfaceContainerLow, CircleShape)
                            .border(1.dp, outlineVariant, CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = outlineVariant, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        bottomBar = {
            // Şimdilik yer tutucu, daha önce yazdığımız BottomNavBar buraya eklenebilir
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Başlık Kısmı
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Wallet & Payments",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
                Text(
                    text = "Manage your balance and view recent activity.",
                    fontSize = 14.sp,
                    color = onSurfaceVariant
                )
            }

            // 2. Kredi Kartı Tarzı Bakiye Kartı
            val gradientBrush = Brush.linearGradient(
                colors = listOf(primaryColor, Color(0xFF004493))
            )
            Surface(
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(gradientBrush)
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "CURRENT BALANCE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.8f),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₺45.20", // Rapora uygun olması için ₺ yapıldı
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Nfc, // Contactless ikonu
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 40.dp, height = 24.dp)
                                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("VoltCharge Pro", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            }

                            // Top-up Butonu
                            Button(
                                onClick = { /* Bakiye Yükle */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = primaryColor
                                ),
                                shape = RoundedCornerShape(50),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Top-up", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // 3. Dinamik Ceza Uyarı Afişi (Warning Banner)
            Surface(
                color = errorContainer,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, errorColor.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = errorColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Important Notice", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = onErrorContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "No-show reservations result in dynamic penalties. Please cancel at least 15 minutes prior to your window.",
                            fontSize = 14.sp,
                            color = onErrorContainer.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 4. İşlem Geçmişi (Transaction Log)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Transaction Log",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurface
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = surfaceContainerLowest,
                    border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        TransactionItem(
                            icon = Icons.Default.EvStation, iconBg = primaryContainer, iconTint = primaryColor,
                            title = "Downtown Plaza Supercharger", subtitle = "Oct 24, 2:15 PM • 45kWh",
                            amount = "- ₺12.50", amountColor = onSurface
                        )
                        HorizontalDivider(color = outlineVariant.copy(alpha = 0.2f))

                        TransactionItem(
                            icon = Icons.Default.AccountBalanceWallet, iconBg = secondaryContainer, iconTint = secondaryColor,
                            title = "Wallet Top-up", subtitle = "Oct 20, 10:00 AM • Apple Pay",
                            amount = "+ ₺50.00", amountColor = secondaryColor
                        )
                        HorizontalDivider(color = outlineVariant.copy(alpha = 0.2f))

                        TransactionItem(
                            icon = Icons.Default.Cancel, iconBg = errorContainer, iconTint = errorColor,
                            title = "No-show Penalty", subtitle = "Oct 18, 9:30 AM • Oak St Station",
                            amount = "- ₺5.00", amountColor = errorColor
                        )
                        HorizontalDivider(color = outlineVariant.copy(alpha = 0.2f))

                        TransactionItem(
                            icon = Icons.Default.EvStation, iconBg = primaryContainer, iconTint = primaryColor,
                            title = "Westside Mall AC Fast", subtitle = "Oct 15, 6:45 PM • 22kWh",
                            amount = "- ₺8.20", amountColor = onSurface
                        )
                    }
                }

                // View All Butonu
                TextButton(
                    onClick = { /* Tüm işlemleri gör */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View All Transactions", color = primaryColor, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Alt NavBar için boşluk
        }
    }
}

@Composable
fun TransactionItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    amount: String,
    amountColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Detay Faturayı Aç */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // İkon
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Metinler
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF181C23),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF414755),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Tutar ve Sağ Ok
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = Color(0xFFC1C6D7)
            )
        }
    }
}