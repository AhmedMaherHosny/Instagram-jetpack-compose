package com.example.instagram

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instagram.screens.CommentsScreenWidget
import com.example.instagram.screens.EditProfileWidget
import com.example.instagram.screens.FeedScreenWidget
import com.example.instagram.screens.HomeScreenWidget
import com.example.instagram.screens.LoginScreenWidget
import com.example.instagram.screens.NewPostSelectWidget
import com.example.instagram.screens.NewPostWidget
import com.example.instagram.screens.NewProfileSelectWidget
import com.example.instagram.screens.NotificationScreenWidget
import com.example.instagram.screens.ProfileScreenWidget
import com.example.instagram.screens.RegisterScreenWidget
import com.example.instagram.screens.SearchScreenWidget
import com.example.instagram.screens.SplashScreenWidget
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.ui.theme.InstagramTheme
import com.example.instagram.viewmodels.EditProfileViewModel
import com.example.instagram.viewmodels.HomeViewModel
import com.example.instagram.viewmodels.LoginViewModel
import com.example.instagram.viewmodels.PostViewModel
import com.example.instagram.viewmodels.ProfileViewModel
import com.example.instagram.viewmodels.RegisterViewModel
import com.example.instagram.viewmodels.SplashScreenViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstagramTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(
                    color = BackgroundColor
                )
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}

@RootNavGraph(start = true)
@Destination
@Composable
fun SplashScreen(
    navigator: DestinationsNavigator,
    splashScreenViewModel: SplashScreenViewModel = hiltViewModel()
) {
    SplashScreenWidget(navigator, splashScreenViewModel)
}

@Destination
@Composable
fun HomeScreen(navigator: DestinationsNavigator) {
    HomeScreenWidget(navigator = navigator)
}

@Destination
@Composable
fun RegisterScreen(
    navigator: DestinationsNavigator,
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
    RegisterScreenWidget(navigator, registerViewModel)
}

@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    LoginScreenWidget(navigator, loginViewModel)
}

@Destination
@Composable
fun FeedScreen(navigator: DestinationsNavigator) {
    FeedScreenWidget(navigator = navigator)
}

@Destination
@Composable
fun SearchScreen(navigator: DestinationsNavigator) {
    SearchScreenWidget(navigator = navigator)
}

@Destination
@Composable
fun NotificationScreen() {
    NotificationScreenWidget()
}

@Destination
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    userId: String,
    navigator: DestinationsNavigator,
) {
    ProfileScreenWidget(profileViewModel, userId, navigator)
}


@Destination
@Composable
fun CommentsScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    usernameForAuthor: String,
    postId: String
) {
    CommentsScreenWidget(homeViewModel, navigator, usernameForAuthor, postId)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Destination
@Composable
fun NewPostSelectScreen(
    navigator: DestinationsNavigator,
    postViewModel: PostViewModel = hiltViewModel()
) {
    NewPostSelectWidget(navigator, postViewModel)
}

@Destination
@Composable
fun NewPostScreen(
    navigator: DestinationsNavigator,
    postViewModel: PostViewModel = hiltViewModel()

) {
    NewPostWidget(navigator, postViewModel)
}

@Destination
@Composable
fun EditProfileScreen(
    navigator: DestinationsNavigator,
    editProfileViewModel: EditProfileViewModel = hiltViewModel()
) {
    EditProfileWidget(navigator, editProfileViewModel)
}

@Destination
@Composable
fun NewProfileSelectScreen(
    navigator: DestinationsNavigator
) {
    NewProfileSelectWidget(navigator)
}