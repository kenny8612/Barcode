package org.k.barcode.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.k.barcode.ui.navigation.appSettingsScreen
import org.k.barcode.ui.navigation.broadcastSettingsScreen
import org.k.barcode.ui.navigation.codeDetailSettingsScreen
import org.k.barcode.ui.navigation.codeSettingsScreen
import org.k.barcode.ui.navigation.scanTestScreen
import org.k.barcode.ui.screen.Screen

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    snackBarHostState:SnackbarHostState,
    shareViewModel: ShareViewModel
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.ScanTest.route
    ) {
        scanTestScreen(
            paddingValues,
            shareViewModel
        )
        appSettingsScreen(
            paddingValues,
            shareViewModel,
            onNavigateToBroadcastSettings = {
                navHostController.navigate(Screen.BroadcastSettings.route)
            },
            onNavigateToCodeSettings = {
                navHostController.navigate(Screen.CodeSettings.route)
            }
        )
        codeSettingsScreen(
            paddingValues,
            shareViewModel
        ) {
            navHostController.navigate(Screen.CodeDetail.route)
        }
        codeDetailSettingsScreen(
            paddingValues,
            shareViewModel
        ) {
            navHostController.popBackStack()
        }
        broadcastSettingsScreen(
            paddingValues,
            shareViewModel
        ) {
            navHostController.popBackStack()
        }
    }
}