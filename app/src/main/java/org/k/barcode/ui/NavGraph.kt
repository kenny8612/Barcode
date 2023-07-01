package org.k.barcode.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.ui.scan.ScanTest
import org.k.barcode.ui.settings.AppSettings
import org.k.barcode.ui.settings.CodeDetail
import org.k.barcode.ui.settings.CodeSettingsScreen

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: SettingsViewModel,
    databaseRepository: DatabaseRepository
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.CodeSettings.route
    ) {
        composable(route = Screen.ScanTest.route) {
            ScanTest(paddingValues = paddingValues)
        }
        composable(route = Screen.AppSettings.route) {
            AppSettings(
                paddingValues = paddingValues,
                navHostController = navHostController,
                viewModel = viewModel,
                databaseRepository = databaseRepository
            )
        }
        composable(
            route = Screen.CodeSettings.route,
            arguments = listOf(navArgument("index") {
                type = NavType.IntType
            })
        ) {
            CodeSettingsScreen(
                paddingValues = paddingValues,
                navHostController = navHostController,
                viewModel = viewModel,
                currentIndex = it.arguments?.getInt("index")!!
            )
        }
        composable(
            route = Screen.CodeDetail.route,
            arguments = listOf(navArgument("name") {
                type = NavType.StringType
            })
        ) {
            val codeDetails = databaseRepository.getCodeDetailByName(it.arguments?.getString("name")!!)

            CodeDetail(
                paddingValues = paddingValues,
                codeDetails = codeDetails
            )
        }
    }
}