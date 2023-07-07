package org.k.barcode.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.k.barcode.data.AppDatabase
import org.k.barcode.model.CodeDetails
import org.k.barcode.model.Settings

@OptIn(DelicateCoroutinesApi::class)
object DatabaseUtils {
    @JvmStatic
    fun Settings.update(database: AppDatabase) {
        GlobalScope.launch {
            database.settingsDao().update(this@update)
        }
    }

    @JvmStatic
    fun List<CodeDetails>.update(database: AppDatabase) {
        GlobalScope.launch {
            for (codeDetails in this@update)
                database.codeDetailDDao().update(codeDetails)
        }
    }

    @JvmStatic
    fun CodeDetails.update(database: AppDatabase) {
        GlobalScope.launch {
            database.codeDetailDDao().update(this@update)
        }
    }
}