package org.k.barcode.data

import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val database: AppDatabase
) {
    fun getSettingsFlow() = database.settingsDao().getFlow()
    fun getCodesFlow(type: Int) = database.codeDetailDDao().getCodesFlow(type)
    fun getCodesFlow() = database.codeDetailDDao().getCodesFlow()
}