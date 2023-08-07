package org.k.barcode.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.k.barcode.Constant.UPC_PREAMBLE_DATA_ONLY
import org.k.barcode.Constant.UPC_PREAMBLE_SYSTEM_COUNTRY_DATA
import org.k.barcode.Constant.UPC_PREAMBLE_SYSTEM_DATA
import org.k.barcode.R
import org.k.barcode.room.CodeDetails
import org.k.barcode.ui.ShareViewModel
import org.k.barcode.decoder.Code.D1.*
import org.k.barcode.decoder.Code.D2.*
import org.k.barcode.decoder.Code.Post.*
import org.k.barcode.utils.SettingsUtils.update

@Composable
fun CodeDetailScreen(
    paddingValues: PaddingValues,
    shareViewModel: ShareViewModel,
    onSave: () -> Unit
) {
    val codeDetails = shareViewModel.codeDetails

    Column(
        modifier = Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            start = 8.dp,
            end = 8.dp
        )
    ) {
        CodeTitle(name = codeDetails.fullName)
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            when (codeDetails.name) {
                EAN8.name, EAN13.name -> ELAN(codeDetails)
                UPC_A.name, UPC_E.name -> UPC(codeDetails)
                Code11.name, INT25.name, Matrix25.name -> LCT(codeDetails)
                CodaBar.name -> {
                    LengthView(codeDetails = codeDetails)
                    CheckBoxView(
                        stringResource(id = R.string.start_stop_characters),
                        codeDetails.startStopCharacters
                    ) {
                        codeDetails.startStopCharacters = it
                    }
                }
                Code39.name -> {
                    LCT(codeDetails)
                    CheckBoxView(
                        label = stringResource(id = R.string.full_ascii),
                        value = codeDetails.fullAscii
                    ) {
                        codeDetails.fullAscii = it
                    }
                }
                MSI.name -> {
                    LengthView(codeDetails)
                    TransmitCheckDigit(codeDetails)
                    Algorithm(codeDetails)
                }
                ChinaPost.name -> LCT(codeDetails)
                JapanPostal.name -> TransmitCheckDigit(codeDetails)
                else -> LengthView(codeDetails)
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = {
                codeDetails.update(shareViewModel)
                onSave.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.save_code))
        }
    }
}

@Composable
fun CodeTitle(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ELAN(codeDetails: CodeDetails) {
    TransmitCheckDigit(codeDetails = codeDetails)
    CheckBoxView(stringResource(id = R.string.supplemental2), codeDetails.supplemental2) {
        codeDetails.supplemental2 = it
    }
    CheckBoxView(stringResource(id = R.string.supplemental5), codeDetails.supplemental5) {
        codeDetails.supplemental5 = it
    }
}

@Composable
fun UPC(codeDetails: CodeDetails) {
    ELAN(codeDetails = codeDetails)
    UpcPreamble(codeDetails = codeDetails)
}

@Composable
fun UpcPreamble(codeDetails: CodeDetails) {
    var preamble by remember {
        mutableStateOf(codeDetails.upcPreamble)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = preamble == UPC_PREAMBLE_DATA_ONLY,
            onClick = {
                preamble = UPC_PREAMBLE_DATA_ONLY
                codeDetails.upcPreamble = preamble
            })
        Text(text = stringResource(id = R.string.transmit_no_preamble))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = preamble == UPC_PREAMBLE_SYSTEM_DATA,
            onClick = {
                preamble = UPC_PREAMBLE_SYSTEM_DATA
                codeDetails.upcPreamble = preamble
            })
        Text(text = stringResource(id = R.string.transmit_system_character_only))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = preamble == UPC_PREAMBLE_SYSTEM_COUNTRY_DATA,
            onClick = {
                preamble = UPC_PREAMBLE_SYSTEM_COUNTRY_DATA
                codeDetails.upcPreamble = preamble
            })
        Text(text = stringResource(id = R.string.transmit_system_character_and_country_code))
    }
}

@Composable
fun LengthView(codeDetails: CodeDetails) {
    MinLength(codeDetails = codeDetails)
    MaxLength(codeDetails = codeDetails)
}

@Composable
fun CheckDigit(codeDetails: CodeDetails) {
    CheckBoxView(stringResource(id = R.string.checkDigit), codeDetails.checkDigit) {
        codeDetails.checkDigit = it
    }
}

@Composable
fun TransmitCheckDigit(codeDetails: CodeDetails) {
    CheckBoxView(stringResource(id = R.string.transmitCheckDigit), codeDetails.transmitCheckDigit) {
        codeDetails.transmitCheckDigit = it
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinLength(codeDetails: CodeDetails) {
    var curValue by remember { mutableStateOf(codeDetails.minLength.toString()) }
    val oriValue by remember { mutableStateOf(codeDetails.minLength) }

    OutlinedTextField(
        value = curValue,
        onValueChange = {
            curValue = it.filter { symbol ->
                symbol.isDigit()
            }.apply {
                codeDetails.minLength =
                    if (it.isNotEmpty() && it.toInt() > 0) it.toInt() else oriValue
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        label = {
            Text(text = stringResource(id = R.string.min_length))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaxLength(codeDetails: CodeDetails) {
    var curValue by remember { mutableStateOf(codeDetails.maxLength.toString()) }
    val oriValue by remember { mutableStateOf(codeDetails.maxLength) }

    OutlinedTextField(
        value = curValue,
        onValueChange = {
            curValue = it.filter { symbol ->
                symbol.isDigit()
            }.apply {
                codeDetails.maxLength =
                    if (it.isNotEmpty() && it.toInt() > 0) it.toInt() else oriValue
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        label = {
            Text(text = stringResource(id = R.string.max_length))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun CheckBoxView(label: String, value: Boolean, onCheckedChange: ((Boolean) -> Unit)) {
    var checkValue by remember { mutableStateOf(value) }

    Row(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkValue,
            onCheckedChange = {
                checkValue = it
                onCheckedChange.invoke(it)
            }
        )
        Text(text = label)
    }
}

@Composable
fun LCT(codeDetails: CodeDetails) {
    LengthView(codeDetails = codeDetails)
    CheckDigit(codeDetails = codeDetails)
    TransmitCheckDigit(codeDetails = codeDetails)
}

@Composable
fun Algorithm(codeDetails: CodeDetails) {
    var algorithm by remember {
        mutableStateOf(codeDetails.algorithm)
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 4.dp),
        text = stringResource(id = R.string.check_mode)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = algorithm == 0,
            onClick = {
                algorithm = 0
                codeDetails.algorithm = 0
            }
        )
        Text(text = stringResource(id = R.string.algorithm_close))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = algorithm == 2,
            onClick = {
                algorithm = 2
                codeDetails.algorithm = 2
            }
        )
        Text(text = stringResource(id = R.string.mod10_mod10))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = algorithm == 3,
            onClick = {
                algorithm = 3
                codeDetails.algorithm = 3
            }
        )
        Text(text = stringResource(id = R.string.mod10_mod11))
    }
}
