package com.maherlabbad.EVChargingSystem

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maherlabbad.EVChargingSystem.Screens.ActiveChargingScreen
import com.maherlabbad.EVChargingSystem.Screens.ActivityScreen
import com.maherlabbad.EVChargingSystem.Screens.AddVehicleScreen
import com.maherlabbad.EVChargingSystem.Screens.AdminAddStationScreen
import com.maherlabbad.EVChargingSystem.Screens.AdminDashboardScreen
import com.maherlabbad.EVChargingSystem.Screens.AdminStationManagementScreen
import com.maherlabbad.EVChargingSystem.Screens.FavoriteStationsScreen
import com.maherlabbad.EVChargingSystem.Screens.InteractiveMapScreen
import com.maherlabbad.EVChargingSystem.Screens.LoginScreen
import com.maherlabbad.EVChargingSystem.Screens.MyVehiclesScreen
import com.maherlabbad.EVChargingSystem.Screens.ProfileScreen
import com.maherlabbad.EVChargingSystem.Screens.SessionDetailsScreen
import com.maherlabbad.EVChargingSystem.Screens.SignUpScreen
import com.maherlabbad.EVChargingSystem.Screens.StationDetailScreen
import com.maherlabbad.EVChargingSystem.Screens.TransactionDetailsScreen
import com.maherlabbad.EVChargingSystem.Screens.WalletScreen
import com.maherlabbad.EVChargingSystem.Viewmodels.ActiveChargingViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.ActivityViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.AdminAddStationViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.AdminDashboardViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.AdminViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.AuthViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.FavoriteStationsViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.MapViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.MyVehiclesViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.ProfileViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.ReservationViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.SessionDetailsViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.StationDetailViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.TransactionDetailsViewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.WalletViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val activeChargingViewModel: ActiveChargingViewModel = viewModel()
    val mapViewModel: MapViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route // Uygulama Login ile başlar
    ) {
        // --- Auth Akışı ---
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    val route = if (it != "Admin") Screen.Map.route else Screen.AdminAddStation.route
                    navController.navigate(route)
                                 },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.SignUp.route) {
            val viewModel = remember { AuthViewModel() }
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Screen.Login.route) },
                onBackToLogin = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // --- Ana Akış ---
        composable(Screen.AddVehicle.route) {
            AddVehicleScreen(
                onVehicleAdded = { navController.navigate(Screen.MyVehicles.route) },
                back = { navController.popBackStack() }
            )
        }

        composable(Screen.Map.route) {
            InteractiveMapScreen(
                onNavigateToDetails = {
                    val route = Screen.StationDetail.createRoute(it)
                    navController.navigate(route)
                                      },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToWallet = { navController.navigate(Screen.Wallet.route) },
                onNavigateToCharging = { navController.navigate(Screen.ActiveCharging.route) },
                onNavigateToActivity = { navController.navigate(Screen.Activity.route) },
                viewModel = mapViewModel
            )
        }

        composable(
            route = Screen.StationDetail.route, // "station_detail/{stationId}"
            arguments = listOf(
                navArgument("stationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            // 1. Linkin içinden ID'yi güvenli bir şekilde çekiyoruz
            val passedStationId = backStackEntry.arguments?.getString("stationId") ?: ""

            StationDetailScreen(
                stationId = passedStationId,
                onBack = {
                    navController.popBackStack()
                },
                onBookingConfirmed = {
                    // Rezervasyon bitince nereye gitsin? Örn: Haritaya
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Map.route) { inclusive = true }
                    }
                },
                stationViewModel = remember { StationDetailViewModel() },
                reservationViewModel = remember { ReservationViewModel() }
            )
        }

        composable(Screen.ActiveCharging.route) {
            ActiveChargingScreen(
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToWallet = { navController.navigate(Screen.Wallet.route) },
                onNavigateToActivity = { navController.navigate(Screen.Activity.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                chargingViewModel = activeChargingViewModel
            )
        }

        // --- Profil ve Cüzdan Akışı ---
        composable(Screen.Wallet.route) {
            WalletScreen(
                onTransactionClick = {
                    val route = Screen.TransactionDetails.createRoute(it)
                    navController.navigate(route)
                                     },
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToCharging = { navController.navigate(Screen.ActiveCharging.route) },
                onNavigateToActivity = { navController.navigate(Screen.Activity.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                walletViewModel = remember { WalletViewModel() }
            )
        }

        composable(
            route = Screen.TransactionDetails.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            // 1. Linkin içinden ID'yi güvenli bir şekilde çekiyoruz
            val passedTransactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            val viewModel = remember { TransactionDetailsViewModel() }

            TransactionDetailsScreen(
                onBack = { navController.popBackStack() },
                transactionId = passedTransactionId,
                viewModel = viewModel
            )
        }

        composable(Screen.AdminStationManagement.route){
            AdminStationManagementScreen(
                viewModel = remember { AdminViewModel() },
                onNavigateToAdminDashboard = { navController.navigate(Screen.AdminDashboard.route) },
                onNavigateToAdminAddStation = { navController.navigate(Screen.AdminAddStation.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToVehicles = { navController.navigate(Screen.MyVehicles.route) },
                onNavigateToFavorites = { navController.navigate(Screen.FavoriteStations.route) },
                onLogout = { navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Map.route) { inclusive = true }
                } },
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToCharging = { navController.navigate(Screen.ActiveCharging.route) },
                onNavigateToWallet = { navController.navigate(Screen.Wallet.route) },
                onNavigateToActivity = { navController.navigate(Screen.Activity.route) },
                viewModel = remember { ProfileViewModel() }
            )
        }
        composable(Screen.MyVehicles.route) {
            MyVehiclesScreen(
                onBack = { navController.popBackStack() },
                viewModel = remember { MyVehiclesViewModel() },
                onNavigateAddVehicle = { navController.navigate(Screen.AddVehicle.route) }
            )
        }

        composable(Screen.AdminAddStation.route) {
            AdminAddStationScreen(
                onNavigateDashboard = { navController.navigate(Screen.AdminDashboard.route) },
                viewModel = remember { AdminAddStationViewModel() },
                onNavigateToStationManagement = { navController.navigate(Screen.AdminStationManagement.route) }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToAddStation = { navController.navigate(Screen.AdminAddStation.route) },
                viewModel = remember { AdminDashboardViewModel() },
                onNavigateToStationManagement = { navController.navigate(Screen.AdminStationManagement.route) }
            )
        }

        composable(Screen.FavoriteStations.route) {
            FavoriteStationsScreen(
                onBack = { navController.popBackStack() },
                viewModel = remember { FavoriteStationsViewModel() },
                onNavigateToStationDetail = {
                    val route = Screen.StationDetail.createRoute(it)
                    navController.navigate(route)
                },
            )
        }
        composable(Screen.Activity.route) {
            ActivityScreen(
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToCharging = { navController.navigate(Screen.ActiveCharging.route) },
                onNavigateToWallet = { navController.navigate(Screen.Wallet.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                activityViewModel = remember { ActivityViewModel() },
                onNavigateToSessionDetail = {
                    val route = Screen.SessionDetail.createRoute(it)
                    navController.navigate(route)
                }
            )
        }

        composable(
            route = Screen.SessionDetail.route,
            arguments = listOf(
                navArgument("reservationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            // 1. Linkin içinden ID'yi güvenli bir şekilde çekiyoruz
            val passedReservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
            val viewModel = remember { SessionDetailsViewModel() }

            SessionDetailsScreen(
                viewModel = viewModel,
                reservationId = passedReservationId,
                onBack = { navController.popBackStack() }
            )
        }

    }
}
