package org.k.barcode.model

data class BarcodeInfo(
    val sourceData: ByteArray,
    val aim: String,
    val decodeTime: Long,
    var formatData:String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BarcodeInfo) return false

        if (!sourceData.contentEquals(other.sourceData)) return false
        if (aim != other.aim) return false
        if (decodeTime != other.decodeTime) return false
        if (formatData != other.formatData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sourceData.contentHashCode()
        result = 31 * result + aim.hashCode()
        result = 31 * result + decodeTime.hashCode()
        result = 31 * result + (formatData?.hashCode() ?: 0)
        return result
    }
}
