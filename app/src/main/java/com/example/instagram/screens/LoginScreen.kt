package com.example.instagram.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagram.api.Resource
import com.example.instagram.destinations.HomeScreenDestination
import com.example.instagram.models.LoginData
import com.example.instagram.other.CustomSnackBarError
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.viewmodels.LoginViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@Composable
fun LoginScreenWidget(navigator: DestinationsNavigator, loginViewModel: LoginViewModel) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundColor
    )
    if (loginViewModel.myPreference.getToken() != "") {
        navigator.navigate(HomeScreenDestination)
    }
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val snackBarState = remember { SnackbarHostState() }
    val error = remember { mutableStateOf("") }
    val loginResult by loginViewModel.loginResult.collectAsState()

    LaunchedEffect(loginViewModel.validationError) {
        loginViewModel.validationError.collect {
            error.value = it
            snackBarState.showSnackbar(it)
        }
    }
    SnackbarHost(hostState = snackBarState, modifier = Modifier) {
        CustomSnackBarError(error = error.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Text(text = "Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = setEmail,
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            textStyle = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = setPassword,
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            textStyle = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Forgot Password?",
            modifier = Modifier.clickable {

            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                val loginData = LoginData(
                    email,
                    password
                )
                loginViewModel.loginData = loginData
                loginViewModel.login()
            },
            interactionSource = loginViewModel.noRippleInteractionSource,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                backgroundColor = Color.White
            ),
            modifier = Modifier.width(150.dp)
        ) {
            Text(text = "Log In")
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (loginResult is Resource.Loading) {
            CircularProgressIndicator()
        } else {
            when (loginResult) {
                is Resource.Success -> {
                    navigator.navigate(HomeScreenDestination)
                }
                is Resource.Error -> {}
                else -> {}
            }
        }
    }


}