package dudukov.nure.kozachok.Statistics

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import dudukov.nure.kozachok.Data.FirebaseAuthHelper
import dudukov.nure.kozachok.Data.TrainingData
import dudukov.nure.kozachok.Data.UserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val dbHelper = FirebaseAuthHelper(context)
    val coroutineScope = rememberCoroutineScope()

    var userData by remember { mutableStateOf<UserData?>(null) }

    // Load user data
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        user?.let {
            userData = dbHelper.getUserData(it.uid)
        }
    }

    if (userData != null) {
        StatisticsContent(userData = userData!!)
    } else {
        LoadingScreen()
    }
}

@Composable
fun StatisticsContent(userData: UserData) {
    val trainingData = userData.trainingData

    // Prepare data for charts
    val weightData = trainingData.mapIndexed { index, training ->
        Pair(index.toFloat(), training.weight)
    }
    val hoursData = trainingData.mapIndexed { index, training ->
        Pair(index.toFloat(), training.trainingHours)
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {

        Text("Statistics", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp))

        // Line Chart for Weight
        Text("Weight Over Time", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LineChart(
            data = weightData,
            label = "Weight (kg)"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Line Chart for Training Hours
        Text("Training Hours Over Time", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LineChart(
            data = hoursData,
            label = "Training Hours"
        )
    }
}

@Composable
fun LineChart(data: List<Pair<Float, Float>>, label: String) {
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 4f
    }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(16.dp)
    ) {
        if (data.isNotEmpty()) {
            val maxX = data.maxOf { it.first }
            val maxY = data.maxOf { it.second }

            // Scale data to fit within the canvas bounds
            val scaleX = size.width / maxX
            val scaleY = size.height / maxY

            // Draw lines
            var startPoint: Offset? = null
            data.forEachIndexed { index, point ->
                val x = point.first * scaleX
                val y = size.height - (point.second * scaleY)

                if (startPoint != null) {
                    drawLine(
                        start = startPoint!!,
                        end = Offset(x, y),
                        color = Color.Black,
                        strokeWidth = 4f
                    )
                }

                startPoint = Offset(x, y)
            }

            // Draw labels
            drawContext.canvas.nativeCanvas.drawText(
                label,
                size.width / 2f,
                size.height + 30f,
                paint
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
