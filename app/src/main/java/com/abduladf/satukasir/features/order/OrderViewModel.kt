package com.abduladf.satukasir.features.order

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())

    val uiState = _uiState.asStateFlow()

}