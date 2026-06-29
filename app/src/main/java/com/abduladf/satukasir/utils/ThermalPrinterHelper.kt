package com.abduladf.satukasir.utils

import android.content.Context
import android.util.DisplayMetrics
import com.abduladf.satukasir.R
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg

object ThermalPrinterHelper {

    fun checkPrinterConnection(): PrinterStatus {
        return try {
            val connection = BluetoothPrintersConnections.selectFirstPaired()
            if (connection == null) {
                PrinterStatus.Disconnected
            } else {
                if (!connection.isConnected) {
                    connection.connect()
                }

                val isSuccess = connection.isConnected
                connection.disconnect()

                if (isSuccess) PrinterStatus.Connected else PrinterStatus.Disconnected
            }
        } catch (e: Exception) {
            PrinterStatus.Error(e.message ?: "Gagal memeriksa printer")
        }
    }

    fun printViaBluetooth(formattedText: String): Result<Boolean> {
        return try {
            val connection = BluetoothPrintersConnections.selectFirstPaired()
                ?: return Result.failure(Exception("Tidak ada thermal printer Bluetooth yang terhubung."))

            val printer = EscPosPrinter(connection, 216, 58f, 32)

            printer.printFormattedText(formattedText + "\n\n\n")

            Result.success(true)
        } catch (e: EscPosConnectionException) {
            Result.failure(Exception("Gagal menyambungkan ke printer: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun printReceiptWithLogo(context: Context, formattedTextWithoutLogo: String): Result<Boolean> {
        return try {
            val connection = BluetoothPrintersConnections.selectFirstPaired()
                ?: return Result.failure(Exception("Printer tidak ditemukan"))

            val printer = EscPosPrinter(connection, 216, 58f, 32)

            val logoHex = PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                context.resources.getDrawableForDensity(R.drawable.logo_mpok_noer, DisplayMetrics.DENSITY_MEDIUM, context.theme)
            )

            val fullReceipt = "[L]<img>$logoHex</img>\n" +
                    formattedTextWithoutLogo + "\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n"

            printer.printFormattedText(fullReceipt)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getDivider(char: String = "=", length: Int = 32): String {
        return "[C]${char.repeat(length)}"
    }
}