package com.abduladf.satukasir.utils

sealed class PrinterStatus {
    object Checking : PrinterStatus()
    object Connected : PrinterStatus()
    object Disconnected : PrinterStatus()

    data class Error(val theMessage: String): PrinterStatus()
}