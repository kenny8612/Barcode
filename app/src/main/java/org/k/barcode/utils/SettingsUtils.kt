package org.k.barcode.utils

import android.content.Context
import org.k.barcode.R
import org.k.barcode.decoder.DecodeMode

object SettingsUtils {
    fun formatMode(context: Context, modeString: String): DecodeMode {
        val modeList = context.resources.getStringArray(R.array.decoder_mode_entries)
        for ((index, value) in modeList.withIndex()) {
            if (value == modeString) {
                val mode = context.resources.getStringArray(R.array.scan_mode_values)[index]
                return DecodeMode.values()[mode.toInt()]
            }
        }
        return DecodeMode.Focus
    }

    fun formatKeycode(context: Context, keyString: String): Int {
        val attachKeyList = context.resources.getStringArray(R.array.attach_keycode_entries)
        for ((index, value) in attachKeyList.withIndex()) {
            if (value == keyString) {
                val keycode =
                    context.resources.getStringArray(R.array.scan_additional_key_values)[index]
                return keycode.toInt()
            }
        }
        return 0
    }

    fun keyCodeToIndex(context: Context, keycode: Int): Int {
        val keycodeList = context.resources.getStringArray(R.array.scan_additional_key_values)
        for ((index, value) in keycodeList.withIndex()) {
            if (value.toInt() == keycode)
                return index
        }
        return 0
    }
}