package com.example.instagram.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.instagram.api.ApiServices
import com.example.instagram.models.ProfilePostsItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ProfilePostsPagingSource @AssistedInject constructor(
    private val apiServices: ApiServices,
    @Assisted private val profileId: String
) :
    PagingSource<Int, ProfilePostsItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProfilePostsItem> {
        return try {
            val page = params.key ?: 1

            val response = apiServices.getProfilePosts(profileId, page)
            val postsList =
                response.body()?.profilePosts?.filterNotNull() ?: emptyList()

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (postsList.isEmpty()) null else page + 1

            return LoadResult.Page(
                data = postsList,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProfilePostsItem>): Int? {
        return state.anchorPosition?.let { pos ->
            val page = state.closestPageToPosition(pos)
            page?.prevKey?.minus(1) ?: page?.nextKey?.plus(1)
        }
    }
}

