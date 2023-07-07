package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.data.AppDatabase
import org.k.barcode.ui.screen.AppSettingsScreen
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.viewmodel.AppSettingsViewModel

fun NavGraphBuilder.appSettingsScreen(
    paddingValues: PaddingValues,
    appSettingsViewModel: AppSettingsViewModel,
    appDatabase: AppDatabase,
    onNavigateToCodeSettings: () -> Unit
) {
    composable(route = Screen.AppSettings.route) {
        AppSettingsScreen(
            paddingValues,
            appSettingsViewModel,
            appDatabase,
            onNavigateToCodeSettings
        )
    }
}