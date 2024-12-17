package dudukov.nure.kozachok

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dudukov.nure.kozachok.Sign_in.SignInActivity
import dudukov.nure.kozachok.app.BottomNavItem
import dudukov.nure.kozachok.app.MainScreen
import dudukov.nure.kozachok.ui.theme.KozachokTheme

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KozachokTheme {
                MainScreen(auth, onSignOut = { signOut() })
            }
        }
    }

    private fun signOut() {
        auth.signOut()
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}
