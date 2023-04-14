package com.github.sdp_begreen.begreen.firebase


import android.app.Activity
import android.content.res.Resources
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.social.GoogleAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseAuth: Auth {

    override fun getConnectedUserId(): String? = Firebase.auth.uid

    override fun getConnectedUserIds(): Flow<String?> = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.uid) }
            Firebase.auth.addAuthStateListener(listener)

            // Unregister the listener to avoid memory leak upon flow deletion
            awaitClose {
                Firebase.auth.removeAuthStateListener(listener)
            }
        }

    override fun signOutCurrentUser(activity: Activity, webClientId: String): Task<Void> {
        Firebase.auth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        GoogleAuth.mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

        return GoogleAuth.mGoogleSignInClient.signOut()
    }
}