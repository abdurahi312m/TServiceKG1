package kg.abu.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Главная", Icons.Default.Home)
    object Search : BottomNavItem("search", "Поиск", Icons.Default.Search)
    object Add : BottomNavItem("add", "Добавить", Icons.Default.Add)
    object Chat : BottomNavItem("chat", "Чаты", Icons.Default.Chat)
    object Profile : BottomNavItem("profile", "Профиль", Icons.Default.Person)

    companion object {
        val items = listOf(Home, Search, Add, Chat, Profile)
    }
}