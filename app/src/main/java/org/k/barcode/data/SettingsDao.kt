package org.k.barcode.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun get(): Settings

    @Query("SELECT * FROM settings LIMIT 1")
    fun getFlow(): Flow<Settings>

    @Update(entity = Settings::class)
    suspend fun update(settings: Settings)
}