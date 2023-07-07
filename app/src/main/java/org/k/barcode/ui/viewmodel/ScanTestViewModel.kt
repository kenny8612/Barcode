package org.k.barcode.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.data.DecoderRepository
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.Settings
import javax.inject.Inject

@HiltViewModel
class ScanTestViewModel @Inject constructor(
    application: Application,
    decoderRepository: DecoderRepository,
    databaseRepository: DatabaseRepository
) : AndroidViewModel(application) {
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> = _settings

    private val _decoderEvent = MutableLiveData<DecoderEvent>()
    val decoderEvent: LiveData<DecoderEvent> = _decoderEvent

    private val _barcode = MutableLiveData<BarcodeInfo>()
    val barcode: LiveData<BarcodeInfo> = _barcode

    var barcodeContent = mutableStateOf("")
    var barcodeInfo = mutableStateOf(BarcodeInfo())

    init {
        viewModelScope.launch {
            databaseRepository.getSettingsFlow().onEach {
                _settings.value = it
            }.launchIn(viewModelScope)
            decoderRepository.getEvent().onEach {
                _decoderEvent.value = it
            }.launchIn(viewModelScope)
            decoderRepository.getBarcode().onEach {
                _barcode.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun reset() {
        _barcode.value = BarcodeInfo()
    }
}