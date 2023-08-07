package org.k.barcode.model

import android.view.KeyEvent

data class KeyInfo(val keycode:Int = 0, val action:Int = KeyEvent.ACTION_UP)
