package com.example.instagram.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.instagram.R
import com.example.instagram.destinations.HomeScreenDestination
import com.example.instagram.destinations.RegisterScreenDestination
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.viewmodels.SplashScreenViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun SplashScreenWidget(
    navigator: DestinationsNavigator,
    splashScreenViewModel: SplashScreenViewModel
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundColor
    )
    if (splashScreenViewModel.myPreference.getToken() != "") {
        navigator.navigate(HomeScreenDestination)
    } else {
        navigator.navigate(RegisterScreenDestination)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "logo",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}