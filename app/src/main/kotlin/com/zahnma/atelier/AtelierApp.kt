package com.zahnma.atelier

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zahnma.atelier.ui.brands.BrandListScreen
import com.zahnma.atelier.ui.directors.DirectorListScreen
import com.zahnma.atelier.ui.theme.AtelierTheme
import com.zahnma.atelier.viewmodel.BrandViewModel

private object Routes {
    const val Brands = "brands"
    const val Directors = "brands/{brandId}/directors"

    fun directors(brandId: String) = "brands/$brandId/directors"
}

@Composable
fun AtelierApp(
    viewModel: BrandViewModel = viewModel(),
) {
    AtelierTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Routes.Brands,
        ) {
            composable(Routes.Brands) {
                BrandListScreen(
                    viewModel = viewModel,
                    onBrandClick = { brandId ->
                        navController.navigate(Routes.directors(brandId))
                    },
                )
            }

            composable(
                route = Routes.Directors,
                arguments = listOf(
                    navArgument("brandId") { type = NavType.StringType },
                ),
            ) { entry ->
                val brandId = entry.arguments?.getString("brandId")
                val brand = brandId?.let(viewModel::brandFor)

                DirectorListScreen(
                    brand = brand,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
