package org.k.barcode.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.k.barcode.BarcodeService
import org.k.barcode.R
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.ui.screen.Screen
import org.k.barcode.ui.theme.BarcodeTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var databaseRepository: DatabaseRepository

    @Inject
    lateinit var decoderManager: DecoderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel: SettingsViewModel by viewModels()
        val decoderViewModel: DecoderViewModel by viewModels()
        val barcodeContentViewModel: BarcodeContentViewModel by viewModels()

        setContent {
            BarcodeTheme(darkTheme = false, dynamicColor = false) {
                MainUI(
                    settingsViewModel = settingsViewModel,
                    decoderViewModel = decoderViewModel,
                    barcodeContentViewModel = barcodeContentViewModel,
                    databaseRepository = databaseRepository,
                    decoderManager = decoderManager
                )
            }
        }

        val permissionList = mutableListOf<String>()
        permissionList.add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= 33)
            permissionList.add(Manifest.permission.POST_NOTIFICATIONS)

        requestPermissionLauncher.launch(permissionList.toTypedArray())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result[Manifest.permission.CAMERA] == true) {
                if (Build.VERSION.SDK_INT >= 33) {
                    if (result[Manifest.permission.POST_NOTIFICATIONS] == true)
                        startForegroundService(Intent(this, BarcodeService::class.java))
                } else {
                    startForegroundService(Intent(this, BarcodeService::class.java))
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI(
    settingsViewModel: SettingsViewModel,
    decoderViewModel: DecoderViewModel,
    barcodeContentViewModel: BarcodeContentViewModel,
    databaseRepository: DatabaseRepository,
    decoderManager: DecoderManager
) {
    val navHostController = rememberNavController()
    var settings by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    settings = navBackStackEntry?.destination?.route != Screen.ScanTest.route

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
                            navHostController.navigate(Screen.AppSettings.route) {
                                //popUpTo(Screen.ScanTest.route){
                                //    inclusive = true
                                //    saveState = true
                                //}
                            }
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
                settingsViewModel = settingsViewModel,
                decoderViewModel = decoderViewModel,
                barcodeContentViewModel = barcodeContentViewModel,
                databaseRepository = databaseRepository,
                decoderManager = decoderManager
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Snackbar(
                        modifier = Modifier
                            .width(200.dp)
                            .height(50.dp)
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = it.visuals.message)
                        }
                    }
                }
            }
        }
    )
}