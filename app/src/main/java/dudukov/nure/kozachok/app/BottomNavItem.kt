package dudukov.nure.kozachok.app


import ExerciseScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dudukov.nure.kozachok.Calendar.CalendarScreen
import dudukov.nure.kozachok.Profile.ProfileScreen
import dudukov.nure.kozachok.R
import dudukov.nure.kozachok.Statistics.StatisticsScreen

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){


   object Profile: BottomNavItem("Profile",R.drawable.profile,"Calendar")
   object Exercise : BottomNavItem("Exercise", R.drawable.exercise, "exercise")
   object Calendar : BottomNavItem("Calendar", R.drawable.calendar, "calendar")
   object Statistics : BottomNavItem("Statistics", R.drawable.statistics, "statistics")

}

@Composable
fun MainScreen(auth: FirebaseAuth, onSignOut: () -> Unit) {
   val navController = rememberNavController()
   Scaffold(
      bottomBar = { BottomNavigationBar(navController) }
   ) { innerPadding ->
      NavHost(
         navController = navController,
         startDestination = BottomNavItem.Profile.screen_route,
         modifier = Modifier.padding(innerPadding)
      ) {
         composable(BottomNavItem.Profile.screen_route) {
            ProfileScreen(
               onSignOut = onSignOut
            )
         }
         composable(BottomNavItem.Exercise.screen_route) {
            ExerciseScreen()
         }
         composable(BottomNavItem.Calendar.screen_route) {
            CalendarScreen()
         }
         composable(BottomNavItem.Statistics.screen_route) {
            StatisticsScreen()
         }
      }
   }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
   val items = listOf(
      BottomNavItem.Profile,
      BottomNavItem.Exercise,
      BottomNavItem.Calendar,
      BottomNavItem.Statistics
   )
   NavigationBar {
      val currentRoute = navController.currentBackStackEntry?.destination?.route
      items.forEach { item ->
         NavigationBarItem(
            icon = { Icon(painterResource(item.icon), contentDescription = item.title) },
            label = { Text(item.title) },
            selected = currentRoute == item.screen_route,
            onClick = {
               if (currentRoute != item.screen_route) {
                  navController.navigate(item.screen_route) {
                     popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                     }
                     launchSingleTop = true
                     restoreState = true
                  }
               }
            }
         )
      }
   }
}

