package com.maherlabbad.EVChargingSystem.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.AdminBottomNavBar
import com.maherlabbad.EVChargingSystem.Screen
import com.maherlabbad.EVChargingSystem.Viewmodels.AdminDashboardViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToAddStation: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel(),
    onNavigateToStationManagement: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val utilizations by viewModel.utilizations.collectAsState()
    val peakHours by viewModel.peakHours.collectAsState()

    // Genel Toplamlar
    val totalRev by viewModel.totalNetworkRevenue.collectAsState()
    val totalPenalties by viewModel.totalNetworkPenalties.collectAsState()
    val totalSessions by viewModel.totalNetworkSessions.collectAsState()
    val totalCancels by viewModel.totalNetworkCancellations.collectAsState()

    val adminPrimary = Color(0xFF1F2937)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network Analytics", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = adminPrimary),
                actions = {
                    IconButton(onClick = { viewModel.fetchDashboardData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            AdminBottomNavBar(
                onNavigateToAddStation = onNavigateToAddStation,
                currentRoute = Screen.AdminDashboard.route,
                onNavigateToDashboard = {},
                onNavigateToStationManagement = onNavigateToStationManagement
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = adminPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // --- 1. NETWORK SUMMARY (AĞ ÖZETİ) ---
                item { Text("Global Network Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = adminPrimary) }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Gross Revenue",
                            value = "₺${String.format("%.2f", totalRev)}",
                            icon = Icons.Default.AccountBalanceWallet,
                            iconTint = Color(0xFF006E28),
                            bgColor = Color(0xFFD7FFDF)
                        )
                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Penalty Collected",
                            value = "₺${String.format("%.2f", totalPenalties * -1)}",
                            icon = Icons.Default.Gavel,
                            iconTint = Color(0xFFBA1A1A),
                            bgColor = Color(0xFFFFDAD6)
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Sessions",
                            value = "$totalSessions",
                            icon = Icons.Default.EvStation,
                            iconTint = Color(0xFF0058BC),
                            bgColor = Color(0xFFE0E8FF)
                        )
                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Cancellations",
                            value = "$totalCancels",
                            icon = Icons.Default.Cancel,
                            iconTint = Color(0xFFE65100),
                            bgColor = Color(0xFFFFE0B2)
                        )
                    }
                }

                // --- 2. PEAK HOURS (FR16) ---
                if (peakHours.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Busiest Hours (Network Peak)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = adminPrimary)
                    }
                    items(peakHours.sortedByDescending { it.totalReservations }.take(3)) { peak ->
                        Surface(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${peak.usageHour?.toInt()}:00", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Text("${peak.totalReservations} Reservations", color = adminPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // --- 3. STATION BREAKDOWN ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Revenue by Station", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = adminPrimary)
                }

                items(utilizations.sortedByDescending { it.grossRevenue }) { station ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(station.stationName ?: "Unknown Station", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = adminPrimary)
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = DividerDefaults.Thickness,
                                color = DividerDefaults.color
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Sessions: ${station.totalSessions}", color = Color.Gray)
                                Text("Cancellations: ${station.totalCancellations}", color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Charging: ₺${station.chargingRevenue}", color = Color(0xFF006E28), fontSize = 14.sp)
                                Text("Penalties: ₺${station.penaltyRevenue?.times(-1)}", color = Color(0xFFBA1A1A), fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Gross: ₺${station.grossRevenue}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = adminPrimary)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

// Küçük Şık İstatistik Kartı Bileşeni
@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    bgColor: Color
) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.5f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = iconTint)
        }
    }
}