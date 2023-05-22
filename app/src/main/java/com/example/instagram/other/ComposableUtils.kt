package com.example.instagram.other

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun CustomSnackBarError(
    error: String,
) {
    Snackbar(
        elevation = 0.dp,
        backgroundColor = Color.Red,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Default.Warning, contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = 3.dp))
            Text(text = error)
        }
    }
}

@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(42.dp)
                .height(42.dp)
                .padding(8.dp),
            strokeWidth = 5.dp,
        )
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MediaPermissions(comp: @Composable () -> Unit) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA,
        )
    )
    var showDialog by remember { mutableStateOf(true) }
    when {
        permissionsState.allPermissionsGranted -> {
            comp()
        }

        permissionsState.shouldShowRationale -> {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Permission Request") },
                    text = { Text("The requested permissions are required for this feature to work properly. Please grant the permissions.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        ) {
                            Text("Grant Permissions")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )

            }
        }

        else -> {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Permission Request") },
                    text = { Text("Media permissions are required for this feature to work properly. Please grant the permissions.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        ) {
                            Text("Grant Permissions")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )

            }
        }
    }
}

@Composable
fun tempAlert(imageBitmap: String) {
    AlertDialog(
        onDismissRequest = {  },
        title = { Text("Permission Request") },
        text = { Text(imageBitmap) },
        confirmButton = {
            Button(
                onClick = {

                }
            ) {
                Text("Grant Permissions")
            }
        },
        dismissButton = {
            Button(
                onClick = {  }
            ) {
                Text("Cancel")
            }
        }
    )
}
