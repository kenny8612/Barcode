package org.k.barcode.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.k.barcode.model.BarcodeInfo
import javax.inject.Inject

@HiltViewModel
class BarcodeContentViewModel @Inject constructor(
    application:Application
) : AndroidViewModel(application) {
    var barcodeContent = mutableStateOf("")
    var barcodeInfo = mutableStateOf(BarcodeInfo())
}