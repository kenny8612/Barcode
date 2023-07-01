package org.k.barcode.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.k.barcode.model.CodeDetails

@Dao
interface CodeDetailDDao {
    @Query("SELECT * FROM codeDetails WHERE type IS (:type)")
    fun getCodesFlow(type: Int): Flow<List<CodeDetails>>

    @Query("SELECT * FROM codeDetails WHERE name IS (:name)")
    suspend fun getCodeByName(name: String): CodeDetails

    @Update(entity = CodeDetails::class)
    suspend fun update(codeDetails: CodeDetails)
}