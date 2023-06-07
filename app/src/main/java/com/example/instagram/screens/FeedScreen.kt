package com.example.instagram.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.instagram.R
import com.example.instagram.destinations.ChatScreenDestination
import com.example.instagram.destinations.CommentsScreenDestination
import com.example.instagram.destinations.ProfileScreenDestination
import com.example.instagram.models.*
import com.example.instagram.garbage.other.LoadingItem
import com.example.instagram.garbage.other.NoRippleInteractionSource
import com.example.instagram.ui.theme.*
import com.example.instagram.viewmodels.HomeViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun FeedScreenWidget(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundColor
    )
    val lazyPagingItems = homeViewModel.followingPostsListPager.collectAsLazyPagingItems()
    val lazyColumnState = rememberLazyListState()
    var searchText by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .background(BackgroundColor)
            .padding(start = 15.dp, end = 15.dp, top = 15.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .background(BackgroundColor)
                .fillMaxSize()
        ) {
            ToolBar(modifier = Modifier, homeViewModel.noRippleInteractionSource, navigator)
            Spacer(modifier = Modifier.height(10.dp))
//                        StorySection(modifier = Modifier, currentUser)
            Spacer(modifier = Modifier.height(15.dp))
            ExplorerSection(
                modifier = Modifier,
                value = searchText
            ) { searchText = it }
            Spacer(modifier = Modifier.height(10.dp))
            PostsSection(
                modifier = Modifier,
                homeViewModel,
                navigator,
                lazyPagingItems,
                lazyColumnState
            )
        }
    }
}

@Composable
fun ToolBar(
    modifier: Modifier,
    noRippleInteractionSource: NoRippleInteractionSource,
    navigator: DestinationsNavigator
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.toolbar_plus),
            contentDescription = "Add Icon",
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = noRippleInteractionSource
            ) {

            }
        )
        Icon(
            painter = painterResource(id = R.drawable.instagram_logo),
            contentDescription = "Instagram"
        )
        if (0 > 0) {
            BadgedBox(
                badge = {
                    Badge {
                        Text(
                            text = "0",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
            ) {
                Icon(
                    painterResource(id = R.drawable.chat),
                    contentDescription = "Chat Icon",
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = noRippleInteractionSource
                    ) {
                        navigator.navigate(ChatScreenDestination)
                    }
                )
            }
        } else {
            Icon(
                painterResource(id = R.drawable.chat),
                contentDescription = "Chat Icon",
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = noRippleInteractionSource
                ) {
                    navigator.navigate(ChatScreenDestination)
                }
            )
        }
    }
}

/*********************** end of toolBar *******************/

@Composable
fun StorySection(modifier: Modifier, currentUser: UserX?) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
    ) {
        items(1) {
            if (it == 0) {
                UserStoryItem(modifier = Modifier, currentUser)
            }
            if (true == false) {
                FriendStoryItem(modifier = Modifier, index = it)
            }
        }
    }
}

@Composable
fun UserStoryItem(modifier: Modifier, currentUser: UserX?) {
    Box(
        modifier = modifier
            .padding(end = 15.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = currentUser?.avatar),
            contentDescription = null,
            modifier = modifier
                .border(
                    width = 2.dp,
                    brush = if (true == false) UserStoryBorder else UserNotStoryBorder,
                    shape = CircleShape
                )
                .padding(3.dp)
                .size(70.dp)
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
                    .border(width = 2.dp, color = BackgroundColor, shape = CircleShape)
                    .padding(2.dp)
                    .size(18.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Icon Add",
                    tint = BackgroundColor,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(brush = UserStoryBorder)
                        .fillMaxSize()
                )
            }

        }
    }

}

@Composable
fun FriendStoryItem(modifier: Modifier, index: Int) {
    Column(
        modifier = modifier
            .padding(end = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = "https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8aHVtYW58ZW58MHx8MHx8&w=1000&q=80"),
            contentDescription = null,
            modifier = Modifier
                .border(
                    width = 2.dp,
                    brush = UserStoryBorder,
                    shape = CircleShape
                )
                .padding(3.dp)
                .size(70.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "ahmed",
            color = NameColor,
            fontSize = 10.sp,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }

}

/*********************** end of storySection *******************/

@Composable
fun ExplorerSection(modifier: Modifier, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Explorer",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = ExplorerColor
        )
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = painterResource(id = R.drawable.search_ic),
            modifier = Modifier
        )
    }
}


@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: Painter,
    modifier: Modifier
) {
    TextField(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .height(48.dp),
        maxLines = 1,
        textStyle = TextStyle(fontSize = 14.sp),
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                painter = leadingIcon,
                contentDescription = null,
            )
        },
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = ExplorerColor,
            disabledTextColor = ExplorerColor,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = ExplorerColor
        )
    )
}

/*********************** end of explorerSection *******************/
@Composable
fun PostsSection(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    navigator: DestinationsNavigator,
    lazyPagingItems: LazyPagingItems<FollowingPostsItem>,
    lazyColumnState: LazyListState,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = lazyColumnState
    ) {
        items(lazyPagingItems) {
            PostItem(
                modifier = Modifier
                    .padding(bottom = 12.dp),
                it,
                homeViewModel,
                navigator
            )
        }
//        items(
//            count = lazyPagingItems.itemCount,
//            key = {
//                lazyPagingItems[it]?._id!!
//            },
//            itemContent = {
//                PostItem(modifier = Modifier.padding(bottom = 12.dp), lazyPagingItems[it], homeViewModel = homeViewModel, navigator = navigator)
//            }
//        )
        when (lazyPagingItems.loadState.append) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item { LoadingItem() }
            }

            is LoadState.Error -> {
                item {

                }
            }
        }
        when (lazyPagingItems.loadState.refresh) {
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

@Composable
fun PostItem(
    modifier: Modifier,
    followingPostsItem: FollowingPostsItem?,
    homeViewModel: HomeViewModel,
    navigator: DestinationsNavigator,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = 0.dp,
        shape = RoundedCornerShape(30.dp),
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = followingPostsItem?.postedBy?.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(50.dp, 50.dp)
                            .clickable { navigator.navigate(ProfileScreenDestination(userId = followingPostsItem?.postedBy?._id!!)) }
                    )
                    Column(modifier = Modifier.padding(start = 15.dp)) {
                        Text(
                            text = followingPostsItem?.postedBy?.username!!,
                            fontSize = 15.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = followingPostsItem.createdAt!!,
                            fontSize = 10.sp,
                            color = PostColor
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.share_ic),
                        contentDescription = "Share Icon",
                        tint = IconsColor,
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .clickable(
                                indication = null,
                                interactionSource = homeViewModel.noRippleInteractionSource
                            ) {

                            }

                    )
                    Icon(
                        painter = painterResource(id = R.drawable.four_dots_ic),
                        contentDescription = "Share Icon",
                        tint = IconsColor,
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = homeViewModel.noRippleInteractionSource
                            ) {

                            }

                    )
                }
            }
            ImageSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(end = 10.dp, start = 10.dp, bottom = 10.dp),
                sliderList = followingPostsItem?.imageOrVideoUrl,
                likes = followingPostsItem?.likes!!.size,
                followingPostsItem.liked,
                homeViewModel,
                followingPostsItem._id!!,
                followingPostsItem.postedBy?.username!!,
                navigator
            )
            Column(
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 20.dp),
            ) {
                if (followingPostsItem.caption != null) {
                    TextComment(
                        modifier = Modifier,
                        name = followingPostsItem.postedBy.username,
                        description = followingPostsItem.caption
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(
    modifier: Modifier,
    sliderList: List<String?>?,
    likes: Int,
    liked: Boolean?,
    homeViewModel: HomeViewModel,
    postId: String,
    postedByUserName: String,
    navigator: DestinationsNavigator,
) {
    var likesState by remember { mutableStateOf(likes) }
    var likedState by remember { mutableStateOf(liked) }
    when (sliderList!!.size) {
        1 -> {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(30.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AsyncImage(
                        model = sliderList.first(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 22.dp, bottom = 33.dp)
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.heart_filled),
                                contentDescription = "Like Icon",
                                tint = if (likedState == true) Color.White else Color.Black,
                                modifier = Modifier
                                    .size(20.dp)
                                    .drawBehind {
                                        drawCircle(
                                            color = Color(0XFFFC0B7B),
                                            radius = this.size.maxDimension
                                        )
                                    }
                                    .clickable(
                                        indication = null,
                                        interactionSource = homeViewModel.noRippleInteractionSource
                                    ) {
                                        likedState = if (likedState == true) {
                                            likesState--
                                            false
                                        } else {
                                            likesState++
                                            true
                                        }
                                        homeViewModel.likeOrUnlikePost(postId)

                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 15.dp)
                            ) {
                                Text(
                                    text = likesState.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .background(
                                            color = Color(0XFF181818).copy(ContentAlpha.disabled),
                                            shape = RoundedCornerShape(1000.dp)
                                        )
                                        .padding(horizontal = 13.dp)
                                )
                            }

                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 22.dp, bottom = 33.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.chat),
                            contentDescription = "Like Icon",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                                .drawBehind {
                                    drawCircle(
                                        color = Color.White,
                                        radius = this.size.maxDimension
                                    )
                                }
                                .clickable(
                                    indication = null,
                                    interactionSource = homeViewModel.noRippleInteractionSource
                                ) {
                                    navigator.navigate(
                                        CommentsScreenDestination(
                                            usernameForAuthor = postedByUserName,
                                            postId = postId
                                        )
                                    )

                                }
                        )
                    }

                }
            }
        }

        else -> {
            val pagerState = rememberPagerState()
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(30.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        count = sliderList.size,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        AsyncImage(
                            model = sliderList[it],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 25.dp, end = 15.dp)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(
                                    color = Color(0XFF181818).copy(ContentAlpha.disabled),
                                    shape = RoundedCornerShape(1000.dp)
                                )
                                .padding(horizontal = 13.dp)
                        )
                    }
                    HorizontalPagerIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 19.dp),
                        pagerState = pagerState,
                        spacing = 8.dp,
                        activeColor = Color.White,
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 22.dp, bottom = 33.dp)
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.heart_filled),
                                contentDescription = "Like Icon",
                                tint = if (likedState == true) Color.White else Color.Black,
                                modifier = Modifier
                                    .size(20.dp)
                                    .drawBehind {
                                        drawCircle(
                                            color = Color(0XFFFC0B7B),
                                            radius = this.size.maxDimension
                                        )
                                    }
                                    .clickable(
                                        indication = null,
                                        interactionSource = homeViewModel.noRippleInteractionSource
                                    ) {
                                        likedState = if (likedState == true) {
                                            likesState--
                                            false
                                        } else {
                                            likesState++
                                            true
                                        }
                                        homeViewModel.likeOrUnlikePost(postId)

                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 15.dp)
                            ) {
                                Text(
                                    text = likes.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .background(
                                            color = Color(0XFF181818).copy(ContentAlpha.disabled),
                                            shape = RoundedCornerShape(1000.dp)
                                        )
                                        .padding(horizontal = 13.dp)
                                )
                            }

                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 22.dp, bottom = 33.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.chat),
                            contentDescription = "Like Icon",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                                .drawBehind {
                                    drawCircle(
                                        color = Color.White,
                                        radius = this.size.maxDimension
                                    )
                                }
                                .clickable(
                                    indication = null,
                                    interactionSource = homeViewModel.noRippleInteractionSource
                                ) {
                                    navigator.navigate(
                                        CommentsScreenDestination(
                                            usernameForAuthor = postedByUserName,
                                            postId = postId
                                        )
                                    )

                                }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun TextComment(modifier: Modifier, name: String, description: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            ) {
                append("$name : ")
            }
            withStyle(
                SpanStyle(
                    color = Color.Black,
                    fontSize = 11.sp
                )
            ) {
                append(description)
            }
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )

}

/*********************** end of postsSection *******************/
