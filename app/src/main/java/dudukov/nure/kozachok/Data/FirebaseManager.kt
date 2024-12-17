package dudukov.nure.kozachok.Data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserData(
    val name: String = "",
    val birthday: String = "",
    val height: String = "",
    val weight: String = "",
    val isAdmin: Boolean = false,
    val notifications: Map<String, Boolean> = emptyMap(),
    val trainingData: List<TrainingData> = emptyList()
)

data class TrainingData(
    val date: String = "",
    val weight: Float = 0f,
    val height: Float = 0f,
    val exercisesCount: Int = 0,
    val trainingHours: Float = 0f
)

class FirebaseAuthHelper(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionName = "user-settings"

    // Перевірка наявності користувача
    suspend fun isUserExist(uid: String): Boolean {
        val userDoc = firestore.collection(collectionName).document(uid).get().await()
        return userDoc.exists()
    }

    // Створення нового користувача
    suspend fun createUser(uid: String, userData: UserData) {
        firestore.collection(collectionName).document(uid).set(userData).await()
    }

    // Оновлення даних користувача
    suspend fun updateUser(uid: String, userData: UserData) {
        firestore.collection(collectionName).document(uid).set(userData).await()
    }

    // Додавання тренування до історії
    suspend fun addTraining(uid: String, trainingData: TrainingData) {
        val userDocRef = firestore.collection(collectionName).document(uid)
        userDocRef.update("trainingData", FieldValue.arrayUnion(trainingData)).await()
    }

    // Отримання даних користувача
    suspend fun getUserData(uid: String): UserData? {
        val snapshot = firestore.collection(collectionName).document(uid).get().await()
        return snapshot.toObject(UserData::class.java)
    }
}
