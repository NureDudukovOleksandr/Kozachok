package dudukov.nure.kozachok.Sign_in

import SignInState
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dudukov.nure.kozachok.MainActivity
import dudukov.nure.kozachok.ui.theme.KozachokTheme
import kotlinx.coroutines.launch
import nure.dudukov.betnost20.Sign_in.GoogleAuthUiClient


class SignInActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var googleAuthUiClient: GoogleAuthUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val oneTapClient = Identity.getSignInClient(this)

        // Тепер передаємо oneTapClient в GoogleAuthUiClient
        googleAuthUiClient = GoogleAuthUiClient(this, oneTapClient)

        setContent {
            KozachokTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var state by remember { mutableStateOf(SignInState()) }
                    SignInScreen(
                        state = state,
                        onSignInWithEmailPassword = { email, password ->
                            if (email.isEmpty() || password.isEmpty()) {
                                state = SignInState(
                                    isSignInSuccessful = false,
                                    signInError = "Email and Password must not be empty"
                                )
                            } else {
                                signInWithEmailPassword(email, password, state)
                            }
                        },
                        onSignInWithGoogle = {
                            signInWithGoogle(state)
                        }
                    )
                }
            }
        }
    }

    private fun signInWithEmailPassword(email: String, password: String, state: SignInState) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Закриваємо SignInActivity
                } else {
                    state.signInError = task.exception?.message ?: "Login failed"
                    state.isSignInSuccessful = false
                }
            }
    }

    private fun signInWithGoogle(state: SignInState) {
        lifecycleScope.launch {
            val pendingIntent = googleAuthUiClient.signIn() // Отримуємо PendingIntent замість IntentSender
            pendingIntent?.let {
                val signInIntentSender = IntentSenderRequest.Builder(it).build() // Використовуємо PendingIntent
                launcher.launch(signInIntentSender) // Запускаємо процес
            } ?: run {
                state.signInError = "Google sign-in failed"
                state.isSignInSuccessful = false
            }
        }
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                val signInResult = googleAuthUiClient.signInWithIntent(result.data ?: return@launch)
                if (signInResult.errorMessage == null) {
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SignInActivity, signInResult.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Sign-in failed", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInWithEmailPassword: (String, String) -> Unit,
    onSignInWithGoogle: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSignInWithEmailPassword(email, password) }) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSignInWithGoogle() }, modifier = Modifier.fillMaxWidth()) {
            Text("Sign In with Google")
        }
    }
}
