package kg.abu.presentation.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.abu.data.repository.AuthRepositoryImpl
import kg.abu.data.repository.PhoneAuthResult
import kg.abu.domain.model.User
import kg.abu.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _phoneAuthFlow = MutableStateFlow<PhoneAuthResult>(PhoneAuthResult.Initial)
    val phoneAuthFlow: StateFlow<PhoneAuthResult> = _phoneAuthFlow

    var storedVerificationId: String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _authState.value = if (user != null) AuthState.Authenticated(user) else AuthState.Unauthenticated
            }
        }
        viewModelScope.launch {
            (authRepository as? AuthRepositoryImpl)?.phoneAuthState?.collect { result ->
                _phoneAuthFlow.value = result
                when (result) {
                    is PhoneAuthResult.VerificationCompleted -> {
                        signInWithPhoneCredential(result.credential)
                    }
                    is PhoneAuthResult.CodeSent -> {
                        storedVerificationId = result.verificationId
                        resendToken = result.resendToken
                    }
                    is PhoneAuthResult.VerificationFailed -> {
                        _authState.value = AuthState.Error(result.errorMessage)
                    }
                    else -> {}
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithGoogle(idToken)
            result.onSuccess { user ->
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { e ->
                _authState.value = AuthState.Error(e.message ?: "Unknown error during Google sign-in")
            }
        }
    }

    fun sendPhoneVerificationCode(phoneNumber: String, activity: Activity?) {
        viewModelScope.launch {
            _phoneAuthFlow.value = PhoneAuthResult.Initial
            val result = (authRepository as? AuthRepositoryImpl)?.sendVerificationCode(phoneNumber, activity)
            result?.onFailure { e ->
                _authState.value = AuthState.Error(e.message ?: "Failed to send verification code")
            }
        }
    }

    fun verifyPhoneNumberWithCode(code: String) {
        viewModelScope.launch {
            if (storedVerificationId != null) {
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
                signInWithPhoneCredential(credential)
            } else {
                _authState.value = AuthState.Error("Verification ID is missing.")
            }
        }
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = (authRepository as? AuthRepositoryImpl)?.signInWithPhoneAuthCredential(credential)
            result?.onSuccess { user ->
                _authState.value = AuthState.Authenticated(user)
            }?.onFailure { e ->
                _authState.value = AuthState.Error(e.message ?: "Phone authentication failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signOut()
            result.onSuccess {
                _authState.value = AuthState.Unauthenticated
            }.onFailure { e ->
                _authState.value = AuthState.Error(e.message ?: "Sign out failed")
            }
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}