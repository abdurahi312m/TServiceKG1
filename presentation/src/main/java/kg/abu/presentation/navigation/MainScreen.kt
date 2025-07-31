package kg.abu.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kg.abu.presentation.add.AddScreen
import kg.abu.presentation.chatbasket.ChatScreen
import kg.abu.presentation.home.HomeScreen
import kg.abu.presentation.profile.ProfileScreen
import kg.abu.presentation.search.SearchScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route
            ) {
                composable(
                    BottomNavItem.Home.route
                ) {
                    HomeScreen(navController = navController)
                }
                composable(BottomNavItem.Search.route) { SearchScreen() }
                composable(BottomNavItem.Add.route) { AddScreen() }
                composable(BottomNavItem.Chat.route) { ChatScreen() }
                composable(BottomNavItem.Profile.route) { ProfileScreen() }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview(modifier: Modifier = Modifier) {
    MainScreen()
}