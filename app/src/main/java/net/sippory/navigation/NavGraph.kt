package net.sippory.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.sippory.data.repository.BottleRepository
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
}

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: BottleRepository
) {
    val viewModelFactory = BottleViewModelFactory(repository)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                viewModel = homeViewModel,
                onBottleClick = { bottleId ->
                    navController.navigate(Screen.Detail.createRoute(bottleId))
                },
                repository = repository
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("bottleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val bottleId = backStackEntry.arguments?.getInt("bottleId") ?: return@composable
            val detailViewModel: DetailViewModel = viewModel(factory = viewModelFactory)
            DetailScreen(
                bottleId = bottleId,
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
