package org.k.barcode.utils

import android.view.KeyEvent
import org.k.barcode.model.BarcodeInfo
import org.k.barcode.model.KeyEventEx

object BarcodeUtils {
    fun BarcodeInfo.toKeyEvents(): MutableList<KeyEventEx> {
        val keyList: MutableList<KeyEventEx> = ArrayList()
        for (b in this.sourceData) {
            when (b.toInt()) {
                in 65..90 -> {
                    keyList.add(KeyEventEx(b.toInt() - 36, true))
                }

                in 97..122 -> {
                    keyList.add(KeyEventEx(b.toInt() - 68))
                }

                in 48..57 -> {
                    keyList.add(KeyEventEx(b.toInt() - 41))
                }

                32 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SPACE))
                }

                33 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_1, true))
                }

                34 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_APOSTROPHE, true))
                }

                35 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_POUND))
                }

                36 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_4, true))
                }

                37 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_5, true))
                }

                38 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_7, true))
                }

                39 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_APOSTROPHE))
                }

                40 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_9, true))
                }

                41 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_0, true))
                }

                42 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_STAR))
                }

                43 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_PLUS))
                }

                44 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_COMMA))
                }

                45 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_MINUS))
                }

                46 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_PERIOD))
                }

                47 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SLASH))
                }

                58 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SEMICOLON, true))
                }

                59 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SEMICOLON))
                }

                60 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_COMMA, true))
                }

                61 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_EQUALS))
                }

                62 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_PERIOD, true))
                }

                63 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_SLASH, true))
                }

                64 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_AT))
                }

                91 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_LEFT_BRACKET))
                }

                92 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_BACKSLASH))
                }

                93 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_RIGHT_BRACKET))
                }

                94 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_6, true))
                }

                95 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_MINUS, true))
                }

                96 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_GRAVE))
                }

                123 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_LEFT_BRACKET, true))
                }

                124 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_BACKSLASH, true))
                }

                125 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_RIGHT_BRACKET, true))
                }

                126 -> {
                    keyList.add(KeyEventEx(KeyEvent.KEYCODE_GRAVE, true))
                }
            }
        }
        return keyList
    }
}