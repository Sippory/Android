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
import net.sippory.data.repository.DrinkRepository
import net.sippory.data.repository.RecentlySearchedDrinkRepository
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
import net.sippory.presentation.tastefinder.TasteFinderScreen
import net.sippory.presentation.tastefinder.TasteFinderViewModel
import net.sippory.presentation.tastefinder.TasteFinderViewModelFactory
import net.sippory.presentation.search.DrinkSearchScreen
import net.sippory.presentation.search.DrinkSearchViewModel
import net.sippory.presentation.searchDetail.SearchDetailScreen
import net.sippory.presentation.signin.SignInScreen
import net.sippory.presentation.signup.SignUpScreen
import net.sippory.utils.BottleViewModelFactory
import net.sippory.utils.DrinkViewModelFactory

sealed class Screen(val route: String) {
    object SignIn : Screen("sign-in")
    object SignUp : Screen("sign-up")

    object Home : Screen("home")

    object Detail : Screen("detail/{bottleId}") {
        fun createRoute(bottleId: Int) = "detail/$bottleId"
    }

    object Dashboard : Screen("dashboard") // ✅ 추가

    object Search : Screen("search")

    object SearchDetail : Screen("search/{drinkName}") {
        fun createRoute(drinkName: String) = "search/$drinkName"
    }

    object AIRecommend : Screen("ai_recommend")

    object TasteFinder : Screen("taste_finder")
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
        composable(Screen.SignIn.route) {
            SignInScreen(
                navController = navController,
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
            )
        }

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
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
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

        composable(Screen.Search.route) {
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val drinkRepository = remember { DrinkRepository() }
            val recentlySearchedDrinkRepository =
                remember { RecentlySearchedDrinkRepository(db.recentlySearchedDrinkDao()) }

            val factory =
                remember {
                    DrinkViewModelFactory(drinkRepository, recentlySearchedDrinkRepository)
                }
            val drinkSearchViewModel: DrinkSearchViewModel = viewModel(factory = factory)
            DrinkSearchScreen(
                navController = navController,
                viewModel = drinkSearchViewModel,
            )
        }

        composable(Screen.SearchDetail.route) { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.Search.route)
                }

            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val drinkRepository = remember { DrinkRepository() }
            val recentlySearchedDrinkRepository =
                remember {
                    RecentlySearchedDrinkRepository(db.recentlySearchedDrinkDao())
                }

            val factory =
                remember {
                    DrinkViewModelFactory(drinkRepository, recentlySearchedDrinkRepository)
                }

            val drinkSearchViewModel: DrinkSearchViewModel =
                viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = factory,
                )

            SearchDetailScreen(
                navController = navController,
                viewModel = drinkSearchViewModel,
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

        composable(Screen.TasteFinder.route) {
            val vm: TasteFinderViewModel =
                viewModel(
                    factory = TasteFinderViewModelFactory(repository),
                )

            TasteFinderScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
