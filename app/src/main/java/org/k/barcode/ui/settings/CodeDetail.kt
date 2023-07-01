package org.k.barcode.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.k.barcode.data.Code1DName
import org.k.barcode.data.CodeDetails
import org.k.barcode.ui.settings.code.Elan8

@Composable
fun CodeDetail(
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
            CodeParameter(codeDetails = codeDetails)
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
fun CodeParameter(codeDetails: CodeDetails) {
    Column {
        when (codeDetails.name) {
            Code1DName.elan8 -> {
                Elan8(codeDetails = codeDetails)
            }
        }
        when (codeDetails.name) {
            Code1DName.elan13 -> {
                Elan8(codeDetails = codeDetails)
            }
        }
    }
}