package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.ui.ShareViewModel
import org.k.barcode.ui.screen.BroadcastSettingsScreen
import org.k.barcode.ui.screen.Screen

fun NavGraphBuilder.broadcastSettingsScreen(
    paddingValues: PaddingValues,
    shareViewModel: ShareViewModel,
    onSave: () -> Unit
) {
    composable(route = Screen.BroadcastSettings.route) {
        BroadcastSettingsScreen(
            paddingValues = paddingValues,
            shareViewModel = shareViewModel,
            onSave = onSave
        )
    }
}