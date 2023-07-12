package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.ui.screen.CodeSettingsScreen
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.ShareViewModel

fun NavGraphBuilder.codeSettingsScreen(
    paddingValues: PaddingValues,
    shareViewModel: ShareViewModel,
    onNavigateToCodeDetails: () -> Unit
) {
    composable(route = Screen.CodeSettings.route) {
        CodeSettingsScreen(
            paddingValues = paddingValues,
            shareViewModel = shareViewModel
        ) {
            if (it.supportDetails) {
                shareViewModel.codeDetails = it
                onNavigateToCodeDetails.invoke()
            }
        }
    }
}