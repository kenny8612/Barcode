package org.k.barcode.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.data.Settings
import org.k.barcode.data.CodeDetails
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(databaseRepository: DatabaseRepository) :
    ViewModel() {
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> = _settings

    private val _code1D = MutableLiveData<List<CodeDetails>>()
    val code1D: LiveData<List<CodeDetails>> = _code1D

    private val _code2D = MutableLiveData<List<CodeDetails>>()
    val code2D: LiveData<List<CodeDetails>> = _code2D

    init {
        databaseRepository.settings.onEach {
            _settings.value = it
        }.launchIn(viewModelScope)
        databaseRepository.codes1D.onEach {
            _code1D.value = it
        }.launchIn(viewModelScope)
        databaseRepository.codes2D.onEach {
            _code2D.value = it
        }.launchIn(viewModelScope)
    }
}