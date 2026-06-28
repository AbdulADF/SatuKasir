package com.abduladf.satukasir.features.settings.menuEditor

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abduladf.satukasir.ui.components.Screen

@Composable
fun MenuEditorScreen(
    viewModel: MenuEditorViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    Screen {
        Text("Menu Editor")
    }
}