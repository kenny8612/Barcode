package org.k.barcode.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Settings::class, CodeDetails::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun codeDetailDDao(): CodeDetailDDao
}