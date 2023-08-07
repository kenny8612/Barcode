package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.ui.ShareViewModel
import org.k.barcode.ui.screen.ScanTestScreen
import org.k.barcode.ui.screen.Screen

fun NavGraphBuilder.scanTestScreen(
    paddingValues: PaddingValues,
    shareViewModel: ShareViewModel,
) {
    composable(route = Screen.ScanTest.route) {
        ScanTestScreen(
            paddingValues = paddingValues,
            viewModel = shareViewModel
        )
    }
}