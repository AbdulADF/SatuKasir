package com.abduladf.satukasir.features.sandbox

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abduladf.satukasir.SandboxViewModel
import com.abduladf.satukasir.ui.components.Screen

@Composable
fun SandboxScreen(viewModel: SandboxViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Screen {
        Column {
            TextField(value = firstName, onValueChange = { firstName = it })
            TextField(value = lastName, onValueChange = { lastName = it })

            Button(
                onClick = {
                    viewModel.printReceipt(context, firstName, lastName)
                }
            ) {
                Text("Print")
            }
        }
    }
}