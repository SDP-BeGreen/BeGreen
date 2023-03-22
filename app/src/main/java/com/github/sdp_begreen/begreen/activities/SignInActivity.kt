package com.github.sdp_begreen.begreen.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.social.GoogleAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    // Late initialization of the LinearLayoutCompat and FirebaseAuth variables
    private lateinit var signInGoogleLayout: LinearLayoutCompat
    private lateinit var firebaseAuth: FirebaseAuth

    // Using ActivityResultContracts to register a launcher for starting the Google sign-in activity
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // I will add a progress bar that appear when google sign in is uploading (new functionality)
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)


            } catch (e: ApiException) {
                // Handling the ApiException based on its status code
                val message = when (e.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Google sign-in cancelled"
                    GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Google sign-in failed"
                    else -> "Error signing in with Google: ${e.message}"
                }
                // Displaying a toast message to the user with the error message
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

        }
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInGoogleLayout =findViewById(R.id.signInGoogleLayout)

        firebaseAuth = FirebaseAuth.getInstance()

        // When the google sign in image is clicked, the google sign in page appears
        signInGoogleLayout.setOnClickListener {
            // Logging out of any existing Google account and starting the Google sign-in activity
            // We can comment that if we want to connect directly with the current account
            GoogleAuth.googleLogOut(this, logoutCallback = {})
            GoogleAuth.googleClient(this)
            launcher.launch(GoogleAuth.googleSignIn())
        }
    }

    /**
     * Helper function that signs in to Firebase using Google authentication.
     *
     * @param account the google sign in account
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this
            ) { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    return@addOnCompleteListener
                }
                if (!account.email!!.equals("")) {

                    // When user login, check if we have already a user instance for him in our
                    // database, if not then create one for him
                    // (i.e. this will only be the case once, upon first login)
                    lifecycleScope.launch {
                        Firebase.auth.currentUser?.also {
                            if (!FirebaseDB.userExists(it.uid)) {
                                val user = User(it.uid,  0, it.displayName.orEmpty(),
                                    email = it.email.orEmpty(), phone = it.phoneNumber.orEmpty())
                                FirebaseDB.addUser(user, it.uid)
                            }
                        }
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    // Here I will hide the progress bar when implemented
                    finish()
                }
            }
    }

}