package com.example.instagram.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.instagram.R
import com.example.instagram.api.Resource
import com.example.instagram.destinations.NewProfileSelectScreenDestination
import com.example.instagram.destinations.ProfileScreenDestination
import com.example.instagram.models.UserX
import com.example.instagram.other.CustomSnackBarError
import com.example.instagram.other.currentUser
import com.example.instagram.other.imageBitmapTempProfile
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.ui.theme.FabColor
import com.example.instagram.ui.theme.UserStoryBorder
import com.example.instagram.viewmodels.EditProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun EditProfileWidget(
    navigator: DestinationsNavigator,
    editProfileViewModel: EditProfileViewModel
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = BackgroundColor)
    val (username, setUsername) = remember { mutableStateOf(currentUser?.username!!) }
    val snackBarState = remember { SnackbarHostState() }
    val error = remember { mutableStateOf("") }
    LaunchedEffect(editProfileViewModel.validationError) {
        editProfileViewModel.validationError.collect {
            error.value = it
            snackBarState.showSnackbar(it)
        }
    }
    SnackbarHost(hostState = snackBarState, modifier = Modifier) {
        CustomSnackBarError(error = error.value)
    }
    val updateResult by editProfileViewModel.updateResult.collectAsState()
    if (updateResult is Resource.Loading) {
        CircularProgressIndicator()
    } else {
        when (updateResult) {
            is Resource.Success -> {
                currentUser = updateResult.data?.user
                imageBitmapTempProfile = null
                navigator.navigate(ProfileScreenDestination(currentUser?._id!!))
            }

            is Resource.Error -> {}
            else -> {}
        }
    }
    BackHandler() {
        imageBitmapTempProfile = null
        navigator.popBackStack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(70.dp),
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageUpdate(Modifier, currentUser = currentUser, navigator)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = username,
                onValueChange = setUsername,
                label = { Text(text = "Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                ),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp),
                onClick = {
                    editProfileViewModel.update(username)
                },
                interactionSource = editProfileViewModel.noRippleInteractionSource,
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    backgroundColor = Color.Transparent
                ),
            ) {
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp)
                        .background(FabColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Update", color = Color.White)
                }
            }

        }
    }
}

@Composable
fun ImageUpdate(modifier: Modifier, currentUser: UserX?, navigator: DestinationsNavigator) {
    Box(
        modifier = modifier
    ) {
        if (imageBitmapTempProfile == null) {
            Image(
                painter = rememberAsyncImagePainter(model = currentUser?.avatar),
                contentDescription = null,
                modifier = Modifier
                    .padding(3.dp)
                    .size(125.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                bitmap = imageBitmapTempProfile!!,
                contentDescription = null,
                modifier = Modifier
                    .padding(3.dp)
                    .size(125.dp)
                    .clip(CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = 35.dp, x = 3.dp)
                .clickable { navigator.navigate(NewProfileSelectScreenDestination) }
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(width = 2.dp, color = BackgroundColor, shape = CircleShape)
                    .padding(2.dp)
                    .size(30.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Icon Add",
                    tint = BackgroundColor,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(brush = UserStoryBorder)
                        .padding(6.dp)
                        .fillMaxSize()
                )
            }

        }
    }

}
