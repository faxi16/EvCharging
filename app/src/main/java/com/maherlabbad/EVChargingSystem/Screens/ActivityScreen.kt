package com.maherlabbad.EVChargingSystem.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.Models.Reservation
import com.maherlabbad.EVChargingSystem.Screen
import com.maherlabbad.EVChargingSystem.Viewmodels.ActivityViewModel
import com.maherlabbad.EVChargingSystem.VoltChargeBottomNavBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToCharging: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSessionDetail: (reservationId : String) -> Unit,
    activityViewModel: ActivityViewModel = viewModel()
) {
    val activities by activityViewModel.activities.collectAsState()
    val isLoading by activityViewModel.isLoading.collectAsState()

    // Sayfa her açıldığında verileri yenile
    LaunchedEffect(Unit) {
        activityViewModel.fetchActivities()
    }

    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            Surface(
                color = surfaceColor.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Charging Activity",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                }
            }
        },
        bottomBar = {
            VoltChargeBottomNavBar(
                currentRoute = Screen.Activity.route,
                onNavigateToMap = onNavigateToMap,
                onNavigateToCharging = onNavigateToCharging,
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToActivity = { /* Zaten buradayız */ },
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else if (activities.isEmpty()) {
            // Boş Durum (Empty State)
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.EvStation, contentDescription = null, modifier = Modifier.size(64.dp), tint = outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No charging history found.", fontSize = 18.sp, color = onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    Text("Your past and upcoming sessions will appear here.", fontSize = 14.sp, color = outlineVariant)
                }
            }
        } else {
            // Listeleme İşlemi (Güvenli LazyColumn)
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(activities) { reservation ->
                    ActivityItemCard(reservation = reservation, onClick = {onNavigateToSessionDetail(reservation.reservationID)})
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityItemCard(reservation: Reservation,onClick : () -> Unit) {
    // Statüye Göre Dinamik Renk ve Metin Ayarları
    val isPending = reservation.status.equals("Pending", ignoreCase = true)
    val isActive = reservation.status.equals("Active", ignoreCase = true)
    val isCompleted = reservation.status.equals("Completed", ignoreCase = true)
    val isCancelled = reservation.status.equals("Cancelled", ignoreCase = true)

    val statusColor = when {
        isPending -> Color(0xFF0058BC) // Mavi
        isActive -> Color(0xFFE65100) // Turuncu
        isCompleted -> Color(0xFF006E28) // Yeşil
        isCancelled -> Color(0xFFBA1A1A) // Kırmızı
        else -> Color(0xFF717786) // Gri
    }

    val statusBgColor = when {
        isPending -> Color(0xFFE0E8FF)
        isActive -> Color(0xFFFFE0B2)
        isCompleted -> Color(0xFFD7FFDF)
        isCancelled -> Color(0xFFFFDAD6)
        else -> Color(0xFFE0E2ED)
    }

    // Tarihi Okunabilir Formata Çevir (Örn: 2026-05-02 -> 02 May 2026)
    var displayDate = reservation.date
    try {
        val parsedDate = LocalDate.parse(reservation.date)
        displayDate = parsedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    } catch (e: Exception) {}

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFC1C6D7).copy(alpha = 0.5f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = {onClick()})
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Üst Kısım: Tarih ve Statü Rozeti
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF717786), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(displayDate, fontSize = 14.sp, color = Color(0xFF414755), fontWeight = FontWeight.Medium)
                }

                // Statü Rozeti (Badge)
                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = reservation.status.uppercase(),
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Orta Kısım: İstasyon ID ve Zamanlar
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sol İkon
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFFECEDF9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EvStation, contentDescription = null, tint = Color(0xFF0058BC))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    // Veritabanında ChargerID var, ilk 6 hanesini cihaz adı gibi gösteriyoruz
                    val shortId = reservation.chargerID.take(6).uppercase()
                    Text("Charger: #$shortId", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF181C23))

                    Spacer(modifier = Modifier.height(4.dp))

                    // Saat Aralığı (Örn: 14:30 - 16:00)
                    // Veritabanındaki saniyeleri (14:30:00) kırpmak için take(5) kullanıyoruz
                    val start = reservation.startTime.take(5)
                    val end = reservation.endTime.take(5)
                    Text("Time: $start - $end", fontSize = 14.sp, color = Color(0xFF717786))
                }
            }
        }
    }
}