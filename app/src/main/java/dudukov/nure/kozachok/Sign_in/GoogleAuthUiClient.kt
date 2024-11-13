package nure.dudukov.betnost20.Sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient

import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dudukov.nure.kozachok.R
import dudukov.nure.kozachok.Sign_in.SignInResult
import kotlinx.coroutines.tasks.await

private const val TAG = "GoogleAuthUiClient"

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Запуск авторизації
    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(buildSignInRequest()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return result?.pendingIntent?.intentSender
    }

    // Обробка результату авторизації, тепер повертається тільки email
    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val userCredential = auth.signInWithCredential(googleCredentials).await()
            val user = userCredential.user

            // Повертається лише електронна пошта користувача
            user?.email?.let { email ->
                SignInResult(
                    data = email,  // Повертаємо лише email
                    errorMessage = null
                )
            } ?: SignInResult(data = null.toString(), errorMessage = "User is null")
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult(data = null.toString(), errorMessage = e.message)
        }
    }

    // Sign-out користувача
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Створення запиту на авторизацію
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
