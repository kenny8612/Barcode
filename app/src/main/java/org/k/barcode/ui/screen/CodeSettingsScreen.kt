package org.k.barcode.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.greenrobot.eventbus.EventBus
import org.k.barcode.Constant.CODE_1D
import org.k.barcode.Constant.CODE_2D
import org.k.barcode.Constant.CODE_OTHERS
import org.k.barcode.R
import org.k.barcode.model.CodeDetails
import org.k.barcode.message.Message
import org.k.barcode.message.MessageEvent
import org.k.barcode.ui.SettingsViewModel

fun CodeDetails.send() {
    EventBus.getDefault().post(MessageEvent(Message.UpdateCode, this))
}

@Composable
fun CodeSettingsScreen(
    paddingValues: PaddingValues,
    navHostController: NavHostController,
    viewModel: SettingsViewModel,
    currentIndex: Int
) {
    val code1DList = viewModel.code1D.observeAsState(initial = emptyList())
    val code2DList = viewModel.code2D.observeAsState(initial = emptyList())
    var selectIndex by remember { mutableStateOf(currentIndex) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        CodeTypeTitle(currentIndex = selectIndex) {
            selectIndex = it
            navHostController.currentBackStackEntry?.arguments?.putInt("index", it)
        }
        when (selectIndex) {
            CODE_1D -> {
                CodeListUI(
                    navHostController = navHostController,
                    codeList = code1DList.value
                )
            }

            CODE_2D -> {
                CodeListUI(
                    navHostController = navHostController,
                    codeList = code2DList.value
                )
            }

            CODE_OTHERS -> {

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
    navHostController: NavHostController,
    codeList: List<CodeDetails>
) {
    val scrollerLazyStata = rememberLazyListState()

    if (codeList.isNotEmpty()) {
        LazyColumn(state = scrollerLazyStata) {
            items(codeList) {
                CodeItem(
                    navHostController = navHostController,
                    codeDetails = it
                )
            }
        }
    }
}

@Composable
fun CodeItem(
    navHostController: NavHostController,
    codeDetails: CodeDetails
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                text = codeDetails.name
            )
            IconButton(
                onClick = {
                    navHostController.navigate(route = Screen.CodeDetail.codeName(codeDetails.name))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
            Checkbox(
                modifier = Modifier.padding(end = 4.dp),
                checked = codeDetails.enable,
                onCheckedChange = { enable ->
                    codeDetails.copy().also { it.enable = enable }.send()
                }
            )
        }
    }
}
