package org.k.barcode.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codeDetails")
data class CodeDetails(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "enable") var enable: Boolean,
    @ColumnInfo(name = "transmitCheckDigit") var transmitCheckDigit: Boolean,
    @ColumnInfo(name = "checkDigit") var checkDigit: Boolean,
    @ColumnInfo(name = "supplemental2") var supplemental2: Boolean,
    @ColumnInfo(name = "supplemental5") var supplemental5: Boolean,
    @ColumnInfo(name = "upcPreamble") var upcPreamble: Int,
    @ColumnInfo(name = "startStopCharacters") var startStopCharacters: Boolean,
    @ColumnInfo(name = "fullAscii") var fullAscii: Boolean,
    @ColumnInfo(name = "minLength") var minLength: Int,
    @ColumnInfo(name = "maxLength") var maxLength: Int,
    @ColumnInfo(name = "algorithm") var algorithm: Int,
    @ColumnInfo(name = "supportDetails") var supportDetails: Boolean
)
