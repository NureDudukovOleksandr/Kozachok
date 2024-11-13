package dudukov.nure.kozachok.app

import dudukov.nure.kozachok.R

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){

//    object Gameplay : BottomNavItem("Gameplay", R.drawable.gameplay,"gameplay")
  //  object Event: BottomNavItem("Event",R.drawable.event,"event")
   object Profile: BottomNavItem("Profile",R.drawable.profile,"profile")
  //  object Payment: BottomNavItem("Payment",R.drawable.payment,"payment")
}

