package com.maherlabbad.EVChargingSystem

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VoltChargeBottomNavBar(
    currentRoute: String?,
    onNavigateToMap: () -> Unit,
    onNavigateToCharging: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val primaryColor = Color(0xFF0058BC)
    
    // Determine which tab is selected based on the route
    val isMapSelected = currentRoute == Screen.Map.route
    val isChargingSelected = currentRoute == Screen.ActiveCharging.route
    val isWalletSelected = currentRoute == Screen.Wallet.route
    val isActivitySelected = currentRoute == Screen.TransactionDetails.route
    val isProfileSelected = currentRoute == Screen.Profile.route || 
                            currentRoute == Screen.MyVehicles.route || 
                            currentRoute == Screen.FavoriteStations.route

    Surface(
        shadowElevation = 16.dp,
        color = Color.White.copy(alpha = 0.95f)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
        ) {
            NavigationBarItem(
                selected = isMapSelected,
                onClick = onNavigateToMap,
                icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                label = { Text("Map", fontWeight = if (isMapSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = primaryColor.copy(alpha = 0.1f)
                )
            )
            NavigationBarItem(
                selected = isChargingSelected,
                onClick = onNavigateToCharging,
                icon = { Icon(Icons.Default.EvStation, contentDescription = "Charging") },
                label = { Text("Charging", fontWeight = if (isChargingSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = primaryColor.copy(alpha = 0.1f)
                )
            )
            NavigationBarItem(
                selected = isWalletSelected,
                onClick = onNavigateToWallet,
                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                label = { Text("Wallet", fontWeight = if (isWalletSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = primaryColor.copy(alpha = 0.1f)
                )
            )
            NavigationBarItem(
                selected = isActivitySelected,
                onClick = onNavigateToActivity,
                icon = { Icon(Icons.Default.History, contentDescription = "Activity") },
                label = { Text("Activity", fontWeight = if (isActivitySelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = primaryColor.copy(alpha = 0.1f)
                )
            )
            NavigationBarItem(
                selected = isProfileSelected,
                onClick = onNavigateToProfile,
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile", fontWeight = if (isProfileSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = primaryColor.copy(alpha = 0.1f)
                )
            )
        }
    }
}
