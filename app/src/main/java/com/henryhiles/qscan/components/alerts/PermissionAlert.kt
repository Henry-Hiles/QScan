package com.henryhiles.qscan.components.alerts

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.henryhiles.qscan.R

@Composable
fun PermissionAlert(textToShow: String, title: String, onGrantRequest: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = onGrantRequest) {
                Text(
                    text = stringResource(id = R.string.action_permission_grant),
                )
            }
        },
        text = {
            SelectionContainer {
                Text(
                    text = textToShow,
                )
            }
        }
    )
}