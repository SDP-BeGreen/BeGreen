package com.github.sdp_begreen.begreen.firebase.meetingServices

import android.graphics.Bitmap
import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import kotlinx.coroutines.flow.Flow

/**
 * Service to interact with the database to work with meeting's photos
 */
interface MeetingPhotoService {

    /**
     * Add a photo to the meeting
     *
     * @param meetingId The id of the meeting to which to add the photo
     * @param photoMetadata The metadata of the photo to be added
     * @param photo The actual photo to add
     *
     * @return The photo metadata containing the new id generated when adding the photo
     *
     * @throws IllegalArgumentException Throw if one of the arguments does not match the requirement
     * @throws EventServiceException Throw if an error occurred while adding the picture to the database,
     * or if an error occurred while generating the key for the photo
     */
    suspend fun addMeetingsPhoto(
        meetingId: String,
        photoMetadata: TrashPhotoMetadata,
        photo: Bitmap
    ): TrashPhotoMetadata

    /**
     * Get all the comments, retrieve the comment dynamically
     *
     * @param meetingId The id of the meeting from which to retrieve the list of photo metadata
     *
     * @return a flow of all the photos metadata for a particular meeting
     *
     * @throws IllegalArgumentException Throw if the meting id is blank
     */
    suspend fun getAllPhotosMetadata(meetingId: String): Flow<List<TrashPhotoMetadata>>

    /**
     * Get a photo given its metadata
     *
     * @param meetingId The id of the meting from which to retrieve the photo
     * @param photoMetadata The metadata of the photo to retrieve
     *
     * @return The photo as a bitmap, or null if the photo could not be decoded
     *
     * @throws IllegalArgumentException Throw if one of the arguments does not match the requirement
     * @throws EventServiceException Throw if an error occurred while retrieving the photo
     */
    suspend fun getPhoto(meetingId: String, photoMetadata: TrashPhotoMetadata): Bitmap?

    /**
     * Remove a photo from the meeting
     *
     * @param meetingId The id of the meeting from which to remove the photo
     * @param photoMetadata The metadata of the photo to remove
     *
     * @throws IllegalArgumentException Throw if one of the arguments does not match the requirement
     * @throws EventServiceException Throw if an error occurred while removing the comment
     */
    suspend fun removeMeetingPhoto(meetingId: String, photoMetadata: TrashPhotoMetadata)
}