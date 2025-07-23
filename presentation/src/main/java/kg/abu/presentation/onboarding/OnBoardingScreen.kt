package kg.abu.presentation.onboarding

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.core.content.edit

@Composable
fun OnBoardingScreen(navController: NavHostController) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val alreadyShown = sharedPrefs.getBoolean("completed", false)

        if (alreadyShown) {
            navController.navigate("home") {
                popUpTo("onBoarding") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text("Welcome to OnBoarding 🧭")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            completeOnBoarding(navController, context)
        }) {
            Text("Начать пользоваться")
        }
    }
}

fun completeOnBoarding(navController: NavHostController, context: Context) {
    val prefs = context.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
    prefs.edit { putBoolean("completed", true) }
    navController.navigate("home") {
        popUpTo("onBoarding") { inclusive = true }
    }
}
