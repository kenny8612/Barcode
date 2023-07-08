package org.k.barcode.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.k.barcode.data.DatabaseRepository
import org.k.barcode.model.Settings
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    databaseRepository: DatabaseRepository
) : AndroidViewModel(application) {
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> = _settings
    init {
        viewModelScope.launch {
            databaseRepository.getSettingsFlow().onEach {
                _settings.value = it
            }.launchIn(viewModelScope)
        }
    }
}