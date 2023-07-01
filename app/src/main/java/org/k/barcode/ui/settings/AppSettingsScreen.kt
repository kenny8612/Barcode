package org.k.barcode.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.greenrobot.eventbus.EventBus
import org.k.barcode.R
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.data.Settings
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.ui.Screen
import org.k.barcode.ui.SettingsViewModel
import org.k.barcode.utils.SettingsUtils.formatKeycode
import org.k.barcode.utils.SettingsUtils.formatMode
import org.k.barcode.utils.SettingsUtils.keyCodeToIndex

fun Settings.send() {
    EventBus.getDefault().post(MessageEvent(Message.UpdateSettings, this))
}

@Composable
fun AppSettings(
    paddingValues: PaddingValues,
    navHostController: NavHostController,
    viewModel: SettingsViewModel,
    databaseRepository: DatabaseRepository
) {
    val context = LocalContext.current
    val settings by viewModel.settings.observeAsState(databaseRepository.getSettings())
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(scrollState)
    ) {
        SwitchEnable(
            stringResource(id = R.string.scan), settings.decoderEnable
        ) { enable ->
            settings.copy().also { it.decoderEnable = enable }.send()
        }
        SwitchEnable(
            stringResource(id = R.string.decoder_vibrate),
            settings.decoderVibrate,
        ) { enable ->
            settings.copy().also { it.decoderVibrate = enable }.send()
        }
        SwitchEnable(
            stringResource(id = R.string.decoder_sound), settings.decoderSound
        ) { enable ->
            settings.copy().also { it.decoderSound = enable }.send()
        }
        SwitchEnable(
            stringResource(id = R.string.continuous_decode), settings.continuousDecode
        ) { enable ->
            settings.copy().also { it.continuousDecode = enable }.send()
        }
        SwitchEnable(
            stringResource(id = R.string.release_decode), settings.releaseDecode
        ) { enable ->
            settings.copy().also { it.releaseDecode = enable }.send()
        }
        SwitchEnable(
            stringResource(id = R.string.decoder_light), settings.decoderLight
        ) { enable ->
            settings.copy().also { it.decoderLight = enable }.send()
        }
        CodesEditView(navHostController)
        ListSelect(
            stringResource(id = R.string.decoder_mode),
            stringArrayResource(id = R.array.decoder_mode_entries),
            stringArrayResource(id = R.array.decoder_mode_entries)[settings.decoderMode.ordinal]
        ) { result ->
            settings.copy().also { it.decoderMode = formatMode(context, result) }.send()
        }
        ListSelect(
            stringResource(id = R.string.decoder_charset),
            stringArrayResource(id = R.array.decoder_charset_entries),
            settings.decoderCharset
        ) { result ->
            settings.copy().also { it.decoderCharset = result }.send()
        }
        ListSelect(
            stringResource(id = R.string.attach_keycode),
            stringArrayResource(id = R.array.attach_keycode_entries),
            stringArrayResource(id = R.array.attach_keycode_entries)[keyCodeToIndex(
                context, settings.attachKeycode
            )]
        ) { result ->
            settings.copy().also { it.attachKeycode = formatKeycode(context, result) }.send()
        }
        EditViewText(
            stringResource(id = R.string.decoder_prefix), settings.decoderPrefix
        ) { result ->
            settings.copy().also { it.decoderPrefix = result }.send()

        }
        EditViewText(
            stringResource(id = R.string.decoder_suffix), settings.decodeSuffix
        ) { result ->
            settings.copy().also { it.decodeSuffix = result }.send()
        }
        EditViewText(
            stringResource(id = R.string.decoder_filter_characters),
            settings.decoderFilterCharacters
        ) { result ->
            settings.copy().also { it.decoderFilterCharacters = result }.send()
        }
        EditViewNumber(
            stringResource(id = R.string.continuous_decode_interval),
            settings.continuousDecodeInterval,
            200,
            10 * 1000
        ) { result ->
            settings.copy().also { it.continuousDecodeInterval = result }.send()
        }
    }
}

@Composable
fun CodesEditView(navHostController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = stringResource(id = R.string.codes_edit),
            fontWeight = FontWeight.Medium
        )
        IconButton(modifier = Modifier.padding(end = 4.dp), onClick = {
            navHostController.navigate(Screen.CodeSettings.setIndex(0))
        }) {
            Icon(
                imageVector = Icons.Default.ArrowForward, contentDescription = null
            )
        }
    }
}

@Composable
fun SwitchEnable(
    label: String, initValue: Boolean, onCheckedChange: ((Boolean) -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = label,
            fontWeight = FontWeight.Medium
        )
        Switch(modifier = Modifier.padding(end = 12.dp), checked = initValue, onCheckedChange = {
            onCheckedChange(it)
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSelect(
    label: String,
    listValue: Array<String>,
    initValue: String,
    onSelect: (value: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp),
        expanded = expanded,
        onExpandedChange = {
            //expanded = !expanded
        }) {
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .menuAnchor(),
            readOnly = true,
            value = initValue,
            onValueChange = { },
            label = {
                Text(
                    text = label, fontWeight = FontWeight.Medium
                )
            },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            })
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            listValue.forEach {
                DropdownMenuItem(text = {
                    Text(text = it)
                }, onClick = {
                    expanded = false
                    onSelect(it)
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
    onClear: () -> Unit,
    onDone: (value: String) -> Unit,
) {
    var showIcon by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                showIcon = it.hasFocus.and(value.isNotEmpty())
                if (!it.hasFocus) onDone(value)
            }
            .onKeyEvent {
                if (it.key == Key.Back) focusManager.clearFocus(true)
                true
            },
        value = value,
        onValueChange = {
            onValueChange(it)
            showIcon = it.isNotEmpty()
        },
        label = {
            Text(
                text = label, fontWeight = FontWeight.Medium
            )
        },
        trailingIcon = {
            if (showIcon) {
                IconButton(onClick = {
                    onClear()
                    showIcon = false
                }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = label)
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

@Composable
fun EditViewText(
    label: String,
    initValue: String,
    onDone: (value: String) -> Unit,
) {
    var value by remember { mutableStateOf(initValue) }

    EditView(label, value, KeyboardType.Text, onValueChange = {
        value = it
    }, onClear = {
        value = ""
    }, onDone = {
        if (value != initValue) onDone(value)
    })
}

@Composable
fun EditViewNumber(
    label: String,
    initValue: Int,
    min: Int = 0,
    max: Int,
    onDone: (value: Int) -> Unit,
) {
    var value by remember { mutableStateOf(initValue.toString()) }

    EditView(label, value, KeyboardType.Number, onValueChange = {
        value = it.filter { symbol ->
            symbol.isDigit()
        }
    }, onClear = {
        value = ""
    }, onDone = {
        value = if (it.isNotEmpty() && it.toInt() in min..max) it
        else initValue.toString()
        onDone(value.toInt())
    })
}

