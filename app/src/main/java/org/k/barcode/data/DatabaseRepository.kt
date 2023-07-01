package org.k.barcode.data

import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val settingsDao: SettingsDao,
    private val codeDetailDDao: CodeDetailDDao
) {
    val settings = settingsDao.getFlow()
    val codes1D = codeDetailDDao.getCodesFlow(0)
    val codes2D = codeDetailDDao.getCodesFlow(1)
    val codesOthers = codeDetailDDao.getCodesFlow(2)
    suspend fun getCodeDetailByName(name: String) = codeDetailDDao.getCodeByName(name)
    suspend fun getSettings() = settingsDao.get()
}