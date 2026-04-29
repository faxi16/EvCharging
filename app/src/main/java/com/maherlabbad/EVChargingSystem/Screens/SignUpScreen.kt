package com.maherlabbad.EVChargingSystem.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen() {
    // --- State Yönetimi ---
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // --- Renk Paleti (HTML'den alındı) ---
    val primaryColor = Color(0xFF0058BC)
    val backgroundColor = Color(0xFFF9F9FF)
    val surfaceColor = Color(0xFFFFFFFF)
    val onSurface = Color(0xFF181C23)
    val onSurfaceVariant = Color(0xFF414755)
    val outline = Color(0xFF717786)
    val outlineVariant = Color(0xFFC1C6D7)

    // Arkaplan
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Form Kartı (Glassmorphism hissiyatı için Surface)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = surfaceColor.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, outlineVariant.copy(alpha = 0.3f)),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Başlık Kısmı
                Text(
                    text = "Create Account",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurface,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Join the future of charging",
                    fontSize = 16.sp,
                    color = onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Full Name Alanı
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Full Name", color = outlineVariant) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = outline) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = outlineVariant,
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Alanı
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", color = outlineVariant) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = outline) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = outlineVariant,
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Alanı
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", color = outlineVariant) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = outline) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = outlineVariant,
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password Alanı
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirm Password", color = outlineVariant) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = outline) }, // Alternatif: LockReset ikonu kullanılabilir
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = outlineVariant,
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Butonu
                Button(
                    onClick = {
                        // Supabase kayıt (Sign Up) işlemi burada çağrılacak
                        println("Kayıt verisi: $fullName, $email")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Alt Link (Giriş Yap Yönlendirmesi)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Login",
                        color = primaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            /* Login ekranına yönlendir (Navigation) */
                        }
                    )
                }
            }
        }
    }
}