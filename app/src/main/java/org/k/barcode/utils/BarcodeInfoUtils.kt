package org.k.barcode.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import kotlinx.coroutines.delay
import org.k.barcode.Constant.ACTION_INPUT_INJECT
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.Settings
import java.nio.charset.Charset

object BarcodeInfoUtils {
    fun BarcodeInfo.transformData(settings: Settings): String? =
        sourceData?.let {
            formatData = String(it, 0, it.size)
            if (settings.decoderCharset != "AUTO") {
                try {
                    formatData = String(
                        it,
                        0,
                        it.size,
                        Charset.forName(settings.decoderCharset)
                    )
                } catch (_: Exception) {

                }
            }
            if (settings.decoderPrefix.isNotEmpty())
                formatData = settings.decoderPrefix + formatData
            if (settings.decodeSuffix.isNotEmpty())
                formatData += settings.decodeSuffix
            if (settings.decoderFilterCharacters.isNotEmpty())
                formatData = formatData?.replace(settings.decoderFilterCharacters, "")
            formatData
        }


    fun BarcodeInfo.injectInputBox(context: Context, settings: Settings) {
        val intent = Intent(ACTION_INPUT_INJECT)
        intent.putExtra("content", this.formatData)
        intent.putExtra("simulateKeyboard", settings.attachKeycode != 0)
        intent.putExtra("simulateKeyboard_keycode", settings.attachKeycode)
        intent.putExtra("deleteSurroundingText", false)
        context.sendBroadcast(intent)
    }

    fun BarcodeInfo.broadcast(context: Context, settings: Settings) {
        val intent = Intent(settings.broadcastDecodeData)
        intent.putExtra(settings.broadcastDecodeDataByte, this.sourceData)
        intent.putExtra(settings.broadcastDecodeDataString, this.formatData)
        intent.putExtra("decode_time", this.decodeTime)
        intent.putExtra("aim_string", this.aim)
        context.sendBroadcast(intent)
    }

    suspend fun BarcodeInfo.simulate(context: Context, settings: Settings) {
        val keyEventList = toKeyEventsArray()
        for (keyEvent in keyEventList) {
            simulateKeycode(context, keyEvent)
            delay(5)
        }
        if (settings.attachKeycode != 0)
            simulateKeycode(context, KeyEventEx(settings.attachKeycode))
    }

    fun BarcodeInfo.clipboard(context: Context) {
        formatData?.let {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(
                    "BarcodeText",
                    formatData
                )
            )
        }
    }

    private fun simulateKeycode(context: Context, keyEvent: KeyEventEx) {
        val intent = Intent(ACTION_INPUT_INJECT)
        intent.putExtra("content", "")
        intent.putExtra("simulateKeyboard", true)
        intent.putExtra("simulateKeyboard_keycode", keyEvent.keycode)
        intent.putExtra("shift", keyEvent.shift)
        context.sendBroadcast(intent)
    }

    private fun BarcodeInfo.toKeyEventsArray(): List<KeyEventEx> {
        val keyList = ArrayList<KeyEventEx>()
        sourceData?.let {
            for (byte in it) {
                when (byte.toInt()) {
                    in 65..90 -> keyList.add(KeyEventEx(byte.toInt() - 36, true))
                    in 97..122 -> keyList.add(KeyEventEx(byte.toInt() - 68))
                    in 48..57 -> keyList.add(KeyEventEx(byte.toInt() - 41))
                    32 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_SPACE))
                    33 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_1, true))
                    34 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_APOSTROPHE, true))
                    35 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_POUND))
                    36 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_4, true))
                    37 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_5, true))
                    38 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_7, true))
                    39 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_APOSTROPHE))
                    40 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_9, true))
                    41 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_0, true))
                    42 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_STAR))
                    43 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_PLUS))
                    44 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_COMMA))
                    45 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_MINUS))
                    46 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_PERIOD))
                    47 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_SLASH))
                    58 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_SEMICOLON, true))
                    59 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_SEMICOLON))
                    60 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_COMMA, true))
                    61 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_EQUALS))
                    62 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_PERIOD, true))
                    63 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_SLASH, true))
                    64 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_AT))
                    91 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_LEFT_BRACKET))
                    92 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_BACKSLASH))
                    93 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_RIGHT_BRACKET))
                    94 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_6, true))
                    95 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_MINUS, true))
                    96 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_GRAVE))
                    123 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_LEFT_BRACKET, true))
                    124 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_BACKSLASH, true))
                    125 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_RIGHT_BRACKET, true))
                    126 -> keyList.add(KeyEventEx(KeyEvent.KEYCODE_GRAVE, true))
                }
            }
        }
        return keyList
    }

    data class KeyEventEx(val keycode: Int, val shift: Boolean = false)
}