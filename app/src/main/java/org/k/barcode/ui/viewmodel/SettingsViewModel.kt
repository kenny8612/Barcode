package org.k.barcode.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.data.DecoderRepository
import org.k.barcode.decoder.DecoderEvent
import org.k.barcode.model.Settings
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    databaseRepository: DatabaseRepository,
    decoderRepository: DecoderRepository
) : AndroidViewModel(application) {
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings

    var decoderEvent: StateFlow<DecoderEvent> = decoderRepository.getEvent()

    init {
        viewModelScope.launch {
            databaseRepository.getSettingsFlow().stateIn(viewModelScope).collect {
                _settings.value = it
            }
        }
    }
}