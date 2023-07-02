package org.k.barcode.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.k.barcode.R
import org.k.barcode.decoder.Code
import org.k.barcode.model.CodeDetails

@Composable
fun CodeDetailScreen(
    paddingValues: PaddingValues,
    codeDetails: CodeDetails
) {
    Column(
        modifier = Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            start = 8.dp,
            end = 8.dp,
            bottom = 8.dp
        )
    ) {
        CodeTitle(codeDetails.name)
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                when (codeDetails.name) {
                    Code.Code128.route -> {
                        Code128(codeDetails = codeDetails)
                    }

                    Code.Code11.route -> {
                        Code11(codeDetails = codeDetails)
                    }

                    Code.Code39.route -> {
                        Code39(codeDetails = codeDetails)
                    }

                    Code.DotCode.route -> {
                        DotCode(codeDetails = codeDetails)
                    }

                    Code.EAN8.route -> {
                        ELAN(codeDetails = codeDetails)
                    }

                    Code.EAN13.route -> {
                        ELAN(codeDetails = codeDetails)
                    }

                    Code.UPC_A.route -> {
                        UpcA(codeDetails = codeDetails)
                    }

                    Code.UPC_E.route -> {
                        UpcE(codeDetails = codeDetails)
                    }

                    Code.Aztec.route -> {
                        Aztec(codeDetails = codeDetails)
                    }

                    Code.CodaBar.route -> {
                        CodaBar(codeDetails = codeDetails)
                    }

                    Code.Codablock.route -> {
                        CodaBlock(codeDetails = codeDetails)
                    }

                    Code.GM.route -> {
                        GridMatrix(codeDetails = codeDetails)
                    }

                    Code.Gs1_128.route -> {
                        GS1_128(codeDetails = codeDetails)
                    }

                    Code.Int25.route -> {
                        INT25(codeDetails = codeDetails)
                    }

                    Code.HanXin.route -> {
                        HanXin(codeDetails = codeDetails)
                    }

                    Code.MSI.route -> {
                        MSI(codeDetails = codeDetails)
                    }

                    Code.Maxicode.route -> {
                        MaxiCode(codeDetails = codeDetails)
                    }

                    Code.MicroPDF.route -> {
                        MicroPDF(codeDetails = codeDetails)
                    }

                    Code.RSS.route -> {
                        RSS(codeDetails = codeDetails)
                    }

                    Code.Matrix25.route -> {
                        Matrix25(codeDetails = codeDetails)
                    }

                    Code.Telepen.route -> {
                        Telepen(codeDetails = codeDetails)
                    }

                    Code.QR.route -> {
                        QR(codeDetails = codeDetails)
                    }

                    Code.PDF417.route -> {
                        PDF417(codeDetails = codeDetails)
                    }
                }
            }
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
    TransmitCheckDigit(codeDetails)
    Supplemental2(codeDetails)
    Supplemental5(codeDetails)
}

@Composable
fun UpcA(codeDetails: CodeDetails) {

}

@Composable
fun UpcE(codeDetails: CodeDetails) {

}

@Composable
fun Code128(codeDetails: CodeDetails) {

}

@Composable
fun Code11(codeDetails: CodeDetails) {

}

@Composable
fun Code39(codeDetails: CodeDetails) {

}

@Composable
fun DotCode(codeDetails: CodeDetails) {

}

@Composable
fun Aztec(codeDetails: CodeDetails) {

}

@Composable
fun CodaBar(codeDetails: CodeDetails) {

}

@Composable
fun CodaBlock(codeDetails: CodeDetails) {

}

@Composable
fun GridMatrix(codeDetails: CodeDetails) {

}

@Composable
fun GS1_128(codeDetails: CodeDetails) {

}

@Composable
fun INT25(codeDetails: CodeDetails) {

}

@Composable
fun HanXin(codeDetails: CodeDetails) {

}

@Composable
fun MSI(codeDetails: CodeDetails) {

}

@Composable
fun MaxiCode(codeDetails: CodeDetails) {

}

@Composable
fun MicroPDF(codeDetails: CodeDetails) {

}

@Composable
fun RSS(codeDetails: CodeDetails) {

}

@Composable
fun Matrix25(codeDetails: CodeDetails) {

}

@Composable
fun Telepen(codeDetails: CodeDetails) {

}

@Composable
fun PDF417(codeDetails: CodeDetails) {

}

@Composable
fun QR(codeDetails: CodeDetails) {

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