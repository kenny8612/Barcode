package org.k.barcode.data

import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val database: AppDatabase
) {
    suspend fun getSettings() = database.settingsDao().get()
    fun getSettingsFlow() = database.settingsDao().getFlow()
    fun getCodesFlow(type: Int) = database.codeDetailDDao().getCodesFlow(type)
    fun getCodesFlow() = database.codeDetailDDao().getCodesFlow()
    suspend fun getCode(uid: Int) = database.codeDetailDDao().getCode(uid)
}