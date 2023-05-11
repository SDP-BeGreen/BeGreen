package com.github.sdp_begreen.begreen.firebase.utils

/**
 * Interface that should be implemented to enforce a class to have an id, and that it must
 * be possible to copy an instance of this class with a new id
 *
 * @param T The type of the object to by copied
 */
interface CopyableWithId<T> {

    /**
     * Method to make a copy with a new id
     *
     * @param newId The new id to add when copying object
     */
    fun copyWithNewId(newId: String): T

    var id: String?
}