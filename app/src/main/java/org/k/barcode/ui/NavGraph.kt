package org.k.barcode.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.runBlocking
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.ui.screen.Screen
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.ui.screen.ScanTestScreen
import org.k.barcode.ui.screen.AppSettingsScreen
import org.k.barcode.ui.screen.CodeDetailScreen
import org.k.barcode.ui.screen.CodeSettingsScreen

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    snackBarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    viewModel: SettingsViewModel,
    databaseRepository: DatabaseRepository,
    decoderManager: DecoderManager
) {

    NavHost(
        navController = navHostController,
        startDestination = Screen.ScanTest.route
    ) {
        composable(route = Screen.ScanTest.route) {
            runBlocking { databaseRepository.getSettings() }.also {
                ScanTestScreen(
                    paddingValues = paddingValues,
                    snackBarHostState = snackBarHostState,
                    viewModel = viewModel,
                    initSettings = it
                )
            }
        }
        composable(route = Screen.AppSettings.route) {
            runBlocking { databaseRepository.getSettings() }.also {
                AppSettingsScreen(
                    paddingValues = paddingValues,
                    navHostController = navHostController,
                    viewModel = viewModel,
                    decoderManager = decoderManager,
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
                CodeDetailScreen(
                    paddingValues = paddingValues,
                    codeDetails = it
                )
            }
        }
    }
}