package org.k.barcode.model

data class BarcodeInfo(
    val sourceData: ByteArray? = null,
    val aim: String? = null,
    var decodeTime: Long = 0,
    var formatData: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BarcodeInfo) return false

        if (sourceData != null) {
            if (other.sourceData == null) return false
            if (!sourceData.contentEquals(other.sourceData)) return false
        } else if (other.sourceData != null) return false
        if (aim != other.aim) return false
        if (decodeTime != other.decodeTime) return false
        if (formatData != other.formatData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sourceData?.contentHashCode() ?: 0
        result = 31 * result + (aim?.hashCode() ?: 0)
        result = 31 * result + decodeTime.hashCode()
        result = 31 * result + (formatData?.hashCode() ?: 0)
        return result
    }
}
