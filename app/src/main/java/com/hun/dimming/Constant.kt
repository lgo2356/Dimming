package com.hun.dimming

import java.util.*

class Constant {

    companion object {
        // Bluetooth
        const val REQUEST_ENABLE_BLUETOOTH = 1000
        const val REQUEST_PERMISSIONS = 2000

        // View pager
        const val PAGE_MAX_COUNT = 5

        // IO
        const val MESSAGE_READ: Int = 0
        const val MESSAGE_WRITE: Int = 1
        const val MESSAGE_TOAST: Int = 2
        const val MESSAGE_PROGRESS: Int = 3
        const val MESSAGE_CONNECTED: Int = 4
        const val MESSAGE_DEVICE: Int = 5
        const val MESSAGE_ERROR: Int = 6

        val UUID_SERIAL_PORT: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        // Color code
        const val COLOR_CODE_RED: Int = 0
        const val COLOR_CODE_VERMILION: Int = 1
        const val COLOR_CODE_ORANGE: Int = 2
        const val COLOR_CODE_AMBER: Int = 3
        const val COLOR_CODE_YELLOW: Int = 4
        const val COLOR_CODE_LIME_GREEN: Int = 5
        const val COLOR_CODE_CHARTREUSE_GREEN: Int = 6
        const val COLOR_CODE_GREEN: Int = 7
        const val COLOR_CODE_TURQUOISE: Int = 8
        const val COLOR_CODE_CYAN: Int = 9
        const val COLOR_CODE_CERULEAN: Int = 10
        const val COLOR_CODE_AZURE: Int = 11
        const val COLOR_CODE_SAPPHIRE_BLUE: Int = 12
        const val COLOR_CODE_BLUE: Int = 13
        const val COLOR_CODE_INDIGO: Int = 14
        const val COLOR_CODE_VIOLET: Int = 15
        const val COLOR_CODE_MULBERRY: Int = 16
        const val COLOR_CODE_MAGENTA: Int = 17
        const val COLOR_CODE_FUCHSIA: Int = 18
        const val COLOR_CODE_ROSE: Int = 19
        const val COLOR_CODE_CRIMSON: Int = 20
        const val COLOR_CODE_3000K: Int = 21
        const val COLOR_CODE_4500K: Int = 22
        const val COLOR_CODE_6000K: Int = 23
        const val COLOR_CODE_BLACK: Int = 24
    }
}
