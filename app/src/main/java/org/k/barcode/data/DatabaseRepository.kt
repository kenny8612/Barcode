package org.k.barcode.data

import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val settingsDao: SettingsDao,
    private val codeDetailDDao: CodeDetailDDao
) {
    suspend fun getSettings() = settingsDao.get()
    fun getSettingsFlow() = settingsDao.getFlow()
    fun getCodesFlow(type:Int) = codeDetailDDao.getCodesFlow(type)
    suspend fun getCodes() = codeDetailDDao.getCodes()
    suspend fun getCodeDetail(uid: Int) = codeDetailDDao.getCodeDetail(uid)
}