package com.maherlabbad.EVChargingSystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AdminBottomNavBar(
    currentRoute: String,
    onNavigateToDashboard: () -> Unit,
    onNavigateToAddStation: () -> Unit,
    onNavigateToStationManagement: () -> Unit
) {
    val adminPrimary = Color(0xFF1F2937) // Koyu Kurumsal Renk
    val adminSecondary = Color(0xFF3B82F6) // Seçili (Aktif) Mavi Renk

    NavigationBar(
        containerColor = adminPrimary, // Arka plan rengi
        contentColor = Color.White
    ) {

        NavigationBarItem(
            selected = currentRoute == Screen.AdminAddStation.route,
            onClick = onNavigateToAddStation,
            icon = { Icon(Icons.Default.AddLocation, contentDescription = "Add Station") },
            label = { Text("Add Station", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = adminSecondary,
                selectedTextColor = adminSecondary,
                indicatorColor = adminPrimary,
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            )
        )


        NavigationBarItem(
            selected = currentRoute == Screen.AdminDashboard.route,
            onClick = onNavigateToDashboard,
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = adminSecondary,
                selectedTextColor = adminSecondary,
                indicatorColor = adminPrimary, // Etrafındaki çemberi gizler veya uyumlu yapar
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            )
        )


        NavigationBarItem(
            selected = currentRoute == Screen.AdminStationManagement.route,
            onClick = onNavigateToStationManagement,
            icon = { Icon(Icons.Default.Handyman, contentDescription = "Manage Station") },
            label = { Text("Manage Station", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = adminSecondary,
                selectedTextColor = adminSecondary,
                indicatorColor = adminPrimary,
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            )
        )

    }
}