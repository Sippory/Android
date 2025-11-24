// net/sippory/navigation/NavGraph.kt
package net.sippory.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.sippory.data.AppDatabase
import net.sippory.data.repository.BottleRepository
import net.sippory.data.repository.DashboardRepository
import net.sippory.presentation.ai.AIRecommendScreen
import net.sippory.presentation.ai.AIRecommendViewModel
import net.sippory.presentation.ai.AIRecommendViewModelFactory
import net.sippory.presentation.dashboard.DashboardScreen
import net.sippory.presentation.dashboard.DashboardViewModel
import net.sippory.presentation.dashboard.DashboardViewModelFactory
import net.sippory.presentation.detail.DetailScreen
import net.sippory.presentation.detail.DetailViewModel
import net.sippory.presentation.home.HomeScreen
import net.sippory.presentation.home.HomeViewModel
import net.sippory.utils.BottleViewModelFactory

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Detail : Screen("detail/{bottleId}") {
        fun createRoute(bottleId: Int) = "detail/$bottleId"
    }

    object Dashboard : Screen("dashboard")

    object AIRecommend : Screen("ai_recommend")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: BottleRepository,
) {
    val viewModelFactory = BottleViewModelFactory(repository)

    // ✅ DB 인스턴스 (대시보드 DAO용)
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                viewModel = homeViewModel,
                onBottleClick = { bottleId ->
                    navController.navigate(Screen.Detail.createRoute(bottleId))
                },
                repository = repository,
                onDashboardClick = {
                    navController.navigate(Screen.Dashboard.route)
                },
                navController = navController,
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments =
                listOf(
                    navArgument("bottleId") { type = NavType.IntType },
                ),
        ) { backStackEntry ->
            val bottleId = backStackEntry.arguments?.getInt("bottleId") ?: return@composable
            val detailViewModel: DetailViewModel = viewModel(factory = viewModelFactory)
            DetailScreen(
                bottleId = bottleId,
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // ✅ Dashboard 목적지
        composable(Screen.Dashboard.route) {
            val repo = remember { DashboardRepository(db.dashboardDao()) }
            val vm: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(repo))
            DashboardScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.AIRecommend.route) {
            val vm: AIRecommendViewModel =
                viewModel(
                    factory = AIRecommendViewModelFactory(repository),
                )

            AIRecommendScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
