package org.k.barcode.ui.screen

sealed class Screen(val route: String) {
    object ScanTest : Screen("scan_test_screen")
    object AppSettings : Screen("app_settings_screen")
    object CodeSettings : Screen("code_settings_screen/{index}") {
        fun setIndex(index: Int): String =
            this.route.replace(oldValue = "{index}", newValue = index.toString())
    }
    object CodeDetail : Screen("code_detail/{uid}") {
        fun codeUid(uid: Int): String =
            this.route.replace(oldValue = "{uid}", newValue = uid.toString())
    }
}