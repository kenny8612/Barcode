package org.k.barcode.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.k.barcode.BarcodeService
import org.k.barcode.R
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.ui.theme.BarcodeTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var databaseRepository: DatabaseRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SettingsViewModel by viewModels()

        setContent {
            BarcodeTheme(darkTheme = false) {
                val navHostController = rememberNavController()
                var settings by remember { mutableStateOf(false) }

                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val route = navBackStackEntry?.destination?.route
                settings = route != Screen.ScanTest.route

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(id = if (!settings) R.string.scan_test else R.string.scan_settings),
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            actions = {
                                if (!settings) {
                                    IconButton(onClick = {
                                        navHostController.navigate(Screen.AppSettings.route)
                                        settings = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                            navigationIcon = {
                                if (settings) {
                                    IconButton(onClick = {
                                        navHostController.popBackStack()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
                    },
                    content = { paddingValues ->
                        SetupNavGraph(
                            navHostController = navHostController,
                            paddingValues = paddingValues,
                            viewModel = viewModel,
                            databaseRepository = databaseRepository
                        )
                    }
                )
            }
        }
        startService(Intent(this, BarcodeService::class.java))
    }
}