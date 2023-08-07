package org.k.barcode.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.k.barcode.BarcodeObserver
import org.k.barcode.Constant
import org.k.barcode.repository.DatabaseRepository
import org.k.barcode.repository.DecoderRepository
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.model.KeyInfo
import org.k.barcode.repository.ScanKeyRepository
import org.k.barcode.room.CodeDetails
import org.k.barcode.room.Settings
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    application: Application,
    private val databaseRepository: DatabaseRepository,
    decoderRepository: DecoderRepository,
    scanKeyRepository: ScanKeyRepository
) : AndroidViewModel(application) {
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    private val _code1D = MutableLiveData<List<CodeDetails>>()
    val code1D: LiveData<List<CodeDetails>> = _code1D

    private val _code2D = MutableLiveData<List<CodeDetails>>()
    val code2D: LiveData<List<CodeDetails>> = _code2D

    private val _codeOthers = MutableLiveData<List<CodeDetails>>()
    val codeOthers: LiveData<List<CodeDetails>> = _codeOthers

    private val _decoderEvent = MutableStateFlow(DecoderEvent.Closed)
    val decoderEvent: StateFlow<DecoderEvent> = _decoderEvent.asStateFlow()

    private val _scanKey = MutableStateFlow(KeyInfo())
    val scanKey: StateFlow<KeyInfo> = _scanKey.asStateFlow()

    var codeTypeIndex = mutableStateOf(0)
    var codeDetails = CodeDetails()

    val barcodeObserver = BarcodeObserver(decoderRepository, settings)

    init {
        viewModelScope.launch {
            databaseRepository.getSettingsFlow().onEach {
                _settings.value = it
            }.launchIn(viewModelScope)
        }
        viewModelScope.launch {
            decoderRepository.getEvent().onEach {
                _decoderEvent.value = it
            }.launchIn(viewModelScope)
        }
        viewModelScope.launch {
            databaseRepository.getCodes(Constant.CODE_1D).onEach {
                _code1D.value = it
            }.launchIn(viewModelScope)
        }
        viewModelScope.launch {
            databaseRepository.getCodes(Constant.CODE_2D).onEach {
                _code2D.value = it
            }.launchIn(viewModelScope)
        }
        viewModelScope.launch {
            databaseRepository.getCodes(Constant.CODE_OTHERS).onEach {
                _codeOthers.value = it
            }.launchIn(viewModelScope)
        }
        viewModelScope.launch {
            scanKeyRepository.getScanKey().onEach {
                _scanKey.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun updateSettings(settings: Settings) {
        viewModelScope.launch {
            databaseRepository.updateSettings(settings)
        }
    }

    fun updateCode(codeDetails: CodeDetails) {
        viewModelScope.launch {
            databaseRepository.updateCode(codeDetails)
        }
    }
}