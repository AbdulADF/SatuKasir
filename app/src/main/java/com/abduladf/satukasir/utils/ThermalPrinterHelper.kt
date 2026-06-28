package com.abduladf.satukasir.utils

import android.content.Context
import android.util.DisplayMetrics
import com.abduladf.satukasir.R
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg

object ThermalPrinterHelper {

    /**
     * Fungsi umum untuk mencetak teks berformat ke printer Bluetooth pertama yang terhubung.
     * @param formattedText Teks yang sudah disusun menggunakan tag [C], [L], [R], dll.
     * @return Result berisi Boolean (true jika sukses) atau Exception jika gagal.
     */
    fun printViaBluetooth(formattedText: String): Result<Boolean> {
        return try {
            // 1. Ambil koneksi perangkat bluetooth yang sudah dipasangkan (paired)
            val connection = BluetoothPrintersConnections.selectFirstPaired()
                ?: return Result.failure(Exception("Tidak ada thermal printer Bluetooth yang terhubung."))

            // 2. Inisialisasi printer (DPI: 216, Lebar Kertas: 58mm, 32 Karakter per baris)
            val printer = EscPosPrinter(connection, 216, 58f, 32)

            // 3. Eksekusi cetak dan tambahkan line break di akhir agar kertas keluar sedikit
            printer.printFormattedText(formattedText + "\n\n\n")

            Result.success(true)
        } catch (e: EscPosConnectionException) {
            Result.failure(Exception("Gagal menyambungkan ke printer: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Tambahkan parameter context ke fungsi print yang sudah ada
    fun printReceiptWithLogo(context: Context, formattedTextWithoutLogo: String): Result<Boolean> {
        return try {
            val connection = BluetoothPrintersConnections.selectFirstPaired()
                ?: return Result.failure(Exception("Printer tidak ditemukan"))

            val printer = EscPosPrinter(connection, 216, 58f, 32)

            // Konversi gambar di sini secara internal
            val logoHex = PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                context.resources.getDrawableForDensity(R.drawable.logo_mpok_noer, DisplayMetrics.DENSITY_MEDIUM, context.theme)
            )

            // Gabungkan logo dengan sisa teks struknya
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

    /**
     * Helper opsional untuk mempercepat pembuatan garis pemisah struk
     */
    fun getDivider(char: String = "=", length: Int = 32): String {
        return "[C]${char.repeat(length)}"
    }
}