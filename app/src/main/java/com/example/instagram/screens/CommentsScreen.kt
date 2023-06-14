package com.example.instagram.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.instagram.R
import com.example.instagram.api.Resource
import com.example.instagram.destinations.ProfileScreenDestination
import com.example.instagram.models.CommentData
import com.example.instagram.models.CommentsItem
import com.example.instagram.other.LoadingItem
import com.example.instagram.other.currentUser
import com.example.instagram.other.extractDateTimeComponents
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.ui.theme.ExplorerColor
import com.example.instagram.ui.theme.IconsColor
import com.example.instagram.ui.theme.PostColor
import com.example.instagram.viewmodels.HomeViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber
import java.util.Calendar

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommentsScreenWidget(
    homeViewModel: HomeViewModel,
    navigator: DestinationsNavigator,
    usernameForAuthor: String,
    postId: String
) {
    LaunchedEffect(true) {
        homeViewModel.postId = postId
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val (comment, setComment) = remember { mutableStateOf("") }
    val getCommentAddedResult by homeViewModel.getCommentAddedResult.collectAsState()
    val listOfComments = homeViewModel.comments
    val scrollState = rememberLazyListState()
    val commentInserted = remember { mutableStateOf(false) }
    val onCommentAdded: (CommentData) -> Unit = { commentData ->
        homeViewModel.commentOnPost(postId, commentData)
    }
    LaunchedEffect(commentInserted.value) {
        if (commentInserted.value && listOfComments.isNotEmpty()) {
            scrollState.animateScrollToItem(listOfComments.lastIndex)
        }
        commentInserted.value = false
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        when (getCommentAddedResult) {
            is Resource.Success -> {
                commentInserted.value = true
            }

            is Resource.Error -> {
                Timber.e(getCommentAddedResult.message)
            }

            else -> {}
        }
        val (commentsList, commentBox, divider) = createRefs()
        Divider(
            color = Color.LightGray,
            thickness = 0.5.dp,
            modifier = Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            })
        Column(modifier = Modifier
            .constrainAs(commentsList) {
                top.linkTo(divider.bottom)
                start.linkTo(parent.start)
                //bottom.linkTo(commentBox.top)
            })
        {
            val lazyCommentsList = homeViewModel.commentsPager.collectAsLazyPagingItems()
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 25.dp, start = 15.dp, end = 15.dp, top = 20.dp),
                state = scrollState
            ) {
                items(lazyCommentsList) {
                    CommentItem(
                        it,
                        homeViewModel,
                        navigator
                    )
                }
//                items(
//                    count = lazyCommentsList.itemCount,
//                    key = {
//                        lazyCommentsList[it]?._id!!
//                    },
//                    itemContent = {
//                        CommentItem(comment = lazyCommentsList[it], homeViewModel = homeViewModel, navigator = navigator)
//                    }
//                )
                when (lazyCommentsList.loadState.append) {
                    is LoadState.NotLoading -> Unit
                    LoadState.Loading -> {
                        item { LoadingItem() }
                    }

                    is LoadState.Error -> {
                        item {

                        }
                    }
                }
                when (lazyCommentsList.loadState.refresh) {
                    is LoadState.NotLoading -> Unit
                    LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is LoadState.Error -> {
                        item {

                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .constrainAs(commentBox) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        ) {
            Divider(
                color = Color.LightGray,
                thickness = 0.5.dp,
                modifier = Modifier.offset(y = (6).dp)
            )
            Row(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = currentUser?.avatar),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )
                TextField(
                    value = comment,
                    onValueChange = setComment,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    placeholder = {
                        Text(
                            text = "Add a comment for ${usernameForAuthor}...",
                            fontSize = 10.sp,
                            color = Color.Black,
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
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions {
                        onCommentAdded(CommentData(comment))
                        setComment("")
                        keyboardController?.hide()
                    }
                )
                Button(
                    onClick = {
                        onCommentAdded(CommentData(comment))
                        setComment("")
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .padding(start = 8.dp),
                    enabled = comment.trim().isNotEmpty(),
                    elevation = null,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (comment.trim()
                                .isNotEmpty()
                        ) Color.Transparent else Color.Unspecified,
                        disabledBackgroundColor = Color.Transparent,
                        contentColor = Color(0xFF2691DC)
                    ),
                ) {
                    Text(text = "Post")
                }
            }

        }
    }

}

@Composable
fun CommentItem(
    comment: CommentsItem?,
    homeViewModel: HomeViewModel,
    navigator: DestinationsNavigator
) {
    val liked = remember { mutableStateOf(comment?.liked) }
    val likes = remember { mutableStateOf(comment?.likes?.size) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = comment?.postedBy?.avatar,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(30.dp)
                    .clickable {
                        navigator.navigate(ProfileScreenDestination(userId = comment?.postedBy?._id!!))
                    }
            )
            Column(modifier = Modifier.padding(start = 15.dp)) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = comment?.postedBy?.username!!,
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (currentUser?.isAdmin == true) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.protection),
                            contentDescription = "admin",
                            modifier = Modifier
                                .size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    val date = extractDateTimeComponents(comment.createdAt)
                    val amPm = date["amPm"] as Int
                    val amPmIndicator = if (amPm == Calendar.AM) "AM" else "PM"
                    val dateString =
                        "${date["year"]}/${date["month"]}/${date["day"]} ${date["hour"]}:${date["minute"]} $amPmIndicator"

                    Text(
                        text = dateString,
                        fontSize = 10.sp,
                        color = IconsColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = comment?.comment!!,
                    fontSize = 13.sp,
                    color = PostColor,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {
            Icon(
                painter = painterResource(id = R.drawable.heart_filled),
                contentDescription = "Share Icon",
                tint = if (liked.value!!) Color.Red else Color.Black,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(
                        indication = null,
                        interactionSource = homeViewModel.noRippleInteractionSource
                    ) {
                        homeViewModel.likeOrUnlikeComment(comment?._id!!)
                        when {
                            liked.value!! -> {
                                likes.value = likes.value!! - 1
                                liked.value = !liked.value!!
                            }

                            else -> {
                                likes.value = likes.value!! + 1
                                liked.value = !liked.value!!
                            }
                        }

                    }
            )
            if (likes.value!! > 0) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = likes.value.toString(),
                    fontSize = 13.sp
                )
            }
        }
    }
}
