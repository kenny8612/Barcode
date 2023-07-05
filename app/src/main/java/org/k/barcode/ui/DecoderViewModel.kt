package org.k.barcode.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.k.barcode.data.DecoderRepository
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.model.BarcodeInfo
import javax.inject.Inject

@HiltViewModel
class DecoderViewModel @Inject constructor(decoderRepository: DecoderRepository) :
    ViewModel() {
    private val _decoderEvent = MutableLiveData<DecoderEvent>()
    val decoderEvent: LiveData<DecoderEvent> = _decoderEvent

    private val _barcode = MutableLiveData<BarcodeInfo>()
    val barcode: LiveData<BarcodeInfo> = _barcode

    init {
        decoderRepository.getEvent().onEach {
            _decoderEvent.value = it
        }.launchIn(viewModelScope)
        decoderRepository.getBarcode().onEach {
            _barcode.value = it
        }.launchIn(viewModelScope)
    }
}