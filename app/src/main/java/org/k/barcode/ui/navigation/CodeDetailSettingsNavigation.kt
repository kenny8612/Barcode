package org.k.barcode.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.runBlocking
import org.k.barcode.data.AppDatabase
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.ui.screen.CodeDetailScreen
import org.k.barcode.ui.screen.Screen

fun NavGraphBuilder.codeDetailSettingsScreen(
    paddingValues: PaddingValues,
    databaseRepository: DatabaseRepository,
    appDatabase: AppDatabase,
    onSave: () -> Unit
) {
    composable(
        route = Screen.CodeDetail.route,
        arguments = listOf(navArgument("uid") {
            type = NavType.IntType
        })
    ) {
        runBlocking {
            databaseRepository.getCode(it.arguments?.getInt("uid")!!)
        }.also { codeDetail ->
            CodeDetailScreen(
                paddingValues,
                codeDetail,
                appDatabase,
                onSave
            )
        }
    }
}