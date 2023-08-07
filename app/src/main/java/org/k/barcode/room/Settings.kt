package org.k.barcode.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.k.barcode.decoder.DecodeMode
import org.k.barcode.decoder.LightLevel

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val uid: Int = 1,
    @ColumnInfo(name = "decoderEnable") var decoderEnable: Boolean = true,
    @ColumnInfo(name = "decoderVibrate") var decoderVibrate: Boolean = true,
    @ColumnInfo(name = "decoderSound") var decoderSound: Boolean = true,
    @ColumnInfo(name = "decoderMode") var decoderMode: DecodeMode = DecodeMode.InputBox,
    @ColumnInfo(name = "decoderCharset") var decoderCharset: String = "AUTO",
    @ColumnInfo(name = "decoderPrefix") var decoderPrefix: String = "",
    @ColumnInfo(name = "decodeSuffix") var decodeSuffix: String = "",
    @ColumnInfo(name = "continuousDecode") var continuousDecode: Boolean = false,
    @ColumnInfo(name = "continuousDecodeInterval") var continuousDecodeInterval: Int = 200,
    @ColumnInfo(name = "attachKeycode") var attachKeycode: Int = 0,
    @ColumnInfo(name = "decoderFilterCharacters") var decoderFilterCharacters: String = "",
    @ColumnInfo(name = "releaseDecode") var releaseDecode: Boolean = false,
    @ColumnInfo(name = "decoderLight") var decoderLight: Boolean = true,
    @ColumnInfo(name = "disableScanKey") var disableScanKey: Boolean = false,
    @ColumnInfo(name = "decoderLightLevel") var lightLevel: LightLevel = LightLevel.Medium,
    @ColumnInfo(name = "broadcastStartDecode") var broadcastStartDecode: String = "",
    @ColumnInfo(name = "broadcastStopDecode") var broadcastStopDecode: String = "",
    @ColumnInfo(name = "broadcastDecodeData") var broadcastDecodeData: String = "",
    @ColumnInfo(name = "broadcastDecodeDataByte") var broadcastDecodeDataByte: String = "",
    @ColumnInfo(name = "broadcastDecodeDataString") var broadcastDecodeDataString: String = ""
)
