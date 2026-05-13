package com.maherlabbad.EVChargingSystem.Screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maherlabbad.EVChargingSystem.Screen
import com.maherlabbad.EVChargingSystem.Viewmodels.ActiveChargingViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.ChargingState
import com.maherlabbad.EVChargingSystem.VoltChargeBottomNavBar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActiveChargingScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToProfile: () -> Unit,
    chargingViewModel: ActiveChargingViewModel
) {
    // --- ViewModel State'lerini Dinle ---
    val chargingState by chargingViewModel.chargingState.collectAsState()
    val batteryPercentage by chargingViewModel.batteryPercentage.collectAsState()
    val currentSession by chargingViewModel.currentSession.collectAsState()
    val addedKwh by chargingViewModel.addedKwh.collectAsState()
    val currentCost by chargingViewModel.currentCost.collectAsState()
    val elapsedTimeSeconds by chargingViewModel.elapsedTimeSeconds.collectAsState()
    val errorMessage by chargingViewModel.errorMessage.collectAsState()
    val isQrVerified by chargingViewModel.isQrVerified.collectAsState()
    val isCablePlugged by chargingViewModel.isCablePlugged.collectAsState()
    val hasActiveReservation by chargingViewModel.hasActiveReservation.collectAsState()
    val isLoading by chargingViewModel.isLoading.collectAsState()
    
    // Faz 1 & 2: Dinamik Değerler
    val unitPrice by chargingViewModel.activeChargerUnitPrice.collectAsState()
    val userBalance by chargingViewModel.userBalance.collectAsState()

    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
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

    LaunchedEffect(Unit) {
        chargingViewModel.loadActiveReservationAndSession()
    }

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            Surface(
                color = surfaceContainerLowest.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
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
                            val statusLabel = if(chargingState == ChargingState.CHARGING) "Charging..." else "Ready"
                            Text(statusLabel, color = secondaryColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                    
                    // Bakiyeyi sağ üstte göster
                    Text(
                        text = "Balance: ₺${String.format(Locale.US, "%.2f", userBalance)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceVariant
                    )
                }
            }
        },
        bottomBar = {
            VoltChargeBottomNavBar(
                currentRoute = Screen.ActiveCharging.route,
                onNavigateToMap = onNavigateToMap,
                onNavigateToCharging = { /* Already here */ },
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToActivity = onNavigateToActivity,
                onNavigateToProfile = onNavigateToProfile
            )
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
            if (!errorMessage.isNullOrEmpty()) {
                Surface(
                    color = errorColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = errorColor,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

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
                    val progress = batteryPercentage / 100f
                    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = surfaceVariant,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = if (chargingState == ChargingState.CHARGING) secondaryColor else outlineVariant,
                                startAngle = -90f,
                                sweepAngle = 360f * progress,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${batteryPercentage}%", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = onSurface)
                            val statusText = if (chargingState == ChargingState.CHARGING) "64 kW output" else "Not Charging"
                            Text(statusText, fontSize = 14.sp, color = onSurfaceVariant)
                        }
                    }

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
                            val statusColor = if (chargingState == ChargingState.CHARGING) secondaryColor else outlineVariant
                            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            val statusText = if (chargingState == ChargingState.CHARGING) "ACTIVE" else "READY"
                            val displayId = currentSession?.reservationID?.take(6)?.uppercase() ?: "UNKNOWN"
                            Text(
                                text = "RES-$displayId $statusText",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. İstatistik Kartları (Energy & Unit Price)
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
                        .height(IntrinsicSize.Min)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            Text("${addedKwh} kWh", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurface)
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(outlineVariant.copy(alpha = 0.3f)))

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Text("Unit Price", fontSize = 12.sp, color = outline)
                        Text("₺${String.format(Locale.US, "%.2f", unitPrice)}/kWh", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Canlı Maliyet (Live Cost) & Faz 2: Circuit Breaker UI
            val balanceRatio = if (userBalance > 0) (currentCost / userBalance).toFloat() else 0f
            val isWarning = balanceRatio >= 0.9f
            
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceContainerLowest,
                border = BorderStroke(
                    width = if (isWarning) 2.dp else 1.dp,
                    color = if (isWarning) errorColor else outlineVariant.copy(alpha = 0.2f)
                ),
                shadowElevation = if (isWarning) 4.dp else 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(surfaceContainerLowest, if (isWarning) errorColor.copy(alpha = 0.05f) else surfaceContainerLow)
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
                                    .background(if (isWarning) errorColor.copy(alpha = 0.1f) else surfaceVariant, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Payments, 
                                    contentDescription = null, 
                                    tint = if (isWarning) errorColor else onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Live Cost", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = onSurface)
                        }
                        Text("₺${String.format(Locale.US, "%.2f", currentCost)}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = if (isWarning) errorColor else onSurface)
                    }
                    
                    if (chargingState == ChargingState.CHARGING) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Faz 2: LinearProgressIndicator (Harcanan paranın toplam bakiyeye oranı)
                        LinearProgressIndicator(
                            progress = { balanceRatio.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = if (isWarning) errorColor else primaryColor,
                            trackColor = outlineVariant.copy(alpha = 0.2f),
                        )
                        
                        if (isWarning) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Warning: Approaching balance limit!",
                                color = errorColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    chargingViewModel.stopChargingAndPay()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = errorColor,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(50),
                enabled = chargingState == ChargingState.CHARGING,
            ) {
                Icon(Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop Charging", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(outlineVariant.copy(alpha = 0.3f)))
                Text("STATION ACTIONS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = outline, modifier = Modifier.padding(horizontal = 12.dp))
                Box(modifier = Modifier.weight(1f).height(1.dp).background(outlineVariant.copy(alpha = 0.3f)))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    onClick = { chargingViewModel.verifyQrCode() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isQrVerified) secondaryColor else primaryColor,
                    enabled = hasActiveReservation && !isQrVerified && chargingState == ChargingState.IDLE,
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
                                Icon(
                                    imageVector = if (isQrVerified) Icons.Default.CheckCircle else Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = if (isQrVerified) "QR Verified" else "Scan QR Code",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isQrVerified) "Station confirmed" else "Identify station",
                                    fontSize = 14.sp,
                                    color = if (isQrVerified) secondaryContainer else primaryFixedDim
                                )
                            }
                        }
                        if (!isQrVerified) {
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = primaryFixedDim)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        onClick = { chargingViewModel.plugCable() },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCablePlugged) secondaryColor.copy(alpha = 0.1f) else surfaceContainerLowest,
                        border = BorderStroke(1.dp, if (isCablePlugged) secondaryColor else outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f).height(110.dp),
                        enabled = hasActiveReservation && isQrVerified && !isCablePlugged && chargingState == ChargingState.IDLE
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(if (isCablePlugged) secondaryColor else surfaceContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cable,
                                    contentDescription = null,
                                    tint = if (isCablePlugged) Color.White else onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isCablePlugged) "Cable Plugged" else "Plug in Cable",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCablePlugged) secondaryColor else onSurface
                            )
                        }
                    }

                    // Faz 2: Başlatma Engeli (Bakiye < 50 TL)
                    val canStart = isQrVerified && isCablePlugged && userBalance >= 50.0

                    Surface(
                        onClick = { chargingViewModel.startCharging() },
                        shape = RoundedCornerShape(16.dp),
                        color = if (canStart) primaryColor else surfaceContainerLowest,
                        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f).height(110.dp),
                        enabled = canStart && chargingState == ChargingState.IDLE,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(if (canStart) Color.White.copy(alpha = 0.2f) else surfaceContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Power,
                                    contentDescription = null,
                                    tint = if (canStart) Color.White else outlineVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (userBalance < 50.0 && isCablePlugged) "Low Balance" else "Start Charging",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (canStart) Color.White else outlineVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
