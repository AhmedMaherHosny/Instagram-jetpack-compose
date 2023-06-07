package com.example.instagram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.instagram.R
import com.example.instagram.api.Resource
import com.example.instagram.destinations.EditProfileScreenDestination
import com.example.instagram.models.ProfilePostsItem
import com.example.instagram.models.ProfileResponse
import com.example.instagram.other.LoadingItem
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.ui.theme.FabColor
import com.example.instagram.ui.theme.ProfileColor
import com.example.instagram.ui.theme.UserNotStoryBorder
import com.example.instagram.ui.theme.UserStoryBorder
import com.example.instagram.viewmodels.ProfileViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ProfileScreenWidget(
    profileViewModel: ProfileViewModel,
    userId: String,
    navigator: DestinationsNavigator,
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.White
    )
    val profilePostsList = profileViewModel.profilePostsPager.collectAsLazyPagingItems()
    profileViewModel.profileId = userId
    val getProfileResult by profileViewModel.getProfileResult.collectAsState()
    LaunchedEffect(true) {
        profileViewModel.getProfile(userId)
    }
    if (getProfileResult is Resource.Loading || profilePostsList.loadState.refresh is LoadState.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Center))
        }
    } else {
        when (getProfileResult) {
            is Resource.Success -> {
                val response = getProfileResult.data!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundColor)
                ) {
                    CardView(
                        response,
                        modifier = Modifier,
                        navigator,
                        profileViewModel,
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(
                            count = profilePostsList.itemCount,
//                            key = {
//                                profilePostsList[it]?._id!!
//                            }
                        ) {
                            PostPicItem(
                                modifier = Modifier
                                    .size(height = 200.dp, width = 200.dp)
                                    .padding(start = 10.dp, end = 10.dp, bottom = 15.dp)
                                    .clip(RoundedCornerShape(25.dp)),
                                profilePostsItem = profilePostsList[it]!!
                            )

                        }
                        when (profilePostsList.loadState.append) {
                            is LoadState.NotLoading -> Unit
                            LoadState.Loading -> {
                                item { LoadingItem() }
                            }

                            is LoadState.Error -> {
                                item {

                                }
                            }
                        }
                    }
                }
            }

            is Resource.Error -> {}
            else -> {}
        }
    }
}

@Composable
fun CardView(
    response: ProfileResponse,
    modifier: Modifier,
    navigator: DestinationsNavigator,
    profileViewModel: ProfileViewModel,
) {
    var textButton by remember { mutableStateOf("") }
    Card(
        modifier = modifier
            .shadow(20.dp, RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp))
            .clip(shape = RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp))
            .background(Color.White)
            .wrapContentHeight()
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = profileViewModel.noRippleInteractionSource
                    ) { navigator.popBackStack() },
                )
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = profileViewModel.noRippleInteractionSource
                    ) { }
                )
            }
            /*----------------------end of top bar-----------------------*/

            AsyncImage(
                model = response.avatar,
                contentDescription = "profile pic",
                modifier = Modifier
                    .border(
                        width = 3.dp,
                        brush = if (true == true) UserStoryBorder else UserNotStoryBorder,
                        shape = CircleShape
                    )
                    .padding(5.dp)
                    .size(125.dp)
                    .clip(CircleShape)
                    .align(CenterHorizontally)
            )

            /*----------------------end of image-----------------------*/

            Spacer(modifier = Modifier.height(7.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(CenterHorizontally)
            ) {
                Text(
                    text = response.username ?: "UserX",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.width(5.dp))
                if (response.isAdmin == true) {
                    Icon(
                        painter = painterResource(id = R.drawable.protection),
                        contentDescription = "admin",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            /*----------------------end of username-----------------------*/

            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = response.bio ?: "",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.align(CenterHorizontally)
            )

            /*----------------------end of bio-----------------------*/

            Spacer(modifier = Modifier.height(7.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(start = 7.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(
                        text = response.postList?.size.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Text(
                        text = "Post",
                        fontSize = 10.sp,
                        color = ProfileColor,
                    )
                }

                Column(
                    horizontalAlignment = CenterHorizontally,
                    modifier = Modifier
                        .padding(start = 13.dp)
                ) {
                    Text(
                        text = response.numberOfFollowing.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Text(
                        text = "Following",
                        fontSize = 10.sp,
                        color = ProfileColor,
                    )
                }

                Column(
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(
                        text = response.numberOfFollowers.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Text(
                        text = "Followers",
                        fontSize = 10.sp,
                        color = ProfileColor,
                    )
                }
            }

            /*----------------------end of followers section-----------------------*/

            Spacer(modifier = Modifier.height(7.dp))
            if (response.isCurrentUser == true) {
                Button(
                    onClick = {
                              navigator.navigate(EditProfileScreenDestination)
                    },
                    interactionSource = profileViewModel.noRippleInteractionSource,
                    modifier = Modifier
                        .background(FabColor, shape = CircleShape)
                        .padding(vertical = 0.dp, horizontal = 27.dp)
                        .align(CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Unspecified,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "edit profile",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                textButton = if (response.isFollowing == true) "Unfollow" else "Follow"
                Button(
                    onClick = {
                        if (textButton == "Unfollow") {
                            textButton = "Follow"
                            profileViewModel.unFollowUser(response._id!!)
                        } else {
                            textButton = "Unfollow"
                            profileViewModel.followUser(response._id!!)
                        }
                    },
                    interactionSource = profileViewModel.noRippleInteractionSource,
                    modifier = Modifier
                        .background(FabColor, shape = CircleShape)
                        .padding(vertical = 0.dp, horizontal = 27.dp)
                        .align(CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Unspecified,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = textButton,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

            }

            /*----------------------end of followers button-----------------------*/

        }
    }
}

@Composable
fun PostPicItem(modifier: Modifier, profilePostsItem: ProfilePostsItem) {
    AsyncImage(
        model = profilePostsItem.firstImageUrl,
        contentDescription = "post image",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
