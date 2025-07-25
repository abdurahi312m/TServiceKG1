package kg.abu.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.abu.domain.model.User
import kg.abu.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _authState.value = if (user != null) AuthState.Authenticated(user) else AuthState.Unauthenticated
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