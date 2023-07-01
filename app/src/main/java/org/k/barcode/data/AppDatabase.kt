package org.k.barcode.data

import androidx.room.Database
import androidx.room.RoomDatabase
import org.k.barcode.model.CodeDetails
import org.k.barcode.model.Settings

@Database(entities = [Settings::class, CodeDetails::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun codeDetailDDao(): CodeDetailDDao
}