package com.maherlabbad.EVChargingSystem.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.maherlabbad.EVChargingSystem.Models.Charger
import com.maherlabbad.EVChargingSystem.Models.ChargingStation
import com.maherlabbad.EVChargingSystem.Screen
import com.maherlabbad.EVChargingSystem.Viewmodels.MapViewModel
import com.maherlabbad.EVChargingSystem.VoltChargeBottomNavBar
import androidx.core.net.toUri
import com.google.android.gms.location.LocationServices
import com.maherlabbad.EVChargingSystem.ActiveVehicleSession
import com.maherlabbad.EVChargingSystem.CurrentLocation

@Composable
fun InteractiveMapScreen(
    onNavigateToDetails: (stationId: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToCharging: () -> Unit,
    onNavigateToActivity: () -> Unit,
    viewModel: MapViewModel
) {

    // İstasyon listesini dinliyoruz
    val stations by viewModel.filteredStations.collectAsState()
    val chargers by viewModel.selectedStationChargers.collectAsState()
    val context = LocalContext.current
    // ... (Kamera ayarları, UI state'leri vs. aynı kalıyor) ...
    var selectedStation by remember { mutableStateOf<ChargingStation?>(null) }

    // Tema Renkleri (HTML'den eşleştirildi)
    val primaryColor = Color(0xFF0058BC) // VoltCharge Blue
    val primaryContainer = Color(0xFFE0E8FF)
    val secondaryColor = Color(0xFF006E28) // Available (Green)
    val surfaceColor = Color(0xFFF9F9FF)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)
    val izmirLocation = LatLng(38.4237, 27.1428)

    val mapProperties = MapProperties(isMyLocationEnabled = true)
    val mapUiSettings = MapUiSettings(myLocationButtonEnabled = true, mapToolbarEnabled = false)
    // Kamera pozisyonunu (Nereye bakılacağını) tutan State
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(izmirLocation, 12f)
    }

    val lat by CurrentLocation.latitude.collectAsState()
    val lng by CurrentLocation.longitude.collectAsState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val activeVehicleId by ActiveVehicleSession.activeVehicleId.collectAsState()

    val dynamicDistance =
        calculateDistanceToStation(
            userLat = lat,
            userLng = lng,
            stationLocationStr = selectedStation?.location
        )

    LaunchedEffect(activeVehicleId) {
        if(activeVehicleId != null){
            viewModel.loadStationsAndFilter()
        }
    }

    LaunchedEffect(Unit) {
        val hasFinePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFinePermission || hasCoarsePermission) {
            // Zaten izin verilmişse direkt konumu çek
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(location.latitude, location.longitude), 12f)
                    CurrentLocation.setLocation(
                        location.latitude,
                        location.longitude
                    )
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            VoltChargeBottomNavBar(
                currentRoute = Screen.Map.route,
                onNavigateToMap = { /* Already here */ },
                onNavigateToCharging = onNavigateToCharging,
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToActivity = onNavigateToActivity,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        containerColor = Color(0xFFD8D9E5) // Harita yüklenene kadar arkaplan rengi
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Harita Arkaplanı
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
                onMapClick = {
                    selectedStation = null
                    viewModel.clearSelectedChargers()
                }
            ) {
                stations.forEach { mapItem ->

                    // Veritabanındaki location string'ini (örn: "38.4237, 27.1428") LatLng'ye çeviriyoruz
                    val coordinates = mapItem.station.location.split(",")
                    if (coordinates.size == 2) {
                        val lat = coordinates[0].trim().toDoubleOrNull() ?: 0.0
                        val lng = coordinates[1].trim().toDoubleOrNull() ?: 0.0

                        Marker(
                            state = MarkerState(position = LatLng(lat,lng)),
                            title = mapItem.station.name,
                            icon = BitmapDescriptorFactory.defaultMarker(mapItem.statusColor),
                            onClick = {
                                // Pine tıklandığında altta detay kartını göster
                                selectedStation = mapItem.station
                                viewModel.fetchChargersForStation(selectedStation?.stationID!!)// Tıklanan istasyonu kaydet (alt kartta göstermek için)
                                true // true yaparsan kamerayı kendi merkezlemez, false varsayılan davranıştır
                            }
                        )
                    }
                }
            }

            // 2. Üst Bar
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
                    Text(
                        text = "VoltCharge",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        letterSpacing = (-0.5).sp
                    )
                }
            }
            selectedStation?.let { it1 ->
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
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier.width(40.dp).height(4.dp)
                                    .background(outlineVariant, RoundedCornerShape(50))
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    selectedStation?.name.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(8.dp)
                                            .background(secondaryColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Working Hours • ${selectedStation?.operatingHours}",
                                        fontSize = 14.sp,
                                        color = onSurfaceVariant
                                    )
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
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(dynamicDistance, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            chargers.forEach {
                                ConnectorCard(charger = it)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { onNavigateToDetails(selectedStation?.stationID.toString()) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = onSurfaceVariant)
                            ) {
                                Text("Details", fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    val coordinates = it1.location.split(",")
                                    if (coordinates.size == 2) {
                                        val lat = coordinates[0].trim().toDoubleOrNull() ?: 0.0
                                        val lng = coordinates[1].trim().toDoubleOrNull() ?: 0.0
                                        val uri = "google.navigation:q=${lat},${lng}&mode=d".toUri() // mode=d (Driving)
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        intent.setPackage("com.google.android.apps.maps")
                                        // Eğer telefonda maps yoksa çökmesin diye kontrol
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(intent)
                                        } else {
                                            // Maps yoksa tarayıcıdan aç
                                            context.startActivity(Intent(Intent.ACTION_VIEW,
                                                "https://maps.google.com/?daddr=${lat},${lng}".toUri()))
                                        }
                                    }

                                },
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
}

@Composable
fun ConnectorCard(charger: Charger) {

    // 1. Statüye (Status) göre renk ve aktiflik durumu
    val isAvailable = charger.status.equals("Available", ignoreCase = true)
    val tintColor = if (isAvailable) Color(0xFF006E28) else Color(0xFFBA1A1A) // Yeşil veya Kırmızı
    val backgroundColor = if (isAvailable) Color(0xFFECEDF9) else Color(0xFFF9F9FF)
    val borderColor = if (isAvailable) tintColor.copy(alpha = 0.5f) else Color(0xFFC1C6D7)

    // 2. Güç Çıktısı (PowerOutput) formatlaması (Örn: 150.0 -> 150 kW)
    val formattedPower = if (charger.powerOutput % 1.0 == 0.0) {
        "${charger.powerOutput.toInt()} kW"
    } else {
        "${charger.powerOutput} kW"
    }

    // 3. Ekranda çok uzun durmaması için UUID'lerin ilk 6 hanesini alıyoruz (ChargerID ve StationID)
    val shortChargerId = charger.chargerID.take(6).uppercase()
    val shortStationId = charger.stationID.take(4).uppercase()

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        color = backgroundColor,
        modifier = Modifier.width(160.dp) // Daha fazla bilgi için biraz genişlettik
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // --- ÜST KISIM: Güç (PowerOutput) ve Tip (Type) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedPower,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C20)
                )

                // AC / DC Rozeti (Badge)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (charger.type == "DC") Color(0xFFFFD54F) else Color(0xFF90CAF9),
                ) {
                    Text(
                        text = charger.type, // "DC" veya "AC"
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // --- ORTA KISIM: Konektör (ConnectorType) ve Fiyat (UnitPrice) ---
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EvStation, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = charger.connectorType, // Örn: "CCS", "Type-2"
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₺${charger.unitPrice} / kWh", // Örn: "₺8.50 / kWh"
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
            }

            Divider(color = borderColor.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 2.dp))

            // --- ALT KISIM: Statü (Status) ve ID'ler (ChargerID, StationID) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = charger.status.uppercase(), // "AVAILABLE", "IN USE"
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tintColor
                )

                // Gerçek hayattaki gibi cihazın üzerinde yazan seri no mantığı
                Text(
                    text = "#$shortChargerId-$shortStationId",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@SuppressLint("DefaultLocale")
fun calculateDistanceToStation(userLat: Double, userLng: Double, stationLocationStr: String?): String {
    if (stationLocationStr.isNullOrBlank()) return "-- km away"

    return try {
        // "38.432, 27.142" formatındaki stringi virgülünden ikiye bölüyoruz
        val parts = stationLocationStr.split(",")
        if (parts.size == 2) {
            val stationLat = parts[0].trim().toDouble()
            val stationLng = parts[1].trim().toDouble()

            // Android'in mesafe hesaplama aracı
            val results = FloatArray(1)
            Location.distanceBetween(userLat, userLng, stationLat, stationLng, results)

            val distanceInMeters = results[0]
            val distanceInKm = distanceInMeters / 1000f

            // Formatlama: "2.4 km away"
            String.format("%.1f km away", distanceInKm)
        } else {
            "-- km away"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "-- km away" // Parse hatası olursa (örneğin koordinat yerine "Alsancak" yazılmışsa)
    }
}