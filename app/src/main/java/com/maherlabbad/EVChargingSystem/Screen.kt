package com.maherlabbad.EVChargingSystem

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object AddVehicle : Screen("add_vehicle")
    object Map : Screen("map")
    object StationDetail : Screen("station_detail/{stationId}") {
        // Navigasyon yaparken bu fonksiyonu çağıracağız, bizim için linki üretecek
        fun createRoute(stationId: String) = "station_detail/$stationId"
    }
    object ActiveCharging : Screen("active_charging")
    object Wallet : Screen("wallet")
    object TransactionDetails : Screen("transaction_details/{transactionId}") {
        fun createRoute(transactionId: String) = "transaction_details/$transactionId"
    }
    object MyVehicles : Screen("my_vehicles")
    object FavoriteStations : Screen("favorite_stations")
    object Profile : Screen("profile")
    object Activity : Screen("activity")

    object SessionDetail : Screen("sessionDetail/{reservationId}"){
        fun createRoute(reservationId: String) = "sessionDetail/$reservationId"
    }
    object AdminAddStation : Screen("admin_add_station")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminStationManagement : Screen("admin_station_management")
}