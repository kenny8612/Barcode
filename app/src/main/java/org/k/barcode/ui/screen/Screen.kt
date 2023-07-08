package org.k.barcode.ui.screen

sealed class Screen(val route: String) {
    object ScanTest : Screen("scan_test")
    object AppSettings : Screen("app_settings")
    object CodeSettings : Screen("code_settings")
    object CodeDetail : Screen("code_detail/{uid}") {
        fun codeUid(uid: Int): String =
            this.route.replace(oldValue = "{uid}", newValue = uid.toString())
    }
    object BroadcastSettings: Screen("broadcast_settings")
}