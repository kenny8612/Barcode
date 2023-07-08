package org.k.barcode.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.k.barcode.data.AppDatabase
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.ui.navigation.appSettingsScreen
import org.k.barcode.ui.navigation.broadcastSettingsScreen
import org.k.barcode.ui.navigation.codeDetailSettingsScreen
import org.k.barcode.ui.navigation.codeSettingsScreen
import org.k.barcode.ui.navigation.scanTestScreen
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.viewmodel.SettingsViewModel
import org.k.barcode.ui.viewmodel.CodeSettingsViewModel
import org.k.barcode.ui.viewmodel.ScanTestViewModel

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    scanTestViewModel: ScanTestViewModel,
    codeSettingsViewModel: CodeSettingsViewModel,
    settingsViewModel: SettingsViewModel,
    databaseRepository: DatabaseRepository,
    appDatabase: AppDatabase
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.ScanTest.route
    ) {
        scanTestScreen(
            paddingValues,
            scanTestViewModel
        )
        appSettingsScreen(
            paddingValues,
            settingsViewModel,
            appDatabase,
            onNavigateToBroadcastSettings = {
                navHostController.navigate(Screen.BroadcastSettings.route)
            },
            onNavigateToCodeSettings = {
                navHostController.navigate(Screen.CodeSettings.route)
            }
        )
        codeSettingsScreen(
            paddingValues,
            codeSettingsViewModel,
            appDatabase
        ) {
            navHostController.navigate(Screen.CodeDetail.codeUid(it.uid))
        }
        codeDetailSettingsScreen(
            paddingValues,
            databaseRepository,
            appDatabase
        ) {
            navHostController.popBackStack()
        }
        broadcastSettingsScreen(
            paddingValues,
            databaseRepository,
            appDatabase
        ) {
            navHostController.popBackStack()
        }
    }
}