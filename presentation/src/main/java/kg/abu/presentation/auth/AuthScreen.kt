package kg.abu.presentation.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kg.abu.data.repository.PhoneAuthResult
import kg.abu.presentation.R

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val phoneAuthState by authViewModel.phoneAuthFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                activity?.getString(R.string.default_web_client_id)
                    ?: throw IllegalStateException("Activity is null!")
            )
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {
                        authViewModel.signInWithGoogle(idToken)
                    } else {
                        Toast.makeText(
                            context,
                            "Google Sign-In failed: ID Token is null",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: ApiException) {
                    Toast.makeText(
                        context,
                        "Google Sign-In failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, "Google Sign-In cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> onAuthSuccess()
            is AuthState.Error -> Toast.makeText(
                context,
                (authState as AuthState.Error).message,
                Toast.LENGTH_LONG
            ).show()

            else -> {}
        }
    }

    var phoneNumber by remember { mutableStateOf("") }
    var smsCode by remember { mutableStateOf("") }
    var showCodeInput by remember { mutableStateOf(false) }

    LaunchedEffect(phoneAuthState) {
        when (phoneAuthState) {
            is PhoneAuthResult.CodeSent -> {
                showCodeInput = true
                Toast.makeText(context, "Verification code sent!", Toast.LENGTH_SHORT).show()
            }

            is PhoneAuthResult.VerificationCompleted -> {
            }

            is PhoneAuthResult.VerificationFailed -> {
                Toast.makeText(
                    context,
                    "Phone Auth failed: ${(phoneAuthState as PhoneAuthResult.VerificationFailed).errorMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }

            is PhoneAuthResult.CodeAutoRetrievalTimeout -> {
                Toast.makeText(context, "Code auto-retrieval timed out", Toast.LENGTH_SHORT).show()
                showCodeInput = true
            }

            else -> {}
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) }) {
            Text("Sign in with Google")
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number (e.g., +16505551234)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (phoneNumber.isNotBlank()) {
                authViewModel.sendPhoneVerificationCode(phoneNumber, activity)
            } else {
                Toast.makeText(context, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Send Verification Code")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (showCodeInput) {
            OutlinedTextField(
                value = smsCode,
                onValueChange = { smsCode = it },
                label = { Text("SMS Code") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (smsCode.isNotBlank()) {
                    authViewModel.verifyPhoneNumberWithCode(smsCode)
                } else {
                    Toast.makeText(context, "Please enter SMS code", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Verify Code")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator()
        }
    }
    Text("Auth Screen Placeholder")
}