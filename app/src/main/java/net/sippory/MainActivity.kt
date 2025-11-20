package net.sippory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import net.sippory.data.AppDatabase
import net.sippory.data.repository.BottleRepository
import net.sippory.navigation.NavGraph
import net.sippory.ui.theme.SipporyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Repository 초기화
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = BottleRepository(database.bottleDao())

        setContent {
            SipporyTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    repository = repository,
                )
            }
        }
    }
}
