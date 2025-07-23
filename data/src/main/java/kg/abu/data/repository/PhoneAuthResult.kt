package kg.abu.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kg.abu.domain.model.User
import kg.abu.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

sealed class PhoneAuthResult {
    object Initial : PhoneAuthResult()
    data class VerificationCompleted(val credential: PhoneAuthCredential): PhoneAuthResult()
    data class VerificationFailed(val errorMessage: String): PhoneAuthResult()
    data class CodeSent(val verificationId: String, val resendToken: PhoneAuthProvider.ForceResendingToken): PhoneAuthResult()
    data class CodeAutoRetrievalTimeout(val verificationId: String): PhoneAuthResult()
}

class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuth) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    private val _phoneAuthState = MutableStateFlow<PhoneAuthResult>(PhoneAuthResult.Initial)
    val phoneAuthState: Flow<PhoneAuthResult> = _phoneAuthState.asStateFlow()

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            _phoneAuthState.value = PhoneAuthResult.VerificationCompleted(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _phoneAuthState.value = PhoneAuthResult.VerificationFailed(e.message ?: "Verification failed")
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            _phoneAuthState.value = PhoneAuthResult.CodeSent(verificationId, token)
        }

        override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
            _phoneAuthState.value = PhoneAuthResult.CodeAutoRetrievalTimeout(verificationId)
        }
    }

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser?.toDomainUser()
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user?.toDomainUser()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Failed to sign in with Google: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendVerificationCode(phoneNumber: String, activity: Activity?): Result<Unit> {
        return try {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity!!)
                .setCallbacks(phoneAuthCallbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user?.toDomainUser()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Failed to sign in with phone: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun com.google.firebase.auth.FirebaseUser.toDomainUser(): User {
        return User(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl?.toString()
        )
    }
}