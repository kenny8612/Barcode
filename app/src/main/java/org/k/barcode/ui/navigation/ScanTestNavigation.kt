package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.ui.screen.ScanTestScreen
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.viewmodel.ScanTestViewModel

fun NavGraphBuilder.scanTestScreen(
    paddingValues: PaddingValues,
    scanTestViewModel: ScanTestViewModel,
    decoderManager: DecoderManager,
    onNavigateToCodeSettings: () -> Unit,
) {
    composable(route = Screen.ScanTest.route) {
        ScanTestScreen(
            paddingValues,
            scanTestViewModel,
            decoderManager,
            onNavigateToCodeSettings
        )
    }
}