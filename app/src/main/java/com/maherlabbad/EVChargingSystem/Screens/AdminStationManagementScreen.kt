package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maherlabbad.EVChargingSystem.AdminBottomNavBar
import com.maherlabbad.EVChargingSystem.Models.ChargingStation
import com.maherlabbad.EVChargingSystem.Screen
import com.maherlabbad.EVChargingSystem.Viewmodels.AdminViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.MapStationItemAdmin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStationManagementScreen(
    viewModel: AdminViewModel,
    onNavigateToAdminDashboard: () -> Unit,
    onNavigateToAdminAddStation: () -> Unit
) {
    // State'leri ViewModel'dan çekiyoruz
    val stations by viewModel.stations.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage Stations", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            AdminBottomNavBar(
                onNavigateToAddStation = onNavigateToAdminAddStation,
                currentRoute = Screen.AdminStationManagement.route,
                onNavigateToDashboard = onNavigateToAdminDashboard,
                onNavigateToStationManagement = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FA)) // Hafif gri profesyonel arka plan
        ) {
            // 1. Arama Barı
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by station name or city...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
            )

            // 2. İstasyon Listesi
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Arama filtresini uygula
                val filteredStations = stations.filter {
                    it.station.name.contains(searchQuery, ignoreCase = true) ||
                            it.station.location.contains(searchQuery, ignoreCase = true)
                }

                items(filteredStations) { item ->
                    AdminStationCard(
                        item = item,
                        onToggle = { id, currentStatus ->
                            viewModel.toggleStationStatus(id, currentStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStationCard(
    item: MapStationItemAdmin, // Senin Station modelin
    onToggle: (String, String) -> Unit
) {
    val isAvailable = item.status == "Available"
    val station = item.station
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1E)
                )
                Text(
                    text = station.location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Durum Rozeti (Badge)
                Surface(
                    color = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isAvailable) Color(0xFF4CAF50) else Color.Red,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAvailable) "ONLINE" else "OUT OF SERVICE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAvailable) Color(0xFF2E7D32) else Color.Red
                        )
                    }
                }
            }

            // AÇ/KAPAT Switch Mekanizması
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isAvailable) "Active" else "Deactive",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { onToggle(station.stationID, item.status) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE91E63) // Servis dışıyken pembe/kırmızı
                    )
                )
            }
        }
    }
}