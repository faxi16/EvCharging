package com.maherlabbad.EVChargingSystem.Screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.Screen
import com.maherlabbad.EVChargingSystem.Viewmodels.ProfileViewModel
import com.maherlabbad.EVChargingSystem.VoltChargeBottomNavBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToVehicles : () -> Unit,
    onNavigateToFavorites : () -> Unit,
    onLogout : () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToCharging: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToActivity: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {

    val user by viewModel.user.collectAsState()
    val stats by viewModel.statistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val issueSubmitStatus by viewModel.issueSubmitStatus.collectAsState()
    val isSubmittingIssue by viewModel.isSubmittingIssue.collectAsState()


    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val primaryContainer = Color(0xFFE0E8FF)
    val primaryFixedDim = Color(0xFFADC6FF)
    val secondaryColor = Color(0xFF006E28)
    val secondaryContainer = Color(0xFFD7FFDF)
    val errorColor = Color(0xFFBA1A1A)
    val errorContainer = Color(0xFFFFDAD6)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F3FE)
    val surfaceContainerHighest = Color(0xFFE0E2ED)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)

    var issueLocation by remember { mutableStateOf("") }
    var issueDescription by remember { mutableStateOf("") }

    // Başarılı olursa inputları temizle
    LaunchedEffect(issueSubmitStatus) {
        if (issueSubmitStatus == "Success") {
            viewModel.resetIssueStatus()
            issueLocation = ""
            issueDescription = ""
        }
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
                    Text(
                        text = "VoltCharge",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        letterSpacing = (-0.5).sp
                    )
                }
            }
        },
        bottomBar = {
            VoltChargeBottomNavBar(
                currentRoute = Screen.Profile.route,
                onNavigateToMap = onNavigateToMap,
                onNavigateToCharging = onNavigateToCharging,
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToActivity = onNavigateToActivity,
                onNavigateToProfile = { /* Already here */ }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            CircularProgressIndicator()
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. Profil Kartı
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = surfaceContainerLowest,
                    border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        // Üst Kısım Gradient Arkaplan
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(primaryFixedDim, Color.Transparent)
                                    )
                                )
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp).fillMaxWidth()
                        ) {
                            // Profil Resmi (Placeholder)
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .background(surfaceContainerHighest, CircleShape)
                                    .border(4.dp, surfaceContainerLowest, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                user?.name.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = onSurface
                            )
                            Text(
                                user?.email.toString(),
                                fontSize = 14.sp,
                                color = onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = outlineVariant.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))

                            // İstatistikler (user_statistics_view'dan gelen dinamik veriler)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Toplam Oturum (Total Sessions)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stats?.totalSessions?.toString() ?: "0",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = onSurface
                                    )
                                    Text("Sessions", fontSize = 12.sp, color = onSurfaceVariant)
                                }

                                // Ayraca (Divider)
                                Box(modifier = Modifier.width(1.dp).height(32.dp).background(outlineVariant.copy(alpha = 0.3f)))

                                // 2. Tüketilen Enerji (Total Energy kWh)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Küsurat çok uzunsa diye formatlıyoruz (Örn: 45.2)
                                    val energy = String.format(java.util.Locale.US, "%.1f", stats?.totalEnergy ?: 0.0)
                                    Text(
                                        text = energy,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = onSurface
                                    )
                                    Text("kWh Charged", fontSize = 12.sp, color = onSurfaceVariant)
                                }

                                // Ayraca (Divider)
                                Box(modifier = Modifier.width(1.dp).height(32.dp).background(outlineVariant.copy(alpha = 0.3f)))

                                // 3. Toplam Harcanan (Total Spent TL)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val spent = String.format(java.util.Locale.US, "%.2f", stats?.totalSpent ?: 0.0)
                                    Text(
                                        text = "₺$spent",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = secondaryColor // Parayı yeşil yapıyoruz
                                    )
                                    Text("Total Spent", fontSize = 12.sp, color = onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                // 2. Aksiyon Butonları (Grid)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActionCard(
                        title = "My Vehicles",
                        subtitle = "Manage your EVs",
                        icon = Icons.Default.DirectionsCar,
                        iconBg = primaryContainer,
                        iconColor = primaryColor,
                        modifier = Modifier.weight(1f),
                        navigate = { onNavigateToVehicles() }
                    )
                    ActionCard(
                        title = "Favorite Stations",
                        subtitle = "Quick access",
                        icon = Icons.Default.Star,
                        iconBg = secondaryContainer,
                        iconColor = secondaryColor,
                        modifier = Modifier.weight(1f),
                        navigate = { onNavigateToFavorites() }
                    )
                }

                // Sorun Bildir (Report an Issue) Formu
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = surfaceContainerHighest.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier.size(40.dp).background(errorContainer, CircleShape)
                                .padding(top = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Report, contentDescription = null, tint = errorColor)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Report an Issue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Encountered a damaged cable or offline terminal? Let us know.",
                                fontSize = 12.sp,
                                color = onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = issueLocation,
                                onValueChange = { issueLocation = it },
                                placeholder = { Text("Station ID or Location") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = surfaceContainerLowest,
                                    focusedContainerColor = surfaceContainerLowest
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = issueDescription,
                                onValueChange = { issueDescription = it },
                                placeholder = { Text("Describe the issue...") },
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                maxLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = surfaceContainerLowest,
                                    focusedContainerColor = surfaceContainerLowest
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Uyarı veya Başarı Mesajı
                            if (!issueSubmitStatus.isNullOrEmpty()) {
                                val isSuccess = issueSubmitStatus == "Success"
                                Text(
                                    text = if (isSuccess) "Report submitted successfully!" else issueSubmitStatus!!,
                                    color = if (isSuccess) secondaryColor else errorColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            Button(
                                onClick = { viewModel.submitIssue(locationorID = issueLocation, description = issueDescription) },
                                colors = ButtonDefaults.buttonColors(containerColor = onSurfaceVariant),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isSubmittingIssue) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Text("Submit Report")
                                }
                            }
                        }
                    }
                }

                // 4. Çıkış Yap Butonu
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.logoutUser( onSuccess = {onLogout()}) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor),
                    border = BorderStroke(1.dp, errorColor),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ActionCard(title: String, subtitle: String, icon: ImageVector, iconBg: Color, iconColor: Color, modifier: Modifier = Modifier,navigate: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFC1C6D7).copy(alpha = 0.5f)),
        shadowElevation = 1.dp,
        modifier = modifier.clickable(onClick = { navigate() })
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(48.dp).background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF181C23))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF414755))
        }
    }
}
