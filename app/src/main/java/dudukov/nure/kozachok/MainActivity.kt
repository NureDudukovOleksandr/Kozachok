package dudukov.nure.kozachok

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import dudukov.nure.kozachok.Sign_in.SignInActivity
import dudukov.nure.kozachok.ui.theme.KozachokTheme

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KozachokTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = auth.currentUser
                    if (user != null) {
                        ProfileScreen(userEmail = user.email ?: "No email available", onSignOut = { signOut() })
                    } else {
                        // Якщо користувач не увійшов, показуємо повідомлення або перенаправляємо на екран входу
                        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun signOut() {
        auth.signOut()
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()

        // Створюємо Intent для переходу на екран входу (SignInActivity)
        val intent = Intent(this, SignInActivity::class.java)

        // Запускаємо SignInActivity
        startActivity(intent)

        // Закриваємо поточну активність, щоб користувач не міг повернутися назад на профіль
        finish()
    }
}

@Composable
fun ProfileScreen(userEmail: String, onSignOut: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Заголовок екрану профілю
        Text(
            text = "User Profile",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Показуємо email користувача
        Text(
            text = "Your Email: $userEmail",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Кнопка для виходу
        Button(onClick = { onSignOut() }) {
            Text("Sign Out")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KozachokTheme {
        ProfileScreen(userEmail = "user@example.com", onSignOut = {})
    }
}
