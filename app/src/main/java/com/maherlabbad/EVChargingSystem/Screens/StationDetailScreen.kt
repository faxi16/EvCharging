package com.maherlabbad.EVChargingSystem.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.Models.Charger
import com.maherlabbad.EVChargingSystem.Viewmodels.ReservationViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.StationDetailViewModel
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailScreen(
    stationId: String,
    onBack : () -> Unit,
    onBookingConfirmed : () -> Unit,
    stationViewModel: StationDetailViewModel = viewModel(),
    reservationViewModel: ReservationViewModel = viewModel()
) {

    val station by stationViewModel.station.collectAsState()
    val chargers by stationViewModel.chargers.collectAsState()
    val isBookingSuccess by reservationViewModel.isSuccess.collectAsState()
    val isBookingLoading by reservationViewModel.isLoading.collectAsState()
    val bookingError by reservationViewModel.errorMessage.collectAsState()
    val isMaintenance = chargers.all{ it.status == "Maintenance"}
    var selectedCharger by remember { mutableStateOf<Charger?>(null) }

    var selectedStartTime by remember { mutableStateOf<String?>(null) }
    var selectedEndTime by remember { mutableStateOf<String?>(null) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val isFavorite by stationViewModel.isFavorite.collectAsState()

    // YENİ: UI'da gösterilecek saat seçimi hatası
    var timeSelectionError by remember { mutableStateOf<String?>(null) }

    val today = remember { LocalDate.now() }
    val tomorrow = remember { today.plusDays(1) }
    var selectedDate by remember { mutableStateOf(today) }

    // Tema Renkleri
    val primaryColor = Color(0xFF0058BC)
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

    LaunchedEffect(stationId) {
        stationViewModel.loadStationDetails(stationId)
    }

    LaunchedEffect(isBookingSuccess) {
        if (isBookingSuccess) {
            reservationViewModel.resetSuccessState()
            onBookingConfirmed()
        }
    }

    // --- POP-UP PENCERELERİ (TEKERLEKLİ SAAT) ---
    if (showStartTimePicker) {
        DialTimePickerModal(
            onConfirm = { time ->
                val parsedTime = LocalTime.parse(time)
                val selectedDateTime = java.time.LocalDateTime.of(selectedDate, parsedTime)
                val now = java.time.LocalDateTime.now()
                val maxAllowed = now.plusHours(24) // Maksimum 24 saat sonrası (DR03)

                if (selectedDateTime.isBefore(now)) {
                    timeSelectionError = "Geçmiş bir saat seçemezsiniz."
                } else if (selectedDateTime.isAfter(maxAllowed)) {
                    timeSelectionError = "En fazla 24 saat sonrasına rezervasyon yapabilirsiniz."
                } else {
                    timeSelectionError = null
                    selectedStartTime = time
                    showStartTimePicker = false
                }
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        DialTimePickerModal(
            onConfirm = { time ->
                val parsedTime = LocalTime.parse(time)
                val selectedDateTime = java.time.LocalDateTime.of(selectedDate, parsedTime)
                val now = java.time.LocalDateTime.now()
                val maxAllowed = now.plusHours(24)

                if (selectedDateTime.isBefore(now)) {
                    timeSelectionError = "Geçmiş bir saat seçemezsiniz."
                } else if (selectedDateTime.isAfter(maxAllowed)) {
                    timeSelectionError = "En fazla 24 saat sonrasına rezervasyon yapabilirsiniz."
                } else {
                    timeSelectionError = null
                    selectedEndTime = time
                    showEndTimePicker = false
                }
            },
            onDismiss = { showEndTimePicker = false }
        )
    }

    Scaffold(
        bottomBar = {
            Surface(
                shadowElevation = 24.dp,
                color = surfaceColor.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp).padding(bottom = 8.dp)) {

                    if (!bookingError.isNullOrEmpty()) {
                        Text(
                            text = bookingError!!,
                            color = errorColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            if (selectedCharger != null && selectedStartTime != null && selectedEndTime != null) {
                                reservationViewModel.createReservation(
                                    chargerId = selectedCharger!!.chargerID,
                                    date = selectedDate.toString(),
                                    startTime = selectedStartTime!!,
                                    endTime = selectedEndTime!!
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        enabled = selectedCharger != null && selectedStartTime != null && selectedEndTime != null && !isBookingLoading
                    ) {
                        if (isBookingLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Confirm Booking",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (station == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Üst Görsel Alanı (Hero Image)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(surfaceVariant)
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .padding(top = 24.dp)
                        .size(40.dp)
                        .background(surfaceColor.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurface)
                }

                IconButton(
                    onClick = { stationViewModel.toggleFavorite(stationId) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .padding(top = 24.dp)
                        .size(40.dp)
                        .background(surfaceColor.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        // Favoriyse kendi belirlediğin primaryColor (Mavi) veya kırmızı(errorColor) yapabilirsin.
                        tint = if (isFavorite) primaryColor else onSurfaceVariant
                    )
                }

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
                        Box(modifier = Modifier.size(10.dp).background(if ( isMaintenance) errorColor else secondaryColor, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isMaintenance) "Maintenance" else "Available", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if ( isMaintenance) errorColor else onSurface)
                    }
                }
            }

            // 2. Ana İçerik Kartı
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = surfaceColor,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 250.dp)
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
                            Text(station?.name ?: "error", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = onSurface)
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
                            val displayPrice = selectedCharger?.unitPrice ?: "..."
                            Text("₺$displayPrice", fontSize = 20.sp, fontWeight = FontWeight.Black, color = primaryColor)
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
                        chargers.forEach { charger ->
                            val isAvailable = charger.status.equals("Available", ignoreCase = true)
                            val canSelect = charger.status != "Maintenance"
                            Box(modifier = Modifier.clickable(enabled = canSelect) {
                                selectedCharger = charger
                            }) {
                                ConnectorItemCard(
                                    type = charger.connectorType,
                                    kw = "${charger.powerOutput} kW",
                                    isSelected = (selectedCharger?.chargerID == charger.chargerID),
                                    statusText = charger.status,
                                    statusColor = if (isAvailable) null else errorColor,
                                    primaryColor = primaryColor,
                                    outlineVariant = outlineVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = surfaceVariant)
                    Spacer(modifier = Modifier.height(24.dp))

                    // YENİ: Saat Seçimi Modülü Hata Mesajı
                    if (!timeSelectionError.isNullOrEmpty()) {
                        Text(
                            text = timeSelectionError!!,
                            color = errorColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

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
                                color = if (selectedDate == today) surfaceColor else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                shadowElevation = if (selectedDate == today) 1.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedDate = today
                                        timeSelectionError = null // Sekme değiştiğinde hatayı sıfırla
                                    }
                                    .padding(2.dp)
                            ) {
                                Text("Today", textAlign = TextAlign.Center, color = if (selectedDate == today) primaryColor else onSurfaceVariant, fontWeight = if (selectedDate == today) FontWeight.SemiBold else FontWeight.Normal, modifier = Modifier.padding(vertical = 8.dp))
                            }
                            Surface(
                                color = if (selectedDate == tomorrow) surfaceColor else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                shadowElevation = if (selectedDate == tomorrow) 1.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedDate = tomorrow
                                        timeSelectionError = null // Sekme değiştiğinde hatayı sıfırla
                                    }
                                    .padding(2.dp)
                            ) {
                                Text("Tomorrow", textAlign = TextAlign.Center, color = if (selectedDate == tomorrow) primaryColor else onSurfaceVariant, fontWeight = if (selectedDate == tomorrow) FontWeight.SemiBold else FontWeight.Normal, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }

                    // YENİ: Başlangıç ve Bitiş Saati Kartları
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Başlangıç Saati Butonu
                        TimeSelectorCard(
                            title = "Start Time",
                            time = selectedStartTime ?: "--:--",
                            onClick = { showStartTimePicker = true },
                            modifier = Modifier.weight(1f),
                            primaryColor = primaryColor,
                            outlineVariant = outlineVariant
                        )

                        // Bitiş Saati Butonu
                        TimeSelectorCard(
                            title = "End Time",
                            time = selectedEndTime ?: "--:--",
                            onClick = { showEndTimePicker = true },
                            modifier = Modifier.weight(1f),
                            primaryColor = primaryColor,
                            outlineVariant = outlineVariant
                        )
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

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// --- YARDIMCI COMPOSABLE'LAR ---

// YENİ: Tekerlekli/Kadranlı Saat Seçici Pop-up
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialTimePickerModal(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentTime = LocalTime.now()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = true // 24 saat formatı
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        confirmButton = {
            TextButton(onClick = {
                val h = timePickerState.hour.toString().padStart(2, '0')
                val m = timePickerState.minute.toString().padStart(2, '0')
                onConfirm("$h:$m")
            }) { Text("Select") }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Material 3 Saat Seçici (Dial Modeli)
                TimePicker(state = timePickerState)
            }
        }
    )
}

// YENİ: Şık Saat Seçim Kartları
@Composable
fun TimeSelectorCard(title: String, time: String, onClick: () -> Unit, modifier: Modifier = Modifier, primaryColor: Color, outlineVariant: Color) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, outlineVariant),
        color = Color.White,
        modifier = modifier.height(72.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 12.sp, color = Color(0xFF717786))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = time, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (time == "--:--") Color.LightGray else primaryColor)
            }
            Icon(Icons.Default.AccessTime, contentDescription = null, tint = outlineVariant)
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