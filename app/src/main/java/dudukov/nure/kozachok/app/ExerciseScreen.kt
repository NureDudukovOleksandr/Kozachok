import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dudukov.nure.kozachok.R

@Composable
fun ExerciseScreen() {
    val exercises = listOf(
        Exercise(
            name = "Squat",
            image = painterResource(id = R.drawable.squat),
            description = "Squats are an essential lower-body exercise for improving strength and stability.",
            instructions = "1. Stand straight.\n2. Spread your legs shoulder-width apart.\n3. Lower your body by bending your knees.",
            benefits = "Squats help strengthen your lower body muscles, improve balance, and enhance joint health."
        ),
        Exercise(
            name = "Push-up",
            image = painterResource(id = R.drawable.pushup),
            description = "Push-ups are a classic upper-body exercise that builds strength and endurance.",
            instructions = "1. Lie on your stomach.\n2. Place your hands under your shoulders.\n3. Lower your body down and push back up.",
            benefits = "Push-ups strengthen your chest, shoulders, triceps, and core, improving upper body strength and endurance."
        ),
        Exercise(
            name = "Barbell Squat",
            image = painterResource(id = R.drawable.barbell_squat),
            description = "Barbell squats are a compound exercise that targets multiple muscle groups.",
            instructions = "1. Stand straight and place the barbell on your shoulders.\n2. Spread your legs shoulder-width apart.\n3. Lower your body down by bending your knees, keeping your back straight.",
            benefits = "Barbell squats build lower body strength, enhance muscle mass, and boost metabolic rate, making them excellent for overall fitness."
        ),
        Exercise(
            name = "Running",
            image = painterResource(id = R.drawable.running),
            description = "Running is a simple yet effective cardiovascular exercise.",
            instructions = "1. Start with a warm-up.\n2. Gradually increase your speed.\n3. Focus on your breathing and maintaining a steady pace.",
            benefits = "Running improves cardiovascular health, enhances lung capacity, burns calories, and boosts mental well-being by releasing endorphins."
        )
    )

    var currentIndex by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    var showInstruction by remember { mutableStateOf(false) }

    if (showInstruction) {
        // Показываем полное описание упражнения
        FullInstruction(
            exercise = exercises[currentIndex],
            onBack = { showInstruction = false }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > 200 && currentIndex > 0) {
                                currentIndex -= 1
                            } else if (offsetX < -200 && currentIndex < exercises.lastIndex) {
                                currentIndex += 1
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX += dragAmount
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Карточка с упражнением
            ExerciseCard(
                exercise = exercises[currentIndex],
                offsetX = offsetX.dp,
                onClick = { showInstruction = true }
            )
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, offsetX: Dp, onClick: () -> Unit) {
    val animatedOffsetX by animateDpAsState(targetValue = offsetX)

    Card(
        modifier = Modifier
            .offset(x = animatedOffsetX)
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = exercise.image,
                contentDescription = exercise.name,
                modifier = Modifier.size(300.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FullInstruction(exercise: Exercise, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Image(
            painter = exercise.image,
            contentDescription = exercise.name,
            modifier = Modifier.size(300.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Description:\n${exercise.description}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Instructions:\n${exercise.instructions}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Benefits:\n${exercise.benefits}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}

// Модель данных для упражнения
data class Exercise(
    val name: String,
    val image: Painter,
    val description: String,
    val instructions: String,
    val benefits: String
)
