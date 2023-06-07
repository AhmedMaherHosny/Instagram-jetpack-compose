package com.example.instagram.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
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
import com.example.instagram.destinations.LoginScreenDestination
import com.example.instagram.models.RegisterData
import com.example.instagram.other.CustomSnackBarError
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.ui.theme.FabColor
import com.example.instagram.viewmodels.RegisterViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterScreenWidget(navigator: DestinationsNavigator, registerViewModel: RegisterViewModel) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundColor
    )
    if (registerViewModel.myPreference.getToken() != "") {
        navigator.navigate(HomeScreenDestination)
    }
    val (username, setUsername) = remember { mutableStateOf("") }
    val (email, setEmail) = remember { mutableStateOf("") }
    val (firstName, setFirstName) = remember { mutableStateOf("") }
    val (lastName, setLastName) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (phoneNumber, setPhoneNumber) = remember { mutableStateOf("") }
    val snackBarState = remember { SnackbarHostState() }
    val error = remember { mutableStateOf("") }
    val registerResult by registerViewModel.registerResult.collectAsState()

    Scaffold(
        floatingActionButton = {
            IconButton(
                modifier = Modifier
                    .background(FabColor, shape = CircleShape)
                    .padding(vertical = 0.dp, horizontal = 30.dp),
                onClick = {
                    navigator.navigate(LoginScreenDestination)
                },
                interactionSource = registerViewModel.noRippleInteractionSource
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Log in", color = Color.White)
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Add",
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        LaunchedEffect(registerViewModel.validationError) {
            registerViewModel.validationError.collect {
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
            Text(text = "Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = username,
                onValueChange = setUsername,
                label = { Text(text = "Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle(fontSize = 16.sp)
            )
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
                value = firstName,
                onValueChange = setFirstName,
                label = { Text(text = "First Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = setLastName,
                label = { Text(text = "Last Name") },
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
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = setPhoneNumber,
                label = { Text(text = "Phone Number with Country Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Phone
                ),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    val registerData = RegisterData(
                        username = username.trim(),
                        email = email.trim(),
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        password = password.trim(),
                        phoneNumber = phoneNumber.trim()
                    )
                    registerViewModel.registerData = registerData
                    registerViewModel.register()
                },
                interactionSource = registerViewModel.noRippleInteractionSource,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    backgroundColor = Color.White
                ),
                modifier = Modifier.width(150.dp)
            ) {
                Text(text = "Sign Up")
            }
            if (registerResult is Resource.Loading) {
                CircularProgressIndicator()
            } else {
                when (registerResult) {
                    is Resource.Success -> {
                        Text(registerResult.data.toString())
                    }

                    is Resource.Error -> {
                        Text(registerResult.message ?: "Unknown error occurred")
                    }

                    else -> {}
                }
            }
        }
    }
}



