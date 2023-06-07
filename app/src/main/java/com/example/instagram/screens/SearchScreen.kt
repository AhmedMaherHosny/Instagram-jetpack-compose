package com.example.instagram.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.instagram.R
import com.example.instagram.destinations.ProfileScreenDestination
import com.example.instagram.models.UserX
import com.example.instagram.garbage.other.NoRippleInteractionSource
import com.example.instagram.ui.theme.BackgroundColor
import com.example.instagram.ui.theme.ExplorerColor
import com.example.instagram.ui.theme.NameColor
import com.example.instagram.viewmodels.SearchViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun SearchScreenWidget(
    searchViewModel: SearchViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundColor
    )
    val searchText by searchViewModel.searchText.collectAsState()
    val users by searchViewModel.users.collectAsState()
    val isSearching by searchViewModel.isSearching.collectAsState()

    Column(
        Modifier
            .background(BackgroundColor)
            .fillMaxSize()
            .padding(15.dp)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            value = searchText,
            onValueChange = searchViewModel::onSearchTextChange,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search_ic),
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
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (users?.isNotEmpty() == true) {
                    items(users!!) {
                        UserItem(user = it!!, navigator, searchViewModel.noRippleInteractionSource)
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: UserX,
    navigator: DestinationsNavigator,
    noRippleInteractionSource: NoRippleInteractionSource
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 13.dp)
            .clickable(
                indication = null,
                interactionSource = noRippleInteractionSource
            ) {
                navigator.navigate(ProfileScreenDestination(userId = user._id!!))
            }
    ) {
        AsyncImage(
            model = user.avatar,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column {
            Row(
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${user.username}",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (user.isAdmin == true) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.protection),
                        contentDescription = "admin",
                        modifier = Modifier
                            .size(15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "${user.firstName} ${user.lastName}",
                color = NameColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}