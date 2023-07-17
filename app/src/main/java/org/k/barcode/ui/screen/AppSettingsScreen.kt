package org.k.barcode.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.greenrobot.eventbus.EventBus
import org.k.barcode.R
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.decoder.DecoderManager
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.ui.ShareViewModel
import org.k.barcode.utils.SettingsUtils.formatKeycode
import org.k.barcode.utils.SettingsUtils.formatLightLevel
import org.k.barcode.utils.SettingsUtils.formatMode
import org.k.barcode.utils.SettingsUtils.keyCodeToIndex
import org.k.barcode.utils.SettingsUtils.update

@Composable
fun AppSettingsScreen(
    paddingValues: PaddingValues,
    viewModel: ShareViewModel,
    onNavigateToCodeSettings: () -> Unit,
    onNavigateToBroadcastSettings: () -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val decoderEvent by viewModel.decoderEvent.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding(), start = 8.dp, end = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SwitchEnable(
            stringResource(id = R.string.scan_service),
            settings.decoderEnable,
            decoderEvent != DecoderEvent.Error
        ) {
            settings.copy(decoderEnable = it).update(viewModel)
        }
        SwitchEnable(
            stringResource(id = R.string.decoder_vibrate), settings.decoderVibrate,
        ) {
            settings.copy(decoderVibrate = it).update(viewModel)
        }
        SwitchEnable(
            stringResource(id = R.string.decoder_sound), settings.decoderSound
        ) {
            settings.copy(decoderSound = it).update(viewModel)
        }
        SwitchEnable(
            stringResource(id = R.string.continuous_decode), settings.continuousDecode
        ) {
            settings.copy(continuousDecode = it).update(viewModel)
        }
        SwitchEnable(
            stringResource(id = R.string.release_decode), settings.releaseDecode
        ) {
            settings.copy(releaseDecode = it).update(viewModel)
        }
        SwitchEnable(
            stringResource(id = R.string.decoder_light),
            settings.decoderLight,
            DecoderManager.instance.supportLight()
        ) {
            settings.copy(decoderLight = it).update(viewModel)
        }
        ListSelect(
            stringResource(id = R.string.decoder_light_level),
            stringArrayResource(id = R.array.decoder_light_level_entries),
            stringArrayResource(id = R.array.decoder_light_level_entries)[settings.lightLevel.ordinal],
            DecoderManager.instance.supportLightLevel()
        ) {
            settings.copy(lightLevel = formatLightLevel(context, it)).update(viewModel)
        }
        ListSelect(
            stringResource(id = R.string.decoder_mode),
            stringArrayResource(id = R.array.decoder_mode_entries),
            stringArrayResource(id = R.array.decoder_mode_entries)[settings.decoderMode.ordinal]
        ) {
            settings.copy(decoderMode = formatMode(context, it)).update(viewModel)
        }
        ListSelect(
            stringResource(id = R.string.decoder_charset),
            stringArrayResource(id = R.array.decoder_charset_entries),
            settings.decoderCharset
        ) {
            settings.copy(decoderCharset = it).update(viewModel)
        }
        ListSelect(
            stringResource(id = R.string.attach_keycode),
            stringArrayResource(id = R.array.attach_keycode_entries),
            stringArrayResource(id = R.array.attach_keycode_entries)[keyCodeToIndex(
                context, settings.attachKeycode
            )]
        ) {
            settings.copy(attachKeycode = formatKeycode(context, it)).update(viewModel)
        }
        EditViewText(
            stringResource(id = R.string.decoder_prefix), settings.decoderPrefix
        ) {
            settings.copy(decoderPrefix = it).update(viewModel)
        }
        EditViewText(
            stringResource(id = R.string.decoder_suffix), settings.decodeSuffix
        ) {
            settings.copy(decodeSuffix = it).update(viewModel)
        }
        EditViewText(
            stringResource(id = R.string.decoder_filter_characters),
            settings.decoderFilterCharacters
        ) {
            settings.copy(decoderFilterCharacters = it).update(viewModel)
        }
        EditViewNumber(
            stringResource(id = R.string.continuous_decode_interval),
            settings.continuousDecodeInterval,
            200,
            10 * 1000
        ) {
            settings.copy(continuousDecodeInterval = it).update(viewModel)
        }
        CodesEditView(
            DecoderManager.instance.supportCode(),
            onNavigateToCodeSettings
        )
        BroadcastSettingsView(onNavigateToBroadcastSettings)
        RestoreSettings()
    }
}

@Composable
fun BroadcastSettingsView(onNavigateToBroadcastSettings: () -> Unit) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(top = 4.dp, bottom = 4.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        onClick = { onNavigateToBroadcastSettings() }
    ) {
        Text(text = stringResource(id = R.string.broadcast_edit))
    }
}

@Composable
fun RestoreSettings() {
    var show by remember { mutableStateOf(false) }

    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(vertical = 4.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        onClick = { show = true }
    ) {
        Text(text = stringResource(id = R.string.restore_settings))
    }

    if (show) {
        AlertDialog(
            onDismissRequest = { show = false },
            confirmButton = {
                TextButton(onClick = {
                    EventBus
                        .getDefault()
                        .post(MessageEvent(Message.RestoreSettings))
                    show = false
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { show = false }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            text = {
                Text(text = stringResource(id = R.string.make_sure_restore_settings))
            }
        )
    }

}

@Composable
fun CodesEditView(
    enable: Boolean,
    onNavigateToCodeSettings: () -> Unit
) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(top = 8.dp, bottom = 4.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        onClick = { onNavigateToCodeSettings() },
        enabled = enable
    ) {
        Text(text = stringResource(id = R.string.codes_edit))
    }
}

@Composable
fun SwitchEnable(
    label: String,
    initValue: Boolean,
    enable: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = 12.dp),
            text = label
        )
        Switch(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
                .padding(end = 12.dp),
            checked = initValue,
            onCheckedChange = {
                onCheckedChange(it)
            },
            enabled = enable
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSelect(
    label: String,
    listValue: Array<String>,
    initValue: String,
    enable: Boolean = true,
    onSelect: (value: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp),
        expanded = expanded,
        onExpandedChange = {
            //expanded = !expanded
        }) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = initValue,
            onValueChange = { },
            label = {
                Text(text = label)
            },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            enabled = enable
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            listValue.forEach {
                DropdownMenuItem(text = {
                    Text(text = it)
                }, onClick = {
                    expanded = false
                    onSelect.invoke(it)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditView(
    label: String,
    value: String,
    keyboardType: KeyboardType,
    onValueChange: (value: String) -> Unit,
    onDone: (value: String) -> Unit,
) {
    var showIcon by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                showIcon = it.hasFocus
                if (!showIcon) onDone(value)
            }
            .onKeyEvent {
                if (it.key == Key.Back) focusManager.clearFocus(true)
                true
            },
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(text = label)
        },
        trailingIcon = {
            if (showIcon) {
                IconButton(onClick = {
                    focusManager.clearFocus(true)
                }) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = label)
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun EditViewText(
    label: String,
    newValue: String,
    onDone: (value: String) -> Unit,
) {
    var value by remember { mutableStateOf(newValue) }
    var prevValue by remember { mutableStateOf(value) }

    if (prevValue != newValue) {
        prevValue = newValue
        value = newValue
    }

    EditView(label, value, KeyboardType.Text, onValueChange = {
        value = it
    }, onDone = {
        if (it != newValue) onDone(it)
    })
}

@Composable
fun EditViewNumber(
    label: String,
    newValue: Int,
    min: Int = 0,
    max: Int,
    onDone: (value: Int) -> Unit,
) {
    var value by remember { mutableStateOf(newValue.toString()) }
    var prevValue by remember { mutableStateOf(newValue) }

    if (prevValue != newValue) {
        prevValue = newValue
        value = newValue.toString()
    }

    EditView(label, value, KeyboardType.Number, onValueChange = {
        value = it.filter { symbol ->
            symbol.isDigit()
        }
    }, onDone = {
        if (it.isNotEmpty() && it.toInt() in min..max && it.toInt() != newValue)
            onDone(value.toInt())
    })
}

