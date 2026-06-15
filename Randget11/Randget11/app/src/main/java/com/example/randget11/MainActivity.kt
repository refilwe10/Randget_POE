package com.example.randget11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.randget11.ui.theme.Randget11Theme

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase   // Declare database here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database here (Correct place)
        db = Room.databaseBuilder(
            applicationContext,           // Now accessible
            AppDatabase::class.java,
            "budget_db"
        ).build()

        // Run seeder once (you can add a check later to run only first time)
        CategorySeeder.seed(this, db)

        setContent {
            Randget11Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Randget11Theme {
        Greeting("Android")
    }
}