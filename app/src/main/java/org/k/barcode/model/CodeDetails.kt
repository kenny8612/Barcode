package org.k.barcode.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codeDetails")
data class CodeDetails(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "type") var type: Int = 0,
    @ColumnInfo(name = "enable") var enable: Boolean = false,
    @ColumnInfo(name = "transmitCheckDigit") var transmitCheckDigit: Boolean = false,
    @ColumnInfo(name = "checkDigit") var checkDigit: Boolean = false,
    @ColumnInfo(name = "supplemental2") var supplemental2: Boolean = false,
    @ColumnInfo(name = "supplemental5") var supplemental5: Boolean = false,
    @ColumnInfo(name = "upcPreamble") var upcPreamble: Int = 0,
    @ColumnInfo(name = "startStopCharacters") var startStopCharacters: Boolean = false,
    @ColumnInfo(name = "fullAscii") var fullAscii: Boolean = false,
    @ColumnInfo(name = "minLength") var minLength: Int = 0,
    @ColumnInfo(name = "maxLength") var maxLength: Int = 0,
    @ColumnInfo(name = "algorithm") var algorithm: Int = 0,
    @ColumnInfo(name = "supportDetails") var supportDetails: Boolean = false
)
