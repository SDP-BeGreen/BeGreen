package com.github.sdp_begreen.begreen.firebase

import android.app.Activity
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

interface Auth {

    /**
     * Method to get the uid of the currently authenticated user
     *
     * @return Returns the uid of the currently authenticated user
     */
    fun getConnectedUserId(): String?

    /**
     * Method to get the uid of the currently authenticated user as a flow, (i.e. the
     * collector of this flow will receive the new uid upon changes in authentication
     * status)
     *
     * @return Returns a flow that will emit last uid upon changes
     */
    fun getConnectedUserIds(): Flow<String?>

    /**
     * Method to sign out the currently logged in user
     *
     * @param activity The activity from which we are calling the sign out method
     * @param webClientId The id token to identify the connected user
     */
    fun signOutCurrentUser(activity: Activity, webClientId: String): Task<Void>
}