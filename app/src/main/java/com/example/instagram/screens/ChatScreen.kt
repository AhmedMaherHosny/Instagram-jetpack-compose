package com.example.instagram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.instagram.R
import com.example.instagram.api.Resource
import com.example.instagram.destinations.ActualChatScreenDestination
import com.example.instagram.models.Chat
import com.example.instagram.other.currentUser
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.ui.theme.IconsColor
import com.example.instagram.viewmodels.ChatScreenViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ChatScreenWidget(navigator: DestinationsNavigator, chatScreenViewModel: ChatScreenViewModel) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundColor
    )

    val chatsResponse by chatScreenViewModel.getChatsResult.collectAsState()

    if (chatsResponse is Resource.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        when (chatsResponse) {
            is Resource.Success -> {
                val chats = chatsResponse.data?.chats!!
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(BackgroundColor)
                        .padding(vertical = 15.dp)
                ) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 17.dp, end = 15.dp, top = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "back",
                                modifier = Modifier
                                    .clickable(
                                        indication = null,
                                        interactionSource = chatScreenViewModel.noRippleInteractionSource
                                    ) { navigator.popBackStack() }
                                    .size(30.dp)
                            )
                            Text(
                                text = "Inbox",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(top = 1.dp)
                            )
                        }
                    }
                    items(chats) {
                        ChatItem(
                            chat = it!!,
                            navigator = navigator,
                            chatScreenViewModel = chatScreenViewModel
                        )
                    }
                }
            }

            is Resource.Error -> {}
            else -> {}
        }
    }
}

@Composable
fun ChatItem(
    chat: Chat,
    navigator: DestinationsNavigator,
    chatScreenViewModel: ChatScreenViewModel
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                indication = null,
                interactionSource = chatScreenViewModel.noRippleInteractionSource
            ) {
                navigator.navigate(
                    ActualChatScreenDestination(
                        chatId = chat._id,
                        receiverId = chat.members.first()._id!!
                    )
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .padding(end = 10.dp)
        ) {
            AsyncImage(
                model = chat.members.first().avatar,
                contentDescription = null,
                modifier = Modifier

                    .padding(3.dp)
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            brush = if (chat.members.first().isOnline == true) Brush.verticalGradient(
                                colors = listOf(Color.Green, Color.Green)
                            ) else SolidColor(Color.Transparent)
                        )
                        .border(
                            width = if (chat.members.first().isOnline == true) 2.dp else 0.dp,
                            color = if (chat.members.first().isOnline == true) BackgroundColor else Color.Transparent,
                            shape = CircleShape
                        )
                        .padding(2.dp)
                        .size(12.dp)
                        .align(Alignment.BottomCenter)
                ) {}
            }
        }
        Column(
            Modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chat.members.first().username!!,
                    fontSize = 13.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    painter = painterResource(id = R.drawable.share_ic),
                    contentDescription = "send message",
                    tint = IconsColor
                )
            }
            chat.latestMessage?.let {
                Text(
                    text = if (chat.latestMessage._id == currentUser?._id)
                        "Me : ${chat.latestMessage.content}"
                    else
                        chat.latestMessage.content,
                    fontSize = 12.sp,
                    color = IconsColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}