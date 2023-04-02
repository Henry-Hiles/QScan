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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.henryhiles.qscan.LabelledCheckBox
import com.henryhiles.qscan.R
import com.henryhiles.qscan.utils.Helpers.isURL

@Composable
fun ScannedAlert(
    onDismiss: () -> Unit,
    code: String,
    doNotAsk: Boolean,
    onChangeDoNotAsk: (Boolean) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var tempDoNotAsk by remember { mutableStateOf(doNotAsk) }

    AlertDialog(
        title = {
            Text(
                text = stringResource(id = R.string.code_scanned),
            )
        }, onDismissRequest = onDismiss, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.action_cancel),
                )
            }
        },
        text = {
            Column {
                SelectionContainer {
                    Text(
                        text = stringResource(id = if (isURL(code)) R.string.code_description_url else R.string.code_description_text).format(
                            code
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LabelledCheckBox(
                    checked = tempDoNotAsk,
                    onCheckedChange = { tempDoNotAsk = it },
                    label = stringResource(id = R.string.setting_do_not_ask)
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
                        text = stringResource(id = R.string.action_open_url),
                    )
                }
        })
}