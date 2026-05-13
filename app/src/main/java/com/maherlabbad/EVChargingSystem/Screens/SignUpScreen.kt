package com.maherlabbad.EVChargingSystem.Screens
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maherlabbad.EVChargingSystem.Viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit, // Başarılı kayıttan sonra ana sayfaya (veya giriş ekranına) dön
    onBackToLogin: () -> Unit,    // Geri butonuna basılınca Login'e dön
    viewModel: AuthViewModel = viewModel()
) {
    // ViewModel'den durumları (State) dinliyoruz
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isAuthSuccessful.collectAsState()

    // Kullanıcının gireceği değerleri tutan yerel durumlar (Local State)
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Şifre göster/gizle ikonu için
    var passwordVisible by remember { mutableStateOf(false) }

    // Sadece bu ekrana özel "Şifreler uyuşmuyor" hatası için yerel hata değişkeni
    var localError by remember { mutableStateOf<String?>(null) }

    // Kayıt başarılı olursa doğrudan uygulamanın içine (Haritaya) yönlendir
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onSignUpSuccess()
        }
    }

    // ViewModel'den gelen hata değiştiğinde yerel hatayı temizle
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) localError = null
    }

    Scaffold(
        Modifier.scrollable(rememberScrollState(), Orientation.Vertical),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Join VoltCharge", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Start your EV charging journey today.", color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            // İsim ve Soyisim (Yan yana)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Şifre
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, "Toggle password visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Şifre Tekrar
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hata Mesajları (Önce yerel hatayı, yoksa ViewModel'den (Supabase'den) gelen hatayı göster)
            val displayError = localError ?: errorMessage
            if (displayError != null) {
                println(displayError)
                Text(text = displayError, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Kayıt Ol Butonu
            Button(
                onClick = {
                    // Kayıt butonuna basıldığında önce basit kontrolleri yap (Şifreler uyuşuyor mu?)
                    if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank()) {
                        localError = "Please fill in all fields."
                    } else if (password != confirmPassword) {
                        localError = "Passwords do not match."
                    } else if (password.length < 6) {
                        localError = "Password must be at least 6 characters."
                    } else {
                        // Her şey tamamsa ViewModel'e gönder!
                        localError = null
                        viewModel.register(email, password, name, surname)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hesabı olanlar için Login'e dönüş butonu
            TextButton(onClick = onBackToLogin) {
                Text("Already have an account? Log in")
            }
        }
    }
}