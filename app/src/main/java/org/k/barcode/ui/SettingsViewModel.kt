package org.k.barcode.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.k.barcode.Constant.CODE_1D
import org.k.barcode.Constant.CODE_2D
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.model.Settings
import org.k.barcode.model.CodeDetails
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    databaseRepository: DatabaseRepository
) : AndroidViewModel(application) {
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> = _settings

    private val _code1D = MutableLiveData<List<CodeDetails>>()
    val code1D: LiveData<List<CodeDetails>> = _code1D

    private val _code2D = MutableLiveData<List<CodeDetails>>()
    val code2D: LiveData<List<CodeDetails>> = _code2D

    init {
        viewModelScope.launch {
            databaseRepository.getSettingsFlow().onEach {
                _settings.value = it
            }.launchIn(viewModelScope)
            databaseRepository.getCodesFlow(CODE_1D).onEach {
                _code1D.value = it
            }.launchIn(viewModelScope)
            databaseRepository.getCodesFlow(CODE_2D).onEach {
                _code2D.value = it
            }.launchIn(viewModelScope)
        }
    }
}