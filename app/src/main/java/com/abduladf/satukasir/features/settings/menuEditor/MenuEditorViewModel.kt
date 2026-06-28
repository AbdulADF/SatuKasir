package com.abduladf.satukasir.features.settings.menuEditor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuEditorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MenuEditorUiState())

    val uiState = _uiState.asStateFlow()

}