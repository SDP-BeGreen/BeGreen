package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import com.github.sdp_begreen.begreen.exceptions.DatabaseTimeoutException
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.google.firebase.database.DatabaseException
import com.google.firebase.storage.StorageException

interface DB {

    companion object {
        private const val TIMEOUT: Long = 10000 // Default waiting time before aborting a <get> on the database
    }

    /**
     * Returns the value associated to the to given [key] in the database
     *
     * @param key the key we want to know the value of
     * @param timeout the maximum time we wait for the database to respond
     * @return the value associated to the [key] or null if could not retrieve it
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun get(key: String, timeout: Long = TIMEOUT): String?

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
     * @param timeout the maximum time we wait for the database to respond
     * @return the [User] associated to the given [userId], or null if it wasn't found
     *
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if an exception occurred while retrieving the data
     * @throws IllegalArgumentException if the [userId] was blank or empty
     */
    suspend fun getUser(userId: String, timeout: Long = TIMEOUT): User?

    /**
     * Retrieve the list of all the users [User] in the database
     *
     * @param timeout the maximum time we wait for the database to respond
     * @return the list of all the users [User] in the database
     *
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun getAllUsers(timeout: Long = TIMEOUT): List<User>

    /**
     * Store the profile picture for the given [User]
     *
     * @param image the profile picture to store
     * @param userId the [User] for whom to add the pictures
     * @return the updated [PhotoMetadata] with the current id of the image we stored
     *
     * @throws IllegalArgumentException if the [userId] was blank or empty
     */
    suspend fun storeUserProfilePicture(image: Bitmap, userId: String, metadata: PhotoMetadata): PhotoMetadata?

    /**
     * Adds and [image] for the user [userId] in the database
     *
     * @param image the image we want to add to the database
     * @param userId the ID of the user wanting to store a new image
     * @return a unique ID under which the image got stored, and null if the image couldn't get stored
     */
    suspend fun addImage(image: Bitmap, userId: Int, metadata: PhotoMetadata): PhotoMetadata?

    /**
     * Test whether a [User] exists in the database for the given [userId]
     *
     * @param userId The id of the user to check for the existence in the database
     * @param timeout the maximum time we wait for the database to respond
     * @return true if the user exists, false otherwise
     */
    suspend fun userExists(userId: String, timeout: Long = TIMEOUT): Boolean

    /**
     * Retrieves the image associated with the given [userId] and [metadata] from the database
     *
     * @param metadata the metadata associated with the given image we want to retrieve
     * @param userId the ID of the user where we should find the image
     * @param timeout the maximum time we wait for the database to respond
     * @return the image, or null if no image was found
     *
     * @throws StorageException if the image could not be retrieved
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if an exception occurred while retrieving the image
     */
    suspend fun getImage(metadata: PhotoMetadata, userId: Int, timeout: Long = TIMEOUT): Bitmap?

    /**
     * Retrieves the profile image associated with the given [userId] and [metadata] from the database
     *
     * @param metadata the metadata associated with the given image we want to retrieve
     * @param userId the ID of the user where we should find the image
     * @param timeout the maximum time we wait for the database to respond
     * @return the image, or null if no image was found
     * @throws StorageException if the image could not be retrieved
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if an exception occurred while retrieving the image
     */
    suspend fun getUserProfilePicture(metadata: PhotoMetadata, userId: String, timeout: Long = TIMEOUT): Bitmap?

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
     * Retrieves the set of all bins currently stored in the database
     *
     * @param timeout the maximum time we wait for the database to respond
     * @return the set of bins fetched from the database
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if an exception occurred while retrieving the data
     */
    suspend fun getAllBins(timeout: Long = TIMEOUT): Set<Bin>

    /**
     * Retrieves the list of advices from the realtime database
     *
     * @return the set of all advices
     */
    suspend fun getAdvices(): Set<String>


}