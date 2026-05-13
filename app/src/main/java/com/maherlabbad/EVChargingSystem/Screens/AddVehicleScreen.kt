package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.MyVehiclesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(modifier : Modifier = Modifier, onVehicleAdded : () -> Unit,back : () -> Unit,myVehiclesViewModel: MyVehiclesViewModel = viewModel()) {
    // --- State Yönetimi ---
    var plateNumber by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var batteryCapacity by remember { mutableStateOf("") }

    // Dropdown (Marka) State'leri
    var expandedBrand by remember { mutableStateOf(false) }
    var selectedBrand by remember { mutableStateOf("") }
    val brands = listOf("Tesla", "Volkswagen", "Hyundai", "Ford", "Nissan")

    // Konektör State'leri
    var selectedConnector by remember { mutableStateOf("Type-2") }
    val connectors = listOf("Type-2", "CCS", "CHAdeMO")

    val isLoading by myVehiclesViewModel.isLoading.collectAsState()
    val isAddSuccess by myVehiclesViewModel.isAddSuccess.collectAsState()
    val errorMessage by myVehiclesViewModel.errorMessage.collectAsState()

    // --- Renk Paleti (HTML'den alındı) ---
    val primaryColor = Color(0xFF0058BC)
    val backgroundColor = Color(0xFFF9F9FF)
    val onSurfaceColor = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)
    val surfaceContainerLowest = Color(0xFFFFFFFF)

    // Başarıyla eklendiğinde sayfayı kapat ve listeye dön
    LaunchedEffect(isAddSuccess) {
        if (isAddSuccess) {
            myVehiclesViewModel.resetSuccessState() // Tekrar tetiklenmemesi için sıfırla
            onVehicleAdded() // AppNavigation'da nereye yönlendirdiysen oraya gider
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        back()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back",
                            tint = onSurfaceColor
                        )
                    }
                },
                actions = {
                    Text(
                        text = "VoltCharge",
                        color = primaryColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            // Sticky Bottom Action Area
            Surface(
                color = backgroundColor.copy(alpha = 0.95f),
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp).padding(bottom = 8.dp)) {
                    Button(
                        onClick = {
                            myVehiclesViewModel.addVehicle(
                                plateNumber = plateNumber,
                                brand = selectedBrand,
                                model = model,
                                batteryCapacity = batteryCapacity.toDoubleOrNull() ?: 0.0,
                                connectorType = selectedConnector
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading && plateNumber.isNotBlank() && selectedBrand.isNotBlank() && model.isNotBlank() && batteryCapacity.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Save and Continue",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Arrow Forward",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- Başlık Kısmı ---
            Text(
                text = "Add Your Vehicle",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor,
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Register your EV to find compatible chargers instantly.",
                fontSize = 16.sp,
                color = onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- İllüstrasyon Placeholder ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFD8E2FF)) // primary-fixed-dim
            ) {
                // Buraya ileride Coil kütüphanesi ile aracın resmi veya Lottie animasyonu eklenebilir
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Vehicle Illustration",
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center),
                    tint = primaryColor.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Form Alanları ---

            // 1. Plaka
            Text("PLATE NUMBER", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = plateNumber,
                onValueChange = { plateNumber = it.uppercase() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 35 ABC 123") },
                leadingIcon = {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = outlineVariant)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = surfaceContainerLowest,
                    focusedContainerColor = surfaceContainerLowest,
                    unfocusedBorderColor = outlineVariant,
                    focusedBorderColor = primaryColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Marka ve Model (Yan yana)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Marka Dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text("BRAND", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedBrand,
                        onExpandedChange = { expandedBrand = !expandedBrand }
                    ) {
                        OutlinedTextField(
                            value = selectedBrand,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBrand) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = surfaceContainerLowest,
                                focusedContainerColor = surfaceContainerLowest,
                                unfocusedBorderColor = outlineVariant,
                                focusedBorderColor = primaryColor
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBrand,
                            onDismissRequest = { expandedBrand = false }
                        ) {
                            brands.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedBrand = selectionOption
                                        expandedBrand = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Model Text Input
                Column(modifier = Modifier.weight(1f)) {
                    Text("MODEL", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Model 3") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = surfaceContainerLowest,
                            focusedContainerColor = surfaceContainerLowest,
                            unfocusedBorderColor = outlineVariant,
                            focusedBorderColor = primaryColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Batarya Kapasitesi
            Text("BATTERY CAPACITY", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = batteryCapacity,
                onValueChange = { batteryCapacity = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("60") },
                trailingIcon = {
                    Text("kWh", color = onSurfaceVariant, modifier = Modifier.padding(end = 16.dp))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = surfaceContainerLowest,
                    focusedContainerColor = surfaceContainerLowest,
                    unfocusedBorderColor = outlineVariant,
                    focusedBorderColor = primaryColor
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Konektör Tipi Çipleri (Chips)
            Text("CONNECTOR TYPE", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(connectors) { connector ->
                    val isSelected = selectedConnector == connector
                    val chipBgColor = if (isSelected) Color(0xFF0070EB) else surfaceContainerLowest
                    val chipContentColor = if (isSelected) Color.White else onSurfaceColor
                    val chipBorderColor = if (isSelected) Color(0xFF0070EB) else outlineVariant

                    Surface(
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, chipBorderColor),
                        color = chipBgColor,
                        modifier = Modifier
                            .height(48.dp)
                            .clickable { selectedConnector = connector }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EvStation,
                                contentDescription = null,
                                tint = chipContentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = connector,
                                color = chipContentColor,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Alt kısımdaki butona kadar içeriklerin kaybolmaması için boşluk bırakıyoruz
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}