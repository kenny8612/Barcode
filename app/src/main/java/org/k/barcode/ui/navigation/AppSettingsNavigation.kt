package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.ui.ShareViewModel
import org.k.barcode.ui.screen.AppSettingsScreen
import org.k.barcode.ui.screen.Screen

fun NavGraphBuilder.appSettingsScreen(
    paddingValues: PaddingValues,
    shareViewModel: ShareViewModel,
    onNavigateToCodeSettings: () -> Unit,
    onNavigateToBroadcastSettings: () -> Unit
) {
    composable(route = Screen.AppSettings.route) {
        AppSettingsScreen(
            paddingValues = paddingValues,
            viewModel = shareViewModel,
            onNavigateToCodeSettings = onNavigateToCodeSettings,
            onNavigateToBroadcastSettings = onNavigateToBroadcastSettings
        )
    }
}