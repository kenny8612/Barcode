package org.k.barcode.data

import org.k.barcode.model.CodeDetails
import org.k.barcode.model.Settings
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val database: AppDatabase
) {
    suspend fun getSettings() = database.settingsDao().querySettings()
    fun getSettingsFlow() = database.settingsDao().querySettingsFlow()
    suspend fun updateSettings(settings: Settings) = database.settingsDao().update(settings)
    fun getCodes(type: Int) = database.settingsDao().queryCodesByType(type)
    fun getCodesFlow() = database.settingsDao().queryCodesFlow()
    suspend fun getCodes() = database.settingsDao().queryCodes()
    suspend fun updateCode(codeDetails: CodeDetails) = database.settingsDao().update(codeDetails)
}