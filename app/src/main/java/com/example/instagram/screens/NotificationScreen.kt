package com.example.instagram.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.instagram.ui.theme.BackgroundColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun NotificationScreenWidget() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundColor
    )
    Text(text = "notify")
}