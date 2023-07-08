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
import org.k.barcode.R
import org.k.barcode.data.AppDatabase
import org.k.barcode.decoder.Code
import org.k.barcode.model.CodeDetails
import org.k.barcode.utils.DatabaseUtils.update

@Composable
fun CodeDetailScreen(
    paddingValues: PaddingValues,
    codeDetails: CodeDetails,
    appDatabase: AppDatabase,
    onSave: () -> Unit
) {
    val value by remember { mutableStateOf(codeDetails) }

    Column(
        modifier = Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            start = 8.dp,
            end = 8.dp
        )
    ) {
        CodeTitle(name = codeDetails.name)
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            when (codeDetails.name) {
                Code.EAN8.aliasName -> {
                    ELAN(codeDetails = value)
                }

                Code.EAN13.aliasName -> {
                    ELAN(codeDetails = value)
                }

                Code.UPC_A.aliasName -> {
                    UPC(codeDetails = value)
                }

                Code.UPC_E.aliasName -> {
                    UPC(codeDetails = value)
                }

                Code.CodaBar.aliasName -> {
                    LengthView(codeDetails = codeDetails)
                    CheckBoxView(
                        stringResource(id = R.string.start_stop_characters),
                        codeDetails.startStopCharacters
                    ) {
                        codeDetails.startStopCharacters = it
                    }
                }

                Code.Code11.aliasName -> {
                    LCT(codeDetails = value)
                }

                Code.Code39.aliasName -> {
                    LCT(codeDetails = value)
                    CheckBoxView(
                        label = stringResource(id = R.string.full_ascii),
                        value = value.fullAscii
                    ) {
                        value.fullAscii = it
                    }
                }

                Code.INT25.aliasName -> {
                    LCT(codeDetails = value)
                }

                Code.Matrix25.aliasName -> {
                    LCT(codeDetails = value)
                }

                Code.MSI.aliasName -> {
                    LCT(codeDetails = value)
                }

                Code.Telepen.aliasName -> {

                }

                else -> {
                    LengthView(codeDetails = value)
                }

            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = {
                if (value != codeDetails)
                    value.update(appDatabase)
                onSave()
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
            selected = preamble == 0,
            onClick = {
                preamble = 0
                codeDetails.upcPreamble = 0
            })
        Text(text = stringResource(id = R.string.transmit_no_preamble))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = preamble == 1,
            onClick = {
                preamble = 1
                codeDetails.upcPreamble = 1
            })
        Text(text = stringResource(id = R.string.transmit_system_character_only))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = preamble == 2,
            onClick = {
                preamble = 2
                codeDetails.upcPreamble = 2
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
    var minValue by remember { mutableStateOf(codeDetails.minLength.toString()) }

    OutlinedTextField(
        value = minValue,
        onValueChange = {
            minValue = it.filter { symbol ->
                symbol.isDigit()
            }
            if (minValue.isNotEmpty() && minValue.toInt() > 0)
                codeDetails.minLength = minValue.toInt()
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
    var maxValue by remember { mutableStateOf(codeDetails.maxLength.toString()) }

    OutlinedTextField(
        value = maxValue,
        onValueChange = {
            maxValue = it.filter { symbol ->
                symbol.isDigit()
            }
            if (maxValue.isNotEmpty() && maxValue.toInt() > 0)
                codeDetails.maxLength = maxValue.toInt()
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
                onCheckedChange(it)
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
