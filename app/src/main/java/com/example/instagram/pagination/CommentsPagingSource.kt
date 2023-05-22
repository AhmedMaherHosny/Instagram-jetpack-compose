package com.example.instagram.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.instagram.api.ApiServices
import com.example.instagram.models.CommentsItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class CommentsPagingSource @AssistedInject constructor(
    private val apiServices: ApiServices,
    @Assisted private val postId: String
) :
    PagingSource<Int, CommentsItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentsItem> {
        return try {
            val page = params.key ?: 1

            val response = apiServices.getCommentsByPostId(postId, page)
            val commentsList =
                response.body()?.comments?.filterNotNull() ?: emptyList()

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (commentsList.isEmpty()) null else page + 1

            return LoadResult.Page(
                data = commentsList,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CommentsItem>): Int? {
        return state.anchorPosition?.let { pos ->
            val page = state.closestPageToPosition(pos)
            page?.prevKey?.minus(1) ?: page?.nextKey?.plus(1)
        }
    }
}

