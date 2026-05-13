package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.AdminBottomNavBar
import com.maherlabbad.EVChargingSystem.Screen
import com.maherlabbad.EVChargingSystem.Viewmodels.AdminAddStationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddStationScreen(
    onNavigateDashboard: () -> Unit,
    viewModel: AdminAddStationViewModel = viewModel(),
    onNavigateToStationManagement: () -> Unit
) {
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val message by viewModel.message.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    val stationName by viewModel.stationName.collectAsState()
    val location by viewModel.location.collectAsState()
    val operatingHours by viewModel.operatingHours.collectAsState()

    val chargerType by viewModel.chargerType.collectAsState()
    val powerOutput by viewModel.powerOutput.collectAsState()
    val connectorType by viewModel.connectorType.collectAsState()
    val unitPrice by viewModel.unitPrice.collectAsState()

    val chargersList by viewModel.chargersList.collectAsState()

    val adminPrimary = Color(0xFF1F2937)
    val adminSecondary = Color(0xFF3B82F6) // Mavi (Cihaz ekleme butonu için)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Network Station", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = adminPrimary)
            )
        },
        bottomBar = {
            AdminBottomNavBar(
                onNavigateToAddStation = {},
                currentRoute = Screen.AdminAddStation.route,
                onNavigateToDashboard = onNavigateDashboard,
                onNavigateToStationManagement = onNavigateToStationManagement
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Bildirim Kartı ---
            if (message != null) {
                Surface(
                    color = if (isSuccess) Color(0xFFD7FFDF) else Color(0xFFFFDAD6),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message!!,
                        color = if (isSuccess) Color(0xFF006E28) else Color(0xFFBA1A1A),
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // --- 1. İSTASYON DETAYLARI ---
            Text("Station Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = adminPrimary)

            OutlinedTextField(
                value = stationName,
                onValueChange = { viewModel.stationName.value = it },
                label = { Text("Station Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Şemaya göre Location tek metin alanı
            OutlinedTextField(
                value = location,
                onValueChange = { viewModel.location.value = it },
                label = { Text("Location (Coordinates) ex: 24.5678,12.3456") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = operatingHours,
                onValueChange = { viewModel.operatingHours.value = it },
                label = { Text("Operating Hours") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // --- 2. CİHAZ (CHARGER) EKLEME FORMU ---
            Text("Add Chargers", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = adminPrimary)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("AC", "DC").forEach { type ->
                    FilterChip(
                        selected = (chargerType == type),
                        onClick = { viewModel.chargerType.value = type },
                        label = { Text(type) }
                    )
                }
            }

            Text("Connector Type", fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Type-2", "CCS", "CHAdeMO").forEach { type ->
                    FilterChip(
                        selected = (connectorType == type),
                        onClick = { viewModel.connectorType.value = type },
                        label = { Text(type) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = powerOutput,
                    onValueChange = { viewModel.powerOutput.value = it },
                    label = { Text("Power (kW)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = { viewModel.unitPrice.value = it },
                    label = { Text("Price (TL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Listeye Ekle Butonu
            OutlinedButton(
                onClick = { viewModel.addChargerToList() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = adminSecondary),
                border = BorderStroke(1.dp, adminSecondary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Charger to List")
            }

            // --- 3. EKLENEN CİHAZLAR LİSTESİ ---
            if (chargersList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Chargers to be added (${chargersList.size})", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)

                chargersList.forEach { charger ->
                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${charger.type} • ${charger.connectorType}", fontWeight = FontWeight.Bold)
                                Text("${charger.powerOutput} kW • ${charger.unitPrice} TL/kWh", fontSize = 12.sp)
                            }
                            IconButton(onClick = { viewModel.removeCharger(charger) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 4. VERİTABANINA KAYDET BUTONU ---
            Button(
                onClick = { viewModel.submitStationAndChargers() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = adminPrimary),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Station & All Chargers", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}