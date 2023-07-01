package org.k.barcode.ui.settings.code

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.k.barcode.R
import org.k.barcode.model.CodeDetails
import org.k.barcode.ui.settings.send

@Composable
fun Elan8(codeDetails: CodeDetails) {
    TransmitCheckDigit(codeDetails)
    Supplemental2(codeDetails)
    Supplemental5(codeDetails)
}

@Composable
fun Supplemental2(codeDetails: CodeDetails) {
    var supplemental2Enable by remember { mutableStateOf(codeDetails.supplemental2) }

    Row(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = supplemental2Enable,
            onCheckedChange = { enable ->
                supplemental2Enable = !supplemental2Enable
                codeDetails.also { it.supplemental2 = enable }.send()
            }
        )
        Text(text = stringResource(id = R.string.supplemental2))
    }
}

@Composable
fun Supplemental5(codeDetails: CodeDetails) {
    var supplemental5Enable by remember { mutableStateOf(codeDetails.supplemental5) }

    Row(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = supplemental5Enable,
            onCheckedChange = { enable ->
                supplemental5Enable = !supplemental5Enable
                codeDetails.also { it.supplemental5 = enable }.send()
            }
        )
        Text(text = stringResource(id = R.string.supplemental5))
    }
}


@Composable
fun TransmitCheckDigit(codeDetails: CodeDetails) {
    var transmitCheckDigitEnable by remember { mutableStateOf(codeDetails.transmitCheckDigit) }

    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = transmitCheckDigitEnable,
            onCheckedChange = { enable ->
                transmitCheckDigitEnable = !transmitCheckDigitEnable
                codeDetails.also { it.transmitCheckDigit = enable }.send()
            }
        )
        Text(text = stringResource(id = R.string.transmitCheckDigit))
    }
}