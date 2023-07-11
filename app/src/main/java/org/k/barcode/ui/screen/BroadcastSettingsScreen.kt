package org.k.barcode.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.k.barcode.R
import org.k.barcode.model.Settings
import org.k.barcode.utils.DatabaseUtils.send

@Composable
fun BroadcastSettingsScreen(
    paddingValues: PaddingValues,
    settings: Settings,
    onSave: () -> Unit
) {
    val broadcastStartDecode = remember { mutableStateOf(settings.broadcastStartDecode) }
    val broadcastStopDecode = remember { mutableStateOf(settings.broadcastStopDecode) }
    val broadcastDecodeData = remember { mutableStateOf(settings.broadcastDecodeData) }
    val broadcastDecodeDataByte = remember { mutableStateOf(settings.broadcastDecodeDataByte) }
    val broadcastDecodeDataString = remember { mutableStateOf(settings.broadcastDecodeDataString) }

    val saveEnable by remember {
        derivedStateOf {
            broadcastStartDecode.value.isNotEmpty()
                    && broadcastStopDecode.value.isNotEmpty()
                    && broadcastDecodeData.value.isNotEmpty()
                    && broadcastDecodeDataByte.value.isNotEmpty()
                    && broadcastDecodeDataString.value.isNotEmpty()
        }
    }

    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding(), start = 8.dp, end = 8.dp)
            .fillMaxSize()
    ) {
        BroadcastEditView(
            label = stringResource(id = R.string.action_start_decode),
            data = broadcastStartDecode
        )
        BroadcastEditView(
            label = stringResource(id = R.string.action_stop_decode),
            data = broadcastStopDecode
        )
        BroadcastEditView(
            label = stringResource(id = R.string.action_decode_data),
            data = broadcastDecodeData
        )
        BroadcastEditView(
            label = stringResource(id = R.string.extra_decode_data_byte),
            data = broadcastDecodeDataByte
        )
        BroadcastEditView(
            label = stringResource(id = R.string.extra_decode_data_string),
            data = broadcastDecodeDataString
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = {
                if (saveEnable) {
                    settings.copy(
                        broadcastStartDecode = broadcastStartDecode.value,
                        broadcastStopDecode = broadcastStopDecode.value,
                        broadcastDecodeData = broadcastDecodeData.value,
                        broadcastDecodeDataByte = broadcastDecodeDataByte.value,
                        broadcastDecodeDataString = broadcastDecodeDataString.value
                    ).send()
                    onSave.invoke()
                }
            },
            enabled = saveEnable
        ) {
            Text(text = stringResource(id = R.string.save_code))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastEditView(label: String, data: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        label = {
            Text(text = label)
        },
        value = data.value,
        onValueChange = {
            data.value = it
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = data.value.isEmpty()
    )
}