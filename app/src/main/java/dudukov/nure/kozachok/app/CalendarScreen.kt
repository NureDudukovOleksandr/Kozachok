package dudukov.nure.kozachok.Calendar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.widget.CalendarView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView



@Composable
fun CalendarScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Calendar", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Встраиваем CalendarView в Compose
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setDate(System.currentTimeMillis(), false, true) // Устанавливаем текущую дату
                    setOnDateChangeListener { view, year, month, dayOfMonth ->
                        val selectedDate = "$dayOfMonth/${month + 1}/$year"  // Формируем строку с выбранной датой
                        Toast.makeText(context, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(300.dp)
        )
    }
}
