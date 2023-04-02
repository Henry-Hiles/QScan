package com.henryhiles.qscan.components.alerts

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PermissionAlert(textToShow: String, permissionName: String, onGrantRequest: () -> Unit) {
    AlertDialog(
        title = {
            Text(
                text = "${permissionName.replaceFirstChar { it.uppercaseChar() }} permission required",
            )
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = onGrantRequest) {
                Text(
                    text = "Grant Permission",
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


//Surface(
//shape = MaterialTheme.shapes.large
//) {
//    Column(modifier = Modifier.padding(16.dp)) {


//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.End
//        ) {

//        }
//    }
//}