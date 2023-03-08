package com.github.sdp_begreen.begreen



import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


class MainActivity : AppCompatActivity() {

    // var for the google image inserted in activity_main.xml
    private lateinit var googleImg: ImageView

    // googleSignInOptions is a variable that is being assigned to the result of this function call.
    // This variable will hold the configured.
    // Source : // https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInOptions
    private lateinit var googleSignInOptions: GoogleSignInOptions

    // GoogleSignInClient object that can be used to initiate the Google sign-in process in an Android app
    // This variable will hold the configured GoogleSignInClient object.
    // Source : https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInClient
    private lateinit var googleSignInClient: GoogleSignInClient

    // Declare a variable to hold the launcher object
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result here
                val data: Intent? = result.data
                handleSignInResult(data)
            }
        }

        // reference to the ImageView that has been defined in the layout activity_main.xml
        googleImg = findViewById(R.id.google)


        // The GoogleSignInOptions object is used to configure the options for signing in with Google
        // in an Android app.
        // GoogleSignInOptions.DEFAULT_SIGN_IN is an option that is specified as the default sign-in option.
        // This tells Google to use the default behavior for signing in, which typically includes asking for the user's email address.
        // requestEmail() is a method that adds a request to the sign-in options to ask for the user's email address.
        // This will allow the app to access the user's email address when they sign in with Google.
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // GoogleSignIn.getClient() is a method that creates a new instance of the GoogleSignInClient class.
        // This client class provides methods for initiating the Google sign-in process and retrieving
        // the user's signed-in account based on the (optional) parameter GoogleSignInOptions.
        // The client can then be used to initiate the Google sign-in process by calling the signInIntent
        // method and starting the resulting intent (see function signIn() below).
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // When the google sign in image is clicked, the google sign in page appears
        googleImg.setOnClickListener {
            signIn()
        }
    }

    private fun signIn(){

        // An Intent is an object that represents an action to be performed or a message to be communicated between
        // components, such as activities, services, and broadcast receivers. It is a way to describe what needs to be
        // done, without knowing exactly which component will do it.
        val intent : Intent = googleSignInClient.signInIntent

        // Use the launcher object to start the activity
        signInLauncher.launch(intent)

    }

    // Define a new function to handle the sign-in result
    private fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // retrieve the result of the sign-in attempt. If the sign-in was successful, it will return a GoogleSignInAccount object.
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                println("account not null")
                // Call the homeActivity() function only if the sign-in was successful
                homeActivity()
            }
        // ApiException is a class provided by the Google Sign-In API that represents an error that occurred during the sign-in process.
        } catch (e: ApiException) {
            println("Error signing in with Google")
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    // function that finishes the current activity and starts a new HomeActivity
    private fun homeActivity() {
        // Finish the current activity
        finish()

        // Create a new intent to start HomeActivity
        val intent = Intent(applicationContext, HomeActivity::class.java)

        // Start the new activity
        startActivity(intent)
    }
}

