package com.henryhiles.qscan.components.alerts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.henryhiles.qscan.LabelledCheckBox
import com.henryhiles.qscan.utils.Helpers.isURL

@Composable
fun ScannedAlert(onDismiss: () -> Unit, code: String, onChangeDoNotAsk: (Boolean) -> Unit) {
    val uriHandler = LocalUriHandler.current
    var tempDoNotAsk by remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(
                text = "QR Code Scanned",
            )
        }, onDismissRequest = onDismiss, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                )
            }
        },
        text = {
            Column {
                SelectionContainer {
                    Text(
                        text = if (isURL(code)) "This QR code will take you to $code, are you sure you want to go there?" else "The content of that QR Code is \"$code\"",
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LabelledCheckBox(
                    checked = tempDoNotAsk,
                    onCheckedChange = { tempDoNotAsk = it },
                    label = "Don't ask again"
                )
            }
        },
        confirmButton = {
            if (isURL(code))
                TextButton(onClick = {
                    uriHandler.openUri(code)
                    onChangeDoNotAsk(tempDoNotAsk)
                    onDismiss()
                }) {
                    Text(
                        text = "Open URL",
                    )
                }
        })
}