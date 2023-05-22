package com.example.instagram.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.instagram.api.Resource
import com.example.instagram.destinations.HomeScreenDestination
import com.example.instagram.other.currentUser
import com.example.instagram.other.imageBitmapTemp
import com.example.instagram.ui.theme.DividerColor
import com.example.instagram.ui.theme.ExplorerColor
import com.example.instagram.ui.theme.IconsColorBottom
import com.example.instagram.viewmodels.PostViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@Composable
fun NewPostWidget(navigator: DestinationsNavigator, postViewModel: PostViewModel) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Black)
    val (caption, setCaption) = remember { mutableStateOf("") }
    val getPostCreatedResult by postViewModel.getPostCreatedResult.collectAsState()
    val loadingState by rememberUpdatedState(newValue = false)

    if (getPostCreatedResult is Resource.Loading) {
        loadingState.not()
    } else {
        when (getPostCreatedResult) {
            is Resource.Success -> {
                loadingState.not()
                navigator.navigate(HomeScreenDestination)
            }

            is Resource.Error -> {
                Timber.e(getPostCreatedResult.message)
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToolBar(
            navigator = navigator,
            caption = caption,
            postViewModel = postViewModel,
            loadingState = loadingState
        )
        InputView(caption, setCaption)
        Divider(Modifier.background(DividerColor))
        Spacer(modifier = Modifier.height(13.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
                .clickable {

                }
        ) {
            Text(text = "Tag people")
        }
        Spacer(modifier = Modifier.height(13.dp))
        Divider(Modifier.background(DividerColor))
        Spacer(modifier = Modifier.height(13.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
                .clickable {

                }
        ) {
            Text(text = "Add Location")
        }
        Spacer(modifier = Modifier.height(13.dp))
        Divider(Modifier.background(DividerColor))
    }
}

@Composable
fun ToolBar(
    navigator: DestinationsNavigator,
    caption: String,
    postViewModel: PostViewModel,
    loadingState: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = { navigator.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "New Post",
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (loadingState) {
            CircularProgressIndicator()
        } else {
            IconButton(
                onClick = {
                    postViewModel.createNewPost(listOf(imageBitmapTemp!!), caption)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "go forward",
                    tint = Color.Blue,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

    }

}

@Composable
fun InputView(caption: String, setCaption: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = 30.dp, horizontal = 20.dp)
        ) {
            val (userImage, textView, postImage) = createRefs()
            AsyncImage(
                model = currentUser?.avatar,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp, 40.dp)
                    .constrainAs(userImage) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
            TextField(
                value = caption,
                onValueChange = setCaption,
                modifier = Modifier
                    .constrainAs(textView) {
                        start.linkTo(userImage.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth(0.7f),
                placeholder = {
                    Text(
                        text = "What is in your mind ... ?",
                        fontSize = 10.sp,
                        color = IconsColorBottom,
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    disabledTextColor = ExplorerColor,
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = ExplorerColor
                ),
            )
            Image(
                bitmap = imageBitmapTemp!!,
                contentDescription = "post image",
                modifier = Modifier
                    .size(height = 70.dp, width = 50.dp)
                    .constrainAs(postImage) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.Crop
            )
        }
    }
}