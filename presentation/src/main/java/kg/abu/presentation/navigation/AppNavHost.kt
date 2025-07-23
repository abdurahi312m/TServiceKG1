package kg.abu.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kg.abu.presentation.auth.AuthScreen
import kg.abu.presentation.home.HomeScreen
import kg.abu.presentation.onboarding.OnBoardingScreen
import kg.abu.presentation.splash.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController) {

    val startDestination = "splash"

    NavHost(navController = navController, startDestination = startDestination) {
        composable(startDestination) { SplashScreen(navController) }
        composable("onBoarding") { OnBoardingScreen(navController) }
        composable(route = "auth") {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("home") { HomeScreen(navController) }
    }
}