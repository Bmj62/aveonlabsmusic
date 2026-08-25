package com.example.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SoundWaveApp
import com.example.data.remote.FirebaseAudioRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Authenticated(val user: FirebaseUser?) : AuthUiState
    data class Guest(val displayName: String = "Guest Listener") : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val repository: FirebaseAudioRepository = SoundWaveApp.instance.firebaseRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(
        if (repository.isUserLoggedIn) AuthUiState.Authenticated(repository.currentUser)
        else AuthUiState.Idle
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val isSignUpMode = MutableStateFlow(false)
    val showGoogleConfigDialog = MutableStateFlow(false)
    val googleClientIdInput = MutableStateFlow("")

    fun toggleAuthMode() {
        isSignUpMode.value = !isSignUpMode.value
    }

    fun openGoogleConfigDialog(context: Context) {
        googleClientIdInput.value = getGoogleClientId(context)
        showGoogleConfigDialog.value = true
    }

    fun closeGoogleConfigDialog() {
        showGoogleConfigDialog.value = false
    }

    fun saveGoogleClientId(context: Context, clientId: String) {
        val trimmed = clientId.trim()
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("google_web_client_id", trimmed).apply()
        googleClientIdInput.value = trimmed
        showGoogleConfigDialog.value = false
        if (trimmed.isNotBlank()) {
            signInWithGoogle(context)
        }
    }

    fun getGoogleClientId(context: Context): String {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getString("google_web_client_id", "")?.trim() ?: ""
        if (saved.isNotBlank()) return saved

        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        val resVal = if (resId != 0) {
            try { context.getString(resId).trim() } catch (e: Exception) { "" }
        } else ""
        return resVal
    }

    fun submit() {
        val em = email.value.trim()
        val pw = password.value.trim()

        if (em.isBlank() || pw.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter both email and password")
            return
        }

        if (pw.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            if (isSignUpMode.value) {
                val res = repository.signUp(em, pw)
                res.fold(
                    onSuccess = { user ->
                        _uiState.value = AuthUiState.Authenticated(user)
                    },
                    onFailure = { err ->
                        _uiState.value = AuthUiState.Error(err.localizedMessage ?: "Failed to create account")
                    }
                )
            } else {
                val res = repository.signIn(em, pw)
                res.fold(
                    onSuccess = { user ->
                        _uiState.value = AuthUiState.Authenticated(user)
                    },
                    onFailure = { err ->
                        _uiState.value = AuthUiState.Error(err.localizedMessage ?: "Invalid email or password")
                    }
                )
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        val serverClientId = getGoogleClientId(context)
        if (serverClientId.isBlank()) {
            _uiState.value = AuthUiState.Idle
            openGoogleConfigDialog(context)
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val response = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                handleGoogleSignInResponse(response)
            } catch (e: GetCredentialCancellationException) {
                // User dismissed or cancelled the Google prompt
                _uiState.value = AuthUiState.Idle
            } catch (e: GetCredentialException) {
                Log.e("AuthViewModel", "Google Credential Error: ${e.message}", e)
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Google Sign-In failed")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Google Auth Error: ${e.message}", e)
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Google Authentication failed")
            }
        }
    }

    private suspend fun handleGoogleSignInResponse(response: GetCredentialResponse) {
        val credential = response.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authRes = repository.signInWithGoogleCredential(idToken)
                authRes.fold(
                    onSuccess = { user ->
                        _uiState.value = AuthUiState.Authenticated(user)
                    },
                    onFailure = { err ->
                        _uiState.value = AuthUiState.Error(err.localizedMessage ?: "Google Firebase authentication failed")
                    }
                )
            } catch (e: GoogleIdTokenParsingException) {
                _uiState.value = AuthUiState.Error("Received invalid Google token payload")
            }
        } else {
            _uiState.value = AuthUiState.Error("Unsupported credential type")
        }
    }

    fun continueAsGuest() {
        _uiState.value = AuthUiState.Guest()
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState.Idle
        email.value = ""
        password.value = ""
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
