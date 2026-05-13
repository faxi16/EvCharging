package com.maherlabbad.EVChargingSystem.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.User
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Ekranda hata mesajı göstermek için (Örn: "Şifre yanlış")
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Giriş/Kayıt başarılı olduğunda sayfayı değiştirmek (Navigasyon) için
    private val _isAuthSuccessful = MutableStateFlow(false)
    val isAuthSuccessful: StateFlow<Boolean> = _isAuthSuccessful.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    // --- GİRİŞ YAP FONKSİYONU ---
    fun login(emailInput: String, passwordInput: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Supabase Auth ile giriş yap
                SupabaseNetwork.client.auth.signInWith(Email) {
                    email = emailInput
                    password = passwordInput
                }
                // Giriş başarılıysa, kullanıcının rolünü çekelim
                val currentUser = SupabaseNetwork.client.auth.currentUserOrNull()
                if (currentUser != null) {
                    val userData = SupabaseNetwork.client.postgrest["User"].select {
                        filter { eq("UserID", currentUser.id) }
                    }.decodeSingle<User>()
                    _role.value = userData.role
                }

                // Başarılı olursa tetikle
                _isAuthSuccessful.value = true

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Giriş başarısız."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- KAYIT OL FONKSİYONU (Çift Aşama) ---
    fun register(emailInput: String, passwordInput: String, nameInput: String, surnameInput: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // 1. Aşama: Kullanıcıyı Supabase Sistemine (Auth) kaydet
                val authResult = SupabaseNetwork.client.auth.signUpWith(Email) {
                    email = emailInput
                    password = passwordInput
                }

                // Auth sisteminin oluşturduğu benzersiz UUID'yi al
                val newUserId = authResult?.id ?: SupabaseNetwork.client.auth.currentUserOrNull()?.id

                if (newUserId != null) {
                    // 2. Aşama: Senin çizdiğin `User` tablosuna kişisel bilgileri (Insert) ekle!
                    val newUser = User(
                        userId = newUserId,
                        name = nameInput,
                        surname = surnameInput,
                        email = emailInput,
                        role = "Customer" // Varsayılan rol
                    )

                    SupabaseNetwork.client.postgrest["User"].insert(newUser)

                    // Her şey kusursuz çalıştı
                    _isAuthSuccessful.value = true
                } else {
                    _errorMessage.value = "Kullanıcı ID'si alınamadı."
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Kayıt olurken bir hata oluştu."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hata mesajını ekrandan temizlemek için (Snackbar kapatıldığında vs.)
    fun clearError() {
        _errorMessage.value = null
    }
}