package com.github.sdp_begreen.begreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class HomeActivity : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var logout: Button
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find views by their IDs and assign them to local variables
        name=findViewById(R.id.name)
        email=findViewById(R.id.email)
        logout=findViewById(R.id.logout)

        // Create GoogleSignInOptions object to specify the options for the sign-in process
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Create a GoogleSignInClient object to interact with the Google Sign-In API
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Check if the user is already signed in, and if so, retrieve their name and email
        val account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null){
            val nameAccount : String? = account.displayName
            val emailAccount : String? = account.email

            name.text = nameAccount
            email.text = emailAccount
        }

        // When the button is clicked, the signOut method is called to sign out the user
        logout.setOnClickListener{
                signOut()
        }
    }

    // This ensures that the user is signed out of the app and taken back to the login screen (i.e., MainActivity)
    // after they click the logout button in the HomeActivity.
    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                // Handle sign-out error
                Toast.makeText(this, "Sign out failed", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener(this) { exception ->
            // Handle sign-out exception
            Toast.makeText(this, "Sign out failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}