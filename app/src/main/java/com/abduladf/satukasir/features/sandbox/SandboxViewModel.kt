package com.abduladf.satukasir

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abduladf.satukasir.features.sandbox.SandboxUiState
import com.abduladf.satukasir.utils.ThermalPrinterHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SandboxViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SandboxUiState())
    val uiState = _uiState.asStateFlow()

    // Tambahkan parameter context: Context di sini
    fun printReceipt(context: Context, firstName: String, lastName: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {

            val receiptTemplate = """
                [C]Test Centering 1
                [C]Test Centering 2
            """.trimIndent()

            // Teruskan variabel context ke helper printer kamu
            val result = ThermalPrinterHelper.printReceiptWithLogo(context, receiptTemplate)
//            val result = ThermalPrinterHelper.printViaBluetooth( receiptTemplate)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                println("Log info print gagal: ${exception.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}