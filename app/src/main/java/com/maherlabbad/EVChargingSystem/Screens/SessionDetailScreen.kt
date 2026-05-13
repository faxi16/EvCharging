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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EvStation
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.SessionDetailsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SessionDetailsScreen(
    reservationId: String,
    onBack: () -> Unit,
    viewModel: SessionDetailsViewModel = viewModel()
) {
    val session by viewModel.session.collectAsState()
    val reservation by viewModel.reservation.collectAsState() // YENİ: Tarih bilgisi için eklendi
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) }
    val infoMessage by viewModel.infoMessage.collectAsState()

    LaunchedEffect(reservationId) {
        viewModel.fetchSessionDetails(reservationId)
    }

    val primaryColor = Color(0xFF0058BC)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val onSurface = Color(0xFF181C23)
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
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurface)
                    }
                    Text(
                        text = "Session Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurface,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
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

            if (isLoading) {
                CircularProgressIndicator(color = primaryColor)
            } else if (!errorMessage.isNullOrEmpty() && session == null) {
                Surface(
                    color = Color(0xFFE0E8FF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = primaryColor,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }else if (!infoMessage.isNullOrEmpty()) {
                // Başarılı iptal mesajını göster
                Surface(
                    color = Color(0xFFD7FFDF), // Yeşilimsi
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = infoMessage!!,
                        color = Color(0xFF006E28),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            val r = reservation
            if(r != null){
                val isPending = r.status.equals("Pending", ignoreCase = true)
                val isCancelled = r.status.equals("Cancelled", ignoreCase = true)

                val startTime = r.startTime.take(5)
                val endTime = r.endTime.take(5)

                // YENİ: Tarih Formatlama ("2026-05-02" -> "02 May 2026")
                var displayDate = r.date ?: "--"
                try {
                    val parsedDate = LocalDate.parse(r.date)
                    displayDate = parsedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                } catch (e: Exception) { e.printStackTrace()}

                if (isPending || isCancelled) {
                    // --- BEKLEYEN VEYA İPTAL EDİLEN REZERVASYON ARAYÜZÜ ---
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = surfaceContainerLowest,
                        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.5f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // Durum Rozeti
                            val statusColor = if (isPending) primaryColor else Color(0xFFBA1A1A) // Mavi vs Kırmızı
                            val statusBg = if (isPending) Color(0xFFE0E8FF) else Color(0xFFFFDAD6)

                            Surface(color = statusBg, shape = RoundedCornerShape(50)) {
                                Text(
                                    text = r.status.uppercase(),
                                    color = statusColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text("Reservation Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onSurface)
                            Spacer(modifier = Modifier.height(16.dp))

                            ReceiptDetailRow("Date", displayDate)
                            Spacer(modifier = Modifier.height(12.dp))
                            ReceiptDetailRow("Time", "$startTime - $endTime")
                            Spacer(modifier = Modifier.height(12.dp))

                            val shortId = r.reservationID.takeLast(8).uppercase()
                            ReceiptDetailRow("Reservation ID", "#$shortId")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // EĞER PENDING İSE İPTAL BUTONU GÖSTER
                    if (isPending) {
                        Button(
                            onClick = {
                                showCancelDialog = true
                                viewModel.cancelReservation()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)), // Kırmızı Buton
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancel Reservation", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                } else if (session != null) {
                    // DÜZELTME BURADA YAPILDI! Parantez dışına çıkarıldı.
                    // --- TAMAMLANAN (COMPLETED) VEYA AKTİF ŞARJ ARAYÜZÜ ---
                    val s = session!!

                    val isCompleted = s.connectionStatus.equals("COMPLETED", ignoreCase = true)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = surfaceContainerLowest,
                        border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.5f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Üst İkon ve Durum
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth().padding(24.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(64.dp).background(Color(0xFFECEDF9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.EvStation, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                val statusColor = if (isCompleted) Color(0xFF006E28) else Color(0xFFE65100)
                                Text(
                                    text = s.connectionStatus.uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "₺${s.finalCost}",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = onSurface
                                )
                            }

                            DashedDivider(color = outlineVariant)

                            // Detaylar Listesi
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // YENİ EKLENEN TARİH VE SAAT SATIRLARI
                                ReceiptDetailRow(label = "Date", value = displayDate)

                                // "14:30:00" formatından son saniyeleri siliyoruz -> "14:30"
                                val start = r.startTime.take(5)
                                val end = r.endTime.take(5)
                                ReceiptDetailRow(label = "Time", value = "$start - $end")

                                ReceiptDetailRow(label = "Energy Consumed", value = "${s.energyConsumed} kWh")

                                val shortId = s.sessionID.takeLast(8).uppercase()
                                ReceiptDetailRow(label = "Session ID", value = "#$shortId")
                            }
                        }
                    }
                }
            }

        }
    }
}