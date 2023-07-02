package org.k.barcode.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.k.barcode.decoder.DecodeMode

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "decoderEnable") var decoderEnable: Boolean,
    @ColumnInfo(name = "decoderVibrate") var decoderVibrate: Boolean,
    @ColumnInfo(name = "decoderSound") var decoderSound: Boolean,
    @ColumnInfo(name = "decoderMode") var decoderMode: DecodeMode,
    @ColumnInfo(name = "decoderCharset") var decoderCharset: String,
    @ColumnInfo(name = "decoderPrefix") var decoderPrefix: String,
    @ColumnInfo(name = "decodeSuffix") var decodeSuffix: String,
    @ColumnInfo(name = "continuousDecode") var continuousDecode: Boolean,
    @ColumnInfo(name = "continuousDecodeInterval") var continuousDecodeInterval: Int,
    @ColumnInfo(name = "attachKeycode") var attachKeycode: Int,
    @ColumnInfo(name = "decoderFilterCharacters") var decoderFilterCharacters: String,
    @ColumnInfo(name = "releaseDecode") var releaseDecode: Boolean,
    @ColumnInfo(name = "decoderLight") var decoderLight: Boolean
)
