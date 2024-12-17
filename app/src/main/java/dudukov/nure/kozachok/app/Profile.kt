package dudukov.nure.kozachok.Profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import dudukov.nure.kozachok.Data.FirebaseAuthHelper
import dudukov.nure.kozachok.Data.TrainingData
import dudukov.nure.kozachok.Data.UserData
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(onSignOut: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val dbHelper = FirebaseAuthHelper(context)
    val coroutineScope = rememberCoroutineScope()

    var userData by remember { mutableStateOf<UserData?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Load user data
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        user?.let {
            if (!dbHelper.isUserExist(it.uid)) {
                val newUser = UserData(
                    name = it.displayName ?: "User",
                    birthday = "",
                    height = "",
                    weight = "",
                    isAdmin = false
                )
                dbHelper.createUser(it.uid, newUser)
            }
            userData = dbHelper.getUserData(it.uid)
        }
    }

    if (userData != null) {
        ProfileContent(
            userData = userData!!,
            onUpdateUserData = { updatedData ->
                coroutineScope.launch {
                    dbHelper.updateUser(auth.currentUser!!.uid, updatedData)
                    userData = updatedData
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                }
            },
            onAddTraining = { training ->
                coroutineScope.launch {
                    dbHelper.addTraining(auth.currentUser!!.uid, training)
                    userData = dbHelper.getUserData(auth.currentUser!!.uid)
                }
            },
            onSignOut = {
                auth.signOut()
                onSignOut()
            },
            showDialog = showDialog,
            onShowDialog = { showDialog = true },
            onDismissDialog = { showDialog = false }
        )
    } else {
        LoadingScreen()
    }
}

@Composable
fun ProfileContent(
    userData: UserData,
    onUpdateUserData: (UserData) -> Unit,
    onAddTraining: (TrainingData) -> Unit,
    onSignOut: () -> Unit,
    showDialog: Boolean,
    onShowDialog: () -> Unit,
    onDismissDialog: () -> Unit
) {
    var name by remember { mutableStateOf(userData.name) }
    var birthday by remember { mutableStateOf(userData.birthday) }
    var height by remember { mutableStateOf(userData.height) }
    var weight by remember { mutableStateOf(userData.weight) }

    // Сортуємо тренування за датою
    val sortedTrainingData = userData.trainingData.sortedWith { training1, training2 ->
        compareDates(training1.date, training2.date)
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {

        Text("Profile", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp))

        // Поля вводу
        ProfileInputField(label = "Name", value = name, onValueChange = { name = it })
        ProfileInputField(label = "Birthday", value = birthday, onValueChange = { birthday = it })
        ProfileInputField(label = "Height", value = height, onValueChange = { height = it })
        ProfileInputField(label = "Weight", value = weight, onValueChange = { weight = it })

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для збереження змін
        Button(
            onClick = {
                onUpdateUserData(userData.copy(name = name, birthday = birthday, height = height, weight = weight))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Історія тренувань
        Text("Training History", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Горизонтальний список тренувань
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(sortedTrainingData) { training ->
                TrainingCard(training = training)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопки для додавання тренування та виходу
        Button(onClick = { onShowDialog() }, modifier = Modifier.fillMaxWidth()) {
            Text("Add Training")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
            Text("Sign Out")
        }

        // Діалог додавання тренування
        if (showDialog) {
            TrainingDialog(
                onDismiss = onDismissDialog,
                onSave = onAddTraining
            )
        }
    }
}

fun compareDates(date1: String, date2: String): Int {
    val parts1 = date1.split(".")
    val parts2 = date2.split(".")

    // Перевіряємо рік
    val year1 = parts1[2].toIntOrNull() ?: 0
    val year2 = parts2[2].toIntOrNull() ?: 0
    if (year1 < year2) return 1 // Повертаємо 1, якщо перша дата менша за другу
    if (year1 > year2) return -1 // Повертаємо -1, якщо перша дата більша за другу

    // Перевіряємо місяці
    val month1 = parts1[1].toIntOrNull() ?: 0
    val month2 = parts2[1].toIntOrNull() ?: 0
    if (month1 < month2) return 1 // Повертаємо 1, якщо перша дата менша за другу
    if (month1 > month2) return -1 // Повертаємо -1, якщо перша дата більша за другу

    // Перевіряємо дні
    val day1 = parts1[0].toIntOrNull() ?: 0
    val day2 = parts2[0].toIntOrNull() ?: 0
    if (day1 < day2) return 1 // Повертаємо 1, якщо перша дата менша за другу
    if (day1 > day2) return -1 // Повертаємо -1, якщо перша дата більша за другу

    return 0 // Якщо дати рівні
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TrainingCard(training: TrainingData) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Date: ${training.date}", style = MaterialTheme.typography.bodyMedium)
            Text("Weight: ${training.weight} kg", style = MaterialTheme.typography.bodyMedium)
            Text("Height: ${training.height} cm", style = MaterialTheme.typography.bodyMedium)
            Text("Exercises: ${training.exercisesCount}", style = MaterialTheme.typography.bodyMedium)
            Text("Training Hours: ${training.trainingHours} hrs", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun TrainingDialog(
    onDismiss: () -> Unit,
    onSave: (TrainingData) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var exercises by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Training") },
        text = {
            Column {
                ProfileInputField(label = "Date (dd.mm.yyyy)", value = date, onValueChange = { date = it })
                ProfileInputField(label = "Weight (kg)", value = weight, onValueChange = { weight = it })
                ProfileInputField(label = "Height (cm)", value = height, onValueChange = { height = it })
                ProfileInputField(label = "Exercises", value = exercises, onValueChange = { exercises = it })
                ProfileInputField(label = "Training Hours", value = hours, onValueChange = { hours = it })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (date.isNotEmpty()) {
                    onSave(
                        TrainingData(
                            date = date,
                            weight = weight.toFloatOrNull() ?: 0f,
                            height = height.toFloatOrNull() ?: 0f,
                            exercisesCount = exercises.toIntOrNull() ?: 0,
                            trainingHours = hours.toFloatOrNull() ?: 0f
                        )
                    )
                }
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
