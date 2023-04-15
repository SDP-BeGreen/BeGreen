package com.github.sdp_begreen.begreen.social

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.social.GoogleAuth.mGoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


object GoogleAuth {

    // GoogleSignInClient object that can be used to initiate the Google sign-in process in an Android app
    // This variable will hold the configured GoogleSignInClient object.
    // Source : https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInClient
    @SuppressLint("StaticFieldLeak")
    lateinit var mGoogleSignInClient: GoogleSignInClient

    fun googleClient(context: Context) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Create a GoogleSignInClient object to interact with the Google Sign-In API
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    fun googleSignIn() = mGoogleSignInClient.signInIntent

    fun googleLogOut(context: Context, logoutCallback: (Context) -> Unit) {
        googleClient(context)
        mGoogleSignInClient.signOut()
        logoutCallback(context)
    }

}

