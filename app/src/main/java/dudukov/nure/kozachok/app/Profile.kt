package dudukov.nure.kozachok.Profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen() {
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        user?.let {
            Text("Welcome, ${it.displayName}")
            Text("Email: ${it.email}")
            Button(onClick = { FirebaseAuth.getInstance().signOut() }) {
                Text("Sign Out")
            }
        } ?: Text("No user signed in")
    }
}
