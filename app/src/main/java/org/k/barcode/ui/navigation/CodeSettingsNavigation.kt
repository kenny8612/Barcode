package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.data.AppDatabase
import org.k.barcode.model.CodeDetails
import org.k.barcode.ui.screen.CodeSettingsScreen
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.viewmodel.CodeSettingsViewModel

fun NavGraphBuilder.codeSettingsScreen(
    paddingValues: PaddingValues,
    codeSettingsViewModel: CodeSettingsViewModel,
    appDatabase: AppDatabase,
    onNavigateToCodeDetails: (codeDetails: CodeDetails) -> Unit
) {
    composable(route = Screen.CodeSettings.route) {
        CodeSettingsScreen(
            paddingValues,
            codeSettingsViewModel,
            appDatabase,
            onNavigateToCodeDetails
        )
    }
}