package org.k.barcode.ui

sealed class Screen(val route: String) {
    object ScanTest : Screen("scan_test_screen")
    object AppSettings : Screen("app_settings_screen")
    object CodeSettings : Screen("code_settings_screen/{index}") {
        fun setIndex(index: Int): String =
            this.route.replace(oldValue = "{index}", newValue = index.toString())
    }
    object CodeDetail : Screen("code_detail/{name}") {
        fun codeName(name: String): String =
            this.route.replace(oldValue = "{name}", newValue = name)
    }
}