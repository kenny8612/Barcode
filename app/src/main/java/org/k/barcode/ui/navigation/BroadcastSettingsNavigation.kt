package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.runBlocking
import org.k.barcode.data.AppDatabase
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.ui.screen.BroadcastSettingsScreen
import org.k.barcode.ui.screen.Screen

fun NavGraphBuilder.broadcastSettingsScreen(
    paddingValues: PaddingValues,
    databaseRepository: DatabaseRepository,
    appDatabase: AppDatabase,
    onSave: () -> Unit
) {
    composable(route = Screen.BroadcastSettings.route) {
        runBlocking {
            databaseRepository.getSettings()
        }.also {
            BroadcastSettingsScreen(
                paddingValues = paddingValues,
                appDatabase = appDatabase,
                settings = it
            ) {
                onSave.invoke()
            }
        }
    }
}