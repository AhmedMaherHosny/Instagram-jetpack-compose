package com.example.instagram.other

import com.example.instagram.R
import com.example.instagram.destinations.*
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: Int,
) {
    Feed(FeedScreenDestination, R.drawable.home_ic),
    Search(SearchScreenDestination, R.drawable.search_bottom),
    Notification(NotificationScreenDestination, R.drawable.heart_ic),
}