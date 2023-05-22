package com.example.instagram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.instagram.NavGraphs
import com.example.instagram.R
import com.example.instagram.api.Resource
import com.example.instagram.appCurrentDestinationAsState
import com.example.instagram.destinations.CommentsScreenDestination
import com.example.instagram.destinations.Destination
import com.example.instagram.destinations.EditProfileScreenDestination
import com.example.instagram.destinations.FeedScreenDestination
import com.example.instagram.destinations.NewPostSelectScreenDestination
import com.example.instagram.destinations.NewProfileSelectScreenDestination
import com.example.instagram.destinations.ProfileScreenDestination
import com.example.instagram.other.BottomBarDestination
import com.example.instagram.other.NoRippleInteractionSource
import com.example.instagram.other.currentUser
import com.example.instagram.startAppDestination
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.ui.theme.BottomColorIcon
import com.example.instagram.ui.theme.FabColor
import com.example.instagram.ui.theme.IconsColorBottom
import com.example.instagram.ui.theme.UserStoryBorder
import com.example.instagram.viewmodels.HomeViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.navigate
import timber.log.Timber


@Composable
fun HomeScreenWidget(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundColor
    )
    LaunchedEffect(true) {
        homeViewModel.getCurrentUser()
    }
    val getCurrentUserResult by homeViewModel.getCurrentUserResult.collectAsState()
    if (getCurrentUserResult is Resource.Loading) {
        CircularProgressIndicator()
    } else {
        when (getCurrentUserResult) {
            is Resource.Success -> {
                currentUser = getCurrentUserResult.data
            }

            is Resource.Error -> {
                Timber.e(getCurrentUserResult.message)
            }

            else -> {}
        }
    }
    val navController = rememberNavController()
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination
    val showFabAndBottomBar = currentDestination !is CommentsScreenDestination
            && currentDestination !is EditProfileScreenDestination
            && currentDestination !is NewProfileSelectScreenDestination
    Scaffold(
        floatingActionButton = {
            if (showFabAndBottomBar)
                IconButton(
                    interactionSource = NoRippleInteractionSource(),
                    modifier = Modifier
                        .background(FabColor, shape = CircleShape)
                        .size(65.dp),
                    onClick = {
                        navigator.navigate(NewPostSelectScreenDestination)
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.insta_ic),
                        contentDescription = "Add",
                        tint = Color.White
                    )
                }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            if (showFabAndBottomBar)
                BottomNavigationView(navController, currentDestination)
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            DestinationsNavHost(
                navController = navController,
                navGraph = NavGraphs.root,
                startRoute = FeedScreenDestination
            )
        }
    }
}


@Composable
fun BottomNavigationView(navController: NavController, currentDestination: Destination) {
    BottomAppBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
        backgroundColor = Color.White,
        elevation = 22.dp,
    ) {
        BottomBarDestination.values().forEach { destination ->
            BottomNavigationItem(
                selectedContentColor = BottomColorIcon,
                unselectedContentColor = IconsColorBottom,
                selected = currentDestination == destination.direction,
                onClick = {
                    if (destination.direction != currentDestination) {
                        navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                            launchSingleTop = true
                        })
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.icon),
                        contentDescription = null
                    )
                },
            )
        }
        BottomNavigationItem(
            selected = currentDestination == ProfileScreenDestination,
            onClick = {
                if (currentDestination != ProfileScreenDestination) {
                    navController.navigate(
                        ProfileScreenDestination(userId = currentUser?._id!!),
                        fun NavOptionsBuilder.() {
                            launchSingleTop = true
                        })
                }
            },
            icon = {
                Icon(
                    painter = rememberAsyncImagePainter(model = currentUser?.avatar),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .border(
                            width = if (currentDestination == ProfileScreenDestination) 1.5.dp else 0.dp,
                            brush = if (currentDestination == ProfileScreenDestination) UserStoryBorder else Brush.verticalGradient(
                                listOf(
                                    Color.White,
                                    Color.White,
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(if (currentDestination == ProfileScreenDestination) 3.dp else 0.dp)
                        .size(27.dp)
                        .clip(CircleShape),
                    tint = Color.Unspecified
                )
            },
        )
    }
}

/*********************** end of bottomNavigationView *******************/


