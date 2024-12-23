package dudukov.nure.kozachok.Profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    // Завантаження даних користувача
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

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = birthday, onValueChange = { birthday = it }, label = { Text("Birthday") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = height, onValueChange = { height = it }, label = { Text("Height") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight") })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onUpdateUserData(userData.copy(name = name, birthday = birthday, height = height, weight = weight))
        }) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(userData.trainingData) { training ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Date: ${training.date}")
                        Text("Weight: ${training.weight}")
                        Text("Height: ${training.height}")
                        Text("Exercises: ${training.exercisesCount}")
                        Text("Hours: ${training.trainingHours}")
                    }
                }
            }
        }

        Button(onClick = { onShowDialog() }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Add Training")
        }
        Button(onClick = onSignOut, modifier = Modifier.padding(top = 8.dp)) {
            Text("Sign Out")
        }

        if (showDialog) {
            TrainingDialog(
                onDismiss = onDismissDialog,
                onSave = onAddTraining
            )
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
                TextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = exercises, onValueChange = { exercises = it }, label = { Text("Exercises") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = hours, onValueChange = { hours = it }, label = { Text("Training Hours") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    TrainingData(
                        date = date,
                        weight = weight.toFloatOrNull() ?: 0f,
                        height = height.toFloatOrNull() ?: 0f,
                        exercisesCount = exercises.toIntOrNull() ?: 0,
                        trainingHours = hours.toFloatOrNull() ?: 0f
                    )
                )
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
