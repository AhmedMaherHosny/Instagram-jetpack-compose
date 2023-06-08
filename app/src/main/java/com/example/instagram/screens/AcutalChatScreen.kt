package com.example.instagram.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.instagram.R
import com.example.instagram.api.Resource
import com.example.instagram.destinations.ProfileScreenDestination
import com.example.instagram.other.MessageStatus
import com.example.instagram.other.ReceivedMessageRow
import com.example.instagram.other.SentMessageRow
import com.example.instagram.other.currentUser
import com.example.instagram.other.noRippleClickable
import com.example.instagram.models.UserX
import com.example.instagram.ui.theme.IconsColor
import com.example.instagram.ui.theme.SendMessage
import com.example.instagram.viewmodels.ActualChatScreenViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ActualChatScreenWidget(
    navigator: DestinationsNavigator,
    actualChatScreenViewModel: ActualChatScreenViewModel,
    chatId: String,
    receiverId: String
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.White
    )
    var isChatInputFocus by remember {
        mutableStateOf(false)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    var userData: UserX? = null
    val getChatHeaderResult by actualChatScreenViewModel.getChatHeaderResult.collectAsState()
    val getAllMessagesResult by actualChatScreenViewModel.getAllMessagesResult.collectAsState()
    val listOfMsgs = actualChatScreenViewModel.allMessages
    if (getChatHeaderResult is Resource.Success) userData = getChatHeaderResult.data?.user!!
    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = listOfMsgs.size)
    val messagesLoadedFirstTime = actualChatScreenViewModel.messagesLoadedFirstTime.value
    val messageInserted = actualChatScreenViewModel.messageInserted.value
    LaunchedEffect(messagesLoadedFirstTime, listOfMsgs, messageInserted) {
        if (listOfMsgs.isNotEmpty()) {
            scrollState.animateScrollToItem(
                index = listOfMsgs.size - 1
            )
        }
    }
    val imePaddingValues = PaddingValues()
    val imeBottomPadding = imePaddingValues.calculateBottomPadding().value.toInt()
    LaunchedEffect(key1 = imeBottomPadding) {
        if (listOfMsgs.isNotEmpty()) {
            scrollState.animateScrollToItem(
                index = listOfMsgs.size - 1
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color.White)
            .focusable()
            .wrapContentHeight()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { keyboardController?.hide() })
            }
    ) {
        ChatAppBar(navigator = navigator, user = userData)
        if (getAllMessagesResult is Resource.Loading) {
            CircularProgressIndicator(Modifier.padding(top = 50.dp))
        } else {
            when (getAllMessagesResult) {
                is Resource.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        state = scrollState
                    ) {
                        items(listOfMsgs) {
                            when (it.senderId == currentUser!!._id) {
                                true -> {
                                    SentMessageRow(
                                        text = it.content,
                                        messageTime = "hh:mm",
                                        messageStatus = if (it.isRead) MessageStatus.isRead else MessageStatus.isDelivered
                                    )
                                }

                                false -> {
                                    ReceivedMessageRow(
                                        text = it.content,
                                        opponentName = "testing here",
                                        quotedMessage = null,
                                        messageTime = "hh:mm",
                                    )
                                }
                            }
                        }
                    }

                }

                is Resource.Error -> {

                }

                else -> {}
            }
        }
        ChatInput(
            onMessageChange = {
                actualChatScreenViewModel.addMessage(chatId, it)
            },
            onFocusEvent = {
                isChatInputFocus = it
            }
        )
    }
}

@Composable
fun ChatAppBar(
    navigator: DestinationsNavigator,
    user: UserX?
) {
    user?.let {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                    modifier = Modifier
                        .noRippleClickable { navigator.popBackStack() }
                        .size(20.dp)
                )
                Spacer(modifier = Modifier.width(15.dp))
                AsyncImage(
                    model = user.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .noRippleClickable { navigator.navigate(ProfileScreenDestination(userId = user._id!!)) })
                Spacer(modifier = Modifier.width(15.dp))
                Column() {
                    Text(
                        text = user.username!!,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = if (user.isOnline == true) "Online" else user.lastSeen.toString(),
                        fontSize = 13.sp,
                        color = IconsColor
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.four_dots_ic),
                contentDescription = "back",
                modifier = Modifier
                    .noRippleClickable { }
                    .size(15.dp)
            )
        }
    }
}

@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    onMessageChange: (String) -> Unit,
    onFocusEvent: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var textEmpty by remember { mutableStateOf(input.text.trim().isEmpty()) }

    LaunchedEffect(input.text) {
        textEmpty = input.text.trim().isEmpty()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        TextField(
            modifier = Modifier
                .clip(shape = CircleShape)
                .weight(1f)
                .focusable(true),
            value = input,
            onValueChange = { input = it },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = "Message...")
            },
            leadingIcon = {
                IconButton(onClick = {
                    Toast.makeText(
                        context,
                        "Emoji Clicked.\n(Not Available)",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Icon(
                        painterResource(id = R.drawable.happy),
                        contentDescription = "Mood",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = {
                Row() {
                    IconButton(onClick = {
                        Toast.makeText(
                            context,
                            "Attach File Clicked.\n(Not Available)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.attachment),
                            contentDescription = "File",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = {
                        Toast.makeText(
                            context,
                            "Camera Clicked.\n(Not Available)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.camera),
                            contentDescription = "Camera",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

            }

        )

        IconButton(
            onClick = {
                if (!textEmpty) {
                    onMessageChange(input.text.trim())
                    input = TextFieldValue("")
                } else {
                    Toast.makeText(
                        context,
                        "Sound Recorder Clicked.\n(Not Available)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(SendMessage)
        ) {
            Icon(
                painter = if (textEmpty) painterResource(id = R.drawable.microphone) else painterResource(
                    id = R.drawable.send_btn
                ),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}
