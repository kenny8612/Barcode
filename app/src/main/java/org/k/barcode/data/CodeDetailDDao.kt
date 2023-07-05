package org.k.barcode.data

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.k.barcode.model.CodeDetails

@Dao
interface CodeDetailDDao {
    @Query("SELECT * FROM codeDetails")
    suspend fun getCodes(): List<CodeDetails>

    @Query("SELECT * FROM codeDetails")
    fun getCodesFlow(): Flow<List<CodeDetails>>

    @Query("SELECT * FROM codeDetails WHERE type IS (:type)")
    fun getCodesFlow(type: Int): Flow<List<CodeDetails>>

    @Query("SELECT * FROM codeDetails WHERE uid IS (:uid)")
    suspend fun getCodeDetail(uid: Int): CodeDetails

    @Update(entity = CodeDetails::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg codeDetails: CodeDetails)
}