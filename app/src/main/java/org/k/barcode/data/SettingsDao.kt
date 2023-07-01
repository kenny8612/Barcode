package org.k.barcode.data

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.k.barcode.model.Settings

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun get(): Settings

    @Query("SELECT * FROM settings LIMIT 1")
    fun getFlow(): Flow<Settings>

    @Update(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg settings: Settings)
}