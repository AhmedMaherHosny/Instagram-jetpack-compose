package com.example.instagram.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.instagram.api.ApiServices
import com.example.instagram.models.FollowingPostsItem
import javax.inject.Inject

class FollowingPostsPagingSource @Inject constructor(private val apiServices: ApiServices) :
    PagingSource<Int, FollowingPostsItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FollowingPostsItem> {
        return try {
            val page = params.key ?: 1

            val response = apiServices.getFollowingPosts(page)
            val followingPostsItems =
                response.body()?.followingPosts?.filterNotNull() ?: emptyList()

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (followingPostsItems.isEmpty()) null else page + 1

            return LoadResult.Page(
                data = followingPostsItems,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FollowingPostsItem>): Int? {
        return state.anchorPosition?.let { pos ->
            val page = state.closestPageToPosition(pos)
            page?.prevKey?.minus(1) ?: page?.nextKey?.plus(1)
        }
    }
}

