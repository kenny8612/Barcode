package org.k.barcode.room

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.k.barcode.room.CodeDetails
import org.k.barcode.room.Settings

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings LIMIT 1")
    fun querySettingsFlow(): Flow<Settings>

    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun querySettings(): Settings

    @Update(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg settings: Settings)

    @Query("SELECT * FROM codeDetails")
    fun queryCodesFlow(): Flow<List<CodeDetails>>

    @Query("SELECT * FROM codeDetails")
    suspend fun queryCodes(): List<CodeDetails>

    @Query("SELECT * FROM codeDetails WHERE uid IS (:uid)")
    suspend fun queryCodeById(uid: Int): CodeDetails

    @Query("SELECT * FROM codeDetails WHERE type IS (:type)")
    fun queryCodesByType(type: Int): Flow<List<CodeDetails>>

    @Update(entity = CodeDetails::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg codeDetails: CodeDetails)
}