package org.k.barcode.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.runBlocking
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
        startDestination = Screen.AppSettings.route
    ) {
        composable(route = Screen.ScanTest.route) {
            ScanTest(paddingValues = paddingValues)
        }
        composable(route = Screen.AppSettings.route) {
            runBlocking { databaseRepository.getSettings() }.also {
                AppSettings(
                    paddingValues = paddingValues,
                    navHostController = navHostController,
                    viewModel = viewModel,
                    initSettings = it
                )
            }
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
            runBlocking {
                databaseRepository.getCodeDetailByName(it.arguments?.getString("name")!!)
            }.also {
                CodeDetail(
                    paddingValues = paddingValues,
                    codeDetails = it
                )
            }
        }
    }
}