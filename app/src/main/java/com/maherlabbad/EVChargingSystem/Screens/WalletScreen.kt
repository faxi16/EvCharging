package com.maherlabbad.EVChargingSystem.Screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // YENİ: Ekranı LazyColumn yaptık
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.Screen
import com.maherlabbad.EVChargingSystem.Viewmodels.WalletViewModel
import com.maherlabbad.EVChargingSystem.VoltChargeBottomNavBar
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onTransactionClick : (transactionId: String) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToCharging: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToProfile: () -> Unit,
    walletViewModel: WalletViewModel
) {
    // --- Renk Paleti ---
    val primaryColor = Color(0xFF0058BC)
    val primaryContainer = Color(0xFFE0E8FF)
    val secondaryColor = Color(0xFF006E28)
    val secondaryContainer = Color(0xFFD7FFDF)
    val errorColor = Color(0xFFBA1A1A)
    val errorContainer = Color(0xFFFFDAD6)
    val onErrorContainer = Color(0xFF93000A)
    val surfaceColor = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F3FE)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outlineVariant = Color(0xFFC1C6D7)

    val isLoading by walletViewModel.isLoading.collectAsState()
    val errorMessage by walletViewModel.errorMessage.collectAsState()
    val isTopUpSuccess by walletViewModel.isTopUpSuccess.collectAsState()
    val wallet by walletViewModel.wallet.collectAsState()
    var showTopUpSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val transactions by walletViewModel.transactions.collectAsState()

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            // Özel TopAppBar (Gölgeli ve Saydam)
            Surface(
                color = surfaceColor.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .statusBarsPadding()
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
                currentRoute = Screen.Wallet.route,
                onNavigateToMap = onNavigateToMap,
                onNavigateToCharging = onNavigateToCharging,
                onNavigateToWallet = { /* Already here */ },
                onNavigateToActivity = onNavigateToActivity,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->

        // --- TÜM EKRAN LAZYCOLUMN YAPILDI ---
        // verticalScroll kullanmayı bıraktık, her şeyi item {} içine aldık.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Başlık Kısmı
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Wallet & Payments",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                    Text(
                        text = "Manage your balance and view recent activity.",
                        fontSize = 14.sp,
                        color = onSurfaceVariant
                    )
                }
            }

            // 2. Kredi Kartı Tarzı Bakiye Kartı
            item {
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(primaryColor, Color(0xFF004493))
                )
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(gradientBrush)
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "CURRENT BALANCE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.8f),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = String.format("₺%.2f", wallet?.balance ?: 0.0),
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Nfc,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 40.dp, height = 24.dp)
                                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("VoltCharge Pro", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                }

                                Button(
                                    onClick = { showTopUpSheet = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = primaryColor
                                    ),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Top-up", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Dinamik Ceza Uyarı Afişi (Warning Banner)
            item {
                Surface(
                    color = errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, errorColor.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = errorColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Important Notice", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = onErrorContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "No-show reservations result in dynamic penalties. Please cancel at least 15 minutes prior to your window.",
                                fontSize = 14.sp,
                                color = onErrorContainer.copy(alpha = 0.9f),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // 4. İşlem Geçmişi Başlığı
            item {
                Text(
                    text = "Transaction Log",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurface
                )
            }

            // 5. İşlem Geçmişi Kartı
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = surfaceContainerLowest,
                    border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (transactions.isEmpty()) {
                        Text("No transactions found.", modifier = Modifier.padding(16.dp), color = onSurfaceVariant)
                    } else {
                        // Artık her şey sorunsuz ölçülüp çizilecek!
                        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                            transactions.forEachIndexed { index, tx ->

                                val isIncome = tx.amount > 0 || tx.type.equals("TopUp", ignoreCase = true)
                                val isPenalty = tx.type.equals("Penalty", ignoreCase = true)

                                val icon = when {
                                    isIncome -> Icons.Default.AccountBalanceWallet
                                    isPenalty -> Icons.Default.Cancel
                                    else -> Icons.Default.EvStation
                                }
                                val iconBg = when {
                                    isIncome -> secondaryContainer
                                    isPenalty -> errorContainer
                                    else -> primaryContainer
                                }
                                val iconTint = when {
                                    isIncome -> secondaryColor
                                    isPenalty -> errorColor
                                    else -> primaryColor
                                }
                                val amountColor = when {
                                    isIncome -> secondaryColor
                                    isPenalty -> errorColor
                                    else -> onSurface
                                }

                                val sign = if (isIncome) "+" else ""
                                val absoluteAmount = Math.abs(tx.amount)
                                val formattedAmount = "$sign ₺$absoluteAmount"

                                var formattedTime = tx.timestamp
                                try {
                                    val parsed = OffsetDateTime.parse(tx.timestamp)
                                    formattedTime = parsed.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"))
                                } catch (e: Exception) {}

                                val displayTitle = tx.description ?: tx.type

                                TransactionItem(
                                    icon = icon,
                                    iconBg = iconBg,
                                    iconTint = iconTint,
                                    title = displayTitle,
                                    subtitle = formattedTime,
                                    amount = formattedAmount,
                                    amountColor = amountColor,
                                    onTransactionClick = { onTransactionClick(tx.transactionID) }
                                )

                                if (index < transactions.lastIndex) {
                                    HorizontalDivider(color = outlineVariant.copy(alpha = 0.2f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- TOP-UP BOTTOM SHEET (Bakiye Yükleme Penceresi) ---
        if (showTopUpSheet) {
            var customAmount by remember { mutableStateOf("") }
            val predefinedAmounts = listOf(50.0, 100.0, 200.0, 500.0)

            ModalBottomSheet(
                onDismissRequest = { showTopUpSheet = false },
                sheetState = sheetState,
                containerColor = surfaceColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Top-up Balance", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Select an amount or enter a custom value.", color = onSurfaceVariant, fontSize = 14.sp)

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(predefinedAmounts) { amount ->
                            OutlinedButton(
                                onClick = { customAmount = amount.toInt().toString() },
                                border = BorderStroke(1.dp, if (customAmount == amount.toInt().toString()) primaryColor else outlineVariant),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (customAmount == amount.toInt().toString()) primaryColor else onSurface
                                )
                            ) {
                                Text("₺${amount.toInt()}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customAmount,
                        onValueChange = { customAmount = it },
                        label = { Text("Custom Amount (₺)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Text("₺", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val amountToLoad = customAmount.toDoubleOrNull() ?: 0.0
                            if (amountToLoad > 0) {
                                walletViewModel.topUpBalance(amountToLoad)
                                showTopUpSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = customAmount.isNotEmpty() && !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Confirm Top-up", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    amount: String,
    onTransactionClick : () -> Unit,
    amountColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTransactionClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF181C23),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF414755),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = Color(0xFFC1C6D7)
            )
        }
    }
}