package net.sippory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import net.sippory.data.AppDatabase
import net.sippory.data.repository.BottleRepository
import net.sippory.navigation.NavGraph
import net.sippory.ui.theme.SipporyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 시스템 네비게이션바 색상 설정
        window.navigationBarColor = android.graphics.Color.parseColor("#0D0D0D")
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightNavigationBars = false

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
