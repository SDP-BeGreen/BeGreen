package com.github.sdp_begreen.begreen.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import org.koin.java.KoinJavaComponent

private const val  STARTING_KEY = 0

/**
 * A PagingSource that loads data from the network.
 */
class PostPagingSource(var photos: List<PhotoMetadata>) : PagingSource<Int, PhotoMetadata>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoMetadata> {
        // Start paging with the STARTING_KEY if this is the first load
        val position = params.key ?: STARTING_KEY
        // Load as many items as hinted by params.loadSize
        val range = position.until(position + params.loadSize)
        val photoBatch: MutableList<PhotoMetadata> = mutableListOf()

        return try {
            // Load the data from the network
            for (i in   range) {
                photoBatch += photos[i]
            }
            // Get the next key for the next page
            val nextKey = if (photoBatch.isEmpty()) null else position + photoBatch.size
            //currentPos += photoBatch.size
            // Return the data and next key
            LoadResult.Page(
                photoBatch,
                prevKey = if (position == STARTING_KEY) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            // Return the error
            LoadResult.Error(e)
        }
    }


    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
    private fun ensureValidKey(key: Int) = Integer.max(STARTING_KEY, key)
    override fun getRefreshKey(state: PagingState<Int, PhotoMetadata>): Int? {
        return state.anchorPosition
    }
}