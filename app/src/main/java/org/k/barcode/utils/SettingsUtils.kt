package org.k.barcode.utils

import android.content.Context
import org.k.barcode.R
import org.k.barcode.decoder.DecodeMode
import org.k.barcode.decoder.LightLevel
import org.k.barcode.room.CodeDetails
import org.k.barcode.room.Settings
import org.k.barcode.ui.ShareViewModel

object SettingsUtils {
    fun formatMode(context: Context, modeString: String): DecodeMode {
        val modeList = context.resources.getStringArray(R.array.decoder_mode_entries)
        for ((index, value) in modeList.withIndex()) {
            if (value == modeString) {
                val mode = context.resources.getStringArray(R.array.decoder_mode_values)[index]
                return DecodeMode.values()[mode.toInt()]
            }
        }
        return DecodeMode.InputBox
    }

    fun formatLightLevel(context: Context, levelString: String): LightLevel {
        val levelList = context.resources.getStringArray(R.array.decoder_light_level_entries)
        for ((index, value) in levelList.withIndex()) {
            if (value == levelString) {
                val level = context.resources.getStringArray(R.array.decoder_light_level_values)[index]
                return LightLevel.values()[level.toInt()]
            }
        }
        return LightLevel.Medium
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

    fun Settings.update(viewModel: ShareViewModel) {
        viewModel.updateSettings(this)
    }

    fun CodeDetails.update(viewModel: ShareViewModel) {
        viewModel.updateCode(this)
    }
}