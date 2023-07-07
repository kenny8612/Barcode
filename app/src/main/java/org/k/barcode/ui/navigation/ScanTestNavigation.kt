package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.ui.screen.ScanTestScreen
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.viewmodel.ScanTestViewModel

fun NavGraphBuilder.scanTestScreen(
    paddingValues: PaddingValues,
    scanTestViewModel: ScanTestViewModel
) {
    composable(route = Screen.ScanTest.route) {
        ScanTestScreen(
            paddingValues,
            scanTestViewModel
        )
    }
}