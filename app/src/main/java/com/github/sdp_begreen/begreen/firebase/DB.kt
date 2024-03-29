package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import com.github.sdp_begreen.begreen.exceptions.DatabaseTimeoutException
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.ProfilePhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.google.firebase.database.DatabaseException
import com.google.firebase.storage.StorageException

interface DB {

    /**
     * Returns the value associated to the to given [key] in the database
     *
     * @param key the key we want to know the value of
     * @return the value associated to the [key] or null if could not retrieve it
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun get(key: String): String?

    /**
     * Associate a new [value] to the given [key].
     * Create a new entry if the [key] did not exist
     *
     * @param key the key we want to set the value (non empty)
     * @param value the new value for the [key]
     * @throws IllegalArgumentException if the userId is blank or empty
     */
    suspend fun set(key: String, value: String)

    /**
     * Add a new [user] to the given [userId]
     *
     * @param user the user we want to add to the database
     * @param userId the userId to use as key to store the user
     * @throws IllegalArgumentException if the userId is blank or empty
     */
    suspend fun addUser(user: User, userId: String)

    /**
     * Retrieve the [User] associated with the given [userId]
     *
     * @param userId The id of the user we want to retrieve from the
     * @return the [User] associated to the given [userId], or null if it wasn't found
     *
     * @throws DatabaseException if an exception occurred while retrieving the data
     * @throws IllegalArgumentException if the [userId] was blank or empty
     */
    suspend fun getUser(userId: String): User?

    /**
     * Retrieve the list of all the users [User] in the database
     *
     * @return the list of all the users [User] in the database
     *
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun getAllUsers(): List<User>

    /**
     * Store the profile picture for the given [User]
     *
     * @param image the profile picture to store
     * @param userId the [User] for whom to add the pictures
     * @return the updated [PhotoMetadata] with the current id of the image we stored
     *
     * @throws IllegalArgumentException if the [userId] was blank or empty
     */
    suspend fun storeUserProfilePicture(image: Bitmap, userId: String, metadata: ProfilePhotoMetadata): ProfilePhotoMetadata?


    /**
     * Adds and [image] of the trash to store in the database
     *
     * @param image the image we want to add to the database
     * @param trashPhotoMetadata the photoMetadata associated to this trash
     * @return the PhotoMetadata under which the image got stored, and null if the image couldn't get stored
     */
    suspend fun addTrashPhoto(image : Bitmap, trashPhotoMetadata: TrashPhotoMetadata): TrashPhotoMetadata?

    /**
     * Test whether a [User] exists in the database for the given [userId]
     *
     * @param userId The id of the user to check for the existence in the database
     * @return true if the user exists, false otherwise
     */
    suspend fun userExists(userId: String): Boolean

    /**
     * Retrieves the image associated with the given [metadata] from the database
     *
     * @param metadata the metadata associated with the given image we want to retrieve
     * @return the image, or null if no image was found
     *
     * @throws StorageException if the image could not be retrieved
     * @throws DatabaseException if an exception occurred while retrieving the image
     */
    suspend fun getImage(metadata: PhotoMetadata): Bitmap?

    /**
     * Retrieves the profile image associated with the given [userId] and [metadata] from the database
     *
     * @param metadata the metadata associated with the given image we want to retrieve
     * @param userId the ID of the user where we should find the image
     * @return the image, or null if no image was found
     * @throws StorageException if the image could not be retrieved
     * @throws DatabaseException if an exception occurred while retrieving the image
     */
    suspend fun getUserProfilePicture(metadata: ProfilePhotoMetadata, userId: String): Bitmap?

    /**
     * Store the given [bin] in the database and assigns a fresh id to the bin
     *
     * @param bin the bin to be stored
     * @return true if the bin got stored in the database, and false if it failed
     * @throws IllegalArgumentException if the bin id is NOT null
     * (as it means it is already stored in the database)
     */
    suspend fun addBin(bin: Bin): Boolean

    /**
     * Remove the bin with the given id from the database
     *
     * @param binId the id of the bin we want to remove from the database
     */
    suspend fun removeBin(binId: String)


    /**
     * Retrieves the list of all bins currently stored in the database
     *
     * @return the list of bins fetched from the database
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun getAllBins(): List<Bin>

    /**
     * Retrieves the list of advices from the realtime database
     *
     * @return the set of all advices
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun getAdvices(): Set<String>

    /**
     * Store in the database that the given User follows the other given User
     *
     * @param followerId the userId of the User that starts following the other User
     * @param followedId the userId of the User that starts being followed by the other User
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun follow(followerId: String, followedId: String)

    /**
     * Store in the database that the given User stops following the other given User
     *
     * @param followerId the userId of the User that stops following the other User
     * @param followedId the userId of the User that stops being followed by the other User
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun unfollow(followerId: String, followedId: String)

    /**
     * Returns the list of UserIds that the given [userId] follows
     *
     * @param userId the userId of the User we want to retrieve the followed users
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun getFollowedIds(userId: String): List<String>

    /**
     * Returns the list of UserIds that follow the given [userId]
     *
     * @param userId the userId for which we want to get the list of userIds that follow this User
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun getFollowerIds(userId: String): List<String>

    /**
     * Returns the list of Users that follow the User with the given [userId]
     *
     * @param userId the userId for which we want to get the list of Users that follow this User
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun getFollowers(userId: String): List<User>

    /**
     * Add a feedback to the database
     *
     * @param feedback the feedback to be added to the database
     * @param userId the Id of the user that added the feedback
     * @param date the date at which the feedback was sent
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun addFeedback(feedback: String, userId: String, date: String)
}