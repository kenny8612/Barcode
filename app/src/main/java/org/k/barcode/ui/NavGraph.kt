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
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.screen.ScanTestScreen
import org.k.barcode.ui.screen.AppSettingsScreen
import org.k.barcode.ui.screen.CodeDetailScreen
import org.k.barcode.ui.screen.CodeSettingsScreen

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    snackBarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    settingsViewModel: SettingsViewModel,
    decoderViewModel: DecoderViewModel,
    databaseRepository: DatabaseRepository,
    decoderManager: DecoderManager
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.ScanTest.route
    ) {
        composable(route = Screen.ScanTest.route) {
            ScanTestScreen(
                paddingValues = paddingValues,
                snackBarHostState = snackBarHostState,
                settingsViewModel = settingsViewModel,
                decoderViewModel = decoderViewModel
            )
        }
        composable(route = Screen.AppSettings.route) {
            AppSettingsScreen(
                paddingValues = paddingValues,
                navHostController = navHostController,
                viewModel = settingsViewModel,
                decoderManager = decoderManager
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
                viewModel = settingsViewModel,
                currentIndex = it.arguments?.getInt("index")!!
            )
        }
        composable(
            route = Screen.CodeDetail.route,
            arguments = listOf(navArgument("uid") {
                type = NavType.IntType
            })
        ) {
            runBlocking {
                databaseRepository.getCodeDetail(it.arguments?.getInt("uid")!!)
            }.also {
                CodeDetailScreen(
                    paddingValues = paddingValues,
                    navHostController = navHostController,
                    codeDetails = it
                )
            }
        }
    }
}