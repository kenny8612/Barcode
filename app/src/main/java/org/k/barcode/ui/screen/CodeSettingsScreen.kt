package org.k.barcode.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.k.barcode.Constant.CODE_1D
import org.k.barcode.Constant.CODE_2D
import org.k.barcode.Constant.CODE_OTHERS
import org.k.barcode.R
import org.k.barcode.room.CodeDetails
import org.k.barcode.ui.ShareViewModel
import org.k.barcode.utils.SettingsUtils.update

@Composable
fun CodeSettingsScreen(
    paddingValues: PaddingValues,
    shareViewModel: ShareViewModel,
    onNavigateToCodeDetails: (codeDetails: CodeDetails) -> Unit
) {
    val code1DList = shareViewModel.code1D.observeAsState(initial = emptyList())
    val code2DList = shareViewModel.code2D.observeAsState(initial = emptyList())
    val codeOthersDList = shareViewModel.codeOthers.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        CodeTypeTitle(currentIndex = shareViewModel.codeTypeIndex.value) {
            shareViewModel.codeTypeIndex.value = it
        }
        when (shareViewModel.codeTypeIndex.value) {
            CODE_1D -> {
                CodeListUI(
                    code1DList.value,
                    shareViewModel,
                    onNavigateToCodeDetails
                )
            }

            CODE_2D -> {
                CodeListUI(
                    code2DList.value,
                    shareViewModel,
                    onNavigateToCodeDetails
                )
            }

            CODE_OTHERS -> {
                CodeListUI(
                    codeOthersDList.value,
                    shareViewModel,
                    onNavigateToCodeDetails
                )
            }
        }
    }
}

@Composable
fun CodeTypeTitle(currentIndex: Int, onClick: (selectIndex: Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentIndex == 0)
                    MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            ),
            onClick = {
                onClick(CODE_1D)
            }) {
            Text(text = stringResource(id = R.string.code1D))
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentIndex == 1)
                    MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            ),
            onClick = {
                onClick(CODE_2D)
            }) {
            Text(text = stringResource(id = R.string.code2D))
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentIndex == 2)
                    MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            ),
            onClick = {
                onClick(CODE_OTHERS)
            }) {
            Text(text = stringResource(id = R.string.others))
        }
    }
}

@Composable
fun CodeListUI(
    codeList: List<CodeDetails>,
    shareViewModel: ShareViewModel,
    onNavigateToCodeDetails: (codeDetails: CodeDetails) -> Unit
) {
    val scrollerLazyStata = rememberLazyListState()

    if (codeList.isNotEmpty()) {
        LazyColumn(state = scrollerLazyStata) {
            items(codeList) {
                CodeItem(
                    codeDetails = it,
                    shareViewModel = shareViewModel,
                    onNavigateToCodeDetails = onNavigateToCodeDetails
                )
            }
        }
    }
}

@Composable
fun CodeItem(
    codeDetails: CodeDetails,
    shareViewModel: ShareViewModel,
    onNavigateToCodeDetails: (codeDetails: CodeDetails) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                onNavigateToCodeDetails.invoke(codeDetails)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = codeDetails.fullName,
                fontWeight = FontWeight.Medium
            )
            Checkbox(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.End)
                    .padding(end = 4.dp),
                checked = codeDetails.enable,
                onCheckedChange = {
                    codeDetails.copy(enable = it).update(shareViewModel)
                }
            )
        }
    }
}
