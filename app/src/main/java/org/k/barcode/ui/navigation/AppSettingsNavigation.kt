package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.ui.screen.AppSettingsScreen
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.viewmodel.SettingsViewModel

fun NavGraphBuilder.appSettingsScreen(
    paddingValues: PaddingValues,
    settingsViewModel: SettingsViewModel,
    decoderManager: DecoderManager,
    onNavigateToCodeSettings: () -> Unit,
    onNavigateToBroadcastSettings: () -> Unit
) {
    composable(route = Screen.AppSettings.route) {
        AppSettingsScreen(
            paddingValues,
            settingsViewModel,
            decoderManager,
            onNavigateToCodeSettings,
            onNavigateToBroadcastSettings
        )
    }
}