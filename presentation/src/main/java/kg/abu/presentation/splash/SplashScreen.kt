package kg.abu.presentation.splash

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kg.abu.presentation.auth.AuthState
import kg.abu.presentation.auth.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        delay(2000L)

        val prefs = context.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val completedOnBoarding = prefs.getBoolean("completed", false)

        when (authState) {
            is AuthState.Loading -> {
            }
            is AuthState.Authenticated -> {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                if (completedOnBoarding) {
                    navController.navigate("auth") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("onBoarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
            is AuthState.Error -> {
                navController.navigate("auth") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "ServiceKG \uD83D\uDE80, \uD83D\uDEE0, " +
                    "⚔\uFE0F Таким образом, твоя уникальность: " +
                    "\uD83E\uDDE9 Основные конкуренты в Кыргызстане " +
                    "\uD83D\uDCA1 Уточним, " +
                    "\uD83D\uDD27 Для исполнителей/мастеров/продавцов: ✅ Splash → OnBoarding → Auth → M" +
                    "ain UI\n" +
                    "\n" +
                    "⏳ Реализация логики + фильтры + поиск\n" +
                    "\n" +
                    "\uD83D\uDCAC Реализация чатов и публикаций\n" +
                    "\n" +
                    "\uD83D\uDCE6 Backend или HTTP-симуляция\n" +
                    "\n" +
                    "\uD83D\uDCF2 Тестовые пользователи, запуск закрытого теста 2. \uD83D\uDCB0 Монетизация:",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
