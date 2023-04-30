package com.github.sdp_begreen.begreen.data
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import kotlinx.coroutines.flow.Flow

/**
 * repository class to manage the data flow and map it if needed
 */
@ExperimentalPagingApi
class FeedRepository{

    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        const val DEFAULT_PAGE_SIZE = 20

        //get feed repository instance
        fun getInstance() = FeedRepository()
    }

    /**
     * calling the paging source to give results from api calls
     * and returning the results in the form of flow [Flow<PagingData<PhotoMetadata>>]
     * since the [PagingDataAdapter] accepts the [PagingData] as the source in later stage
     */
    fun letPhotoMetadataFlow(posts: List<PhotoMetadata>, pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<PhotoMetadata>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { PostPagingSource(posts) }
        ).flow
    }

    /**
     * let's define page size, page size is the only required param, rest is optional
     */
    fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = true)
    }

    //fun letPhotoMetadataFlowDb(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<PhotoMetadata>> {
    //    if (db == null) throw IllegalStateException("Database is not initialized")
//
    //    val pagingSourceFactory = { appDatabase.getImageModelDao().getAllDoggoModel() }
    //    return Pager(
    //        config = pagingConfig,
    //        pagingSourceFactory = pagingSourceFactory,
    //        remoteMediator = FeedMediator()
    //    ).flow
    //}

}