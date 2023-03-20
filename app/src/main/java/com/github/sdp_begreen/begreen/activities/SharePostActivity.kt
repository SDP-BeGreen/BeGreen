package com.github.sdp_begreen.begreen.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.entities.Post
import com.google.android.material.snackbar.Snackbar

class SharePostActivity : AppCompatActivity() {

    private lateinit var postImageView : ImageView;
    private lateinit var postTitleEditText : EditText;
    private lateinit var sharePostBtn : Button;

    companion object {
        const val POST_SHARED_MESSAGE = "Your post is shared !"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_post)

        setupUI()
    }

    /**
     * Helper function to setups ui buttons
     */
    private fun setupUI() {

        // Bind all UIs
        postTitleEditText = findViewById(R.id.postTitleEditText);
        sharePostBtn = findViewById(R.id.sharePostBtn)
        postImageView = findViewById(R.id.postImageView);

        // Display the post image
        val image = getPostImage()
        displayPostImage(image)

        // Setup the share button action
        sharePostBtn.setOnClickListener {
            sharePost()
        }
    }

    /**
     * Helper function to return the image post which is an extra of the intent
     */
    private fun getPostImage() : Bitmap {

        // The case where extras == null is handled in the if statement below where image == null
        val image : Bitmap? = intent.extras?.get(AddNewPostActivity.EXTRA_IMAGE_BITMAP) as Bitmap?

        if (!intent.hasExtra(AddNewPostActivity.EXTRA_IMAGE_BITMAP) || image == null) {
            throw IllegalArgumentException()
        }

        return image
    }

    /**
     * Helper function to return the post title
     */
    private fun getPostTitle() : String {
        return postTitleEditText.text.toString()
    }

    /**
     * Helper function to display the post image
     */
    private fun displayPostImage(image : Bitmap) {
        postImageView.setImageBitmap(image)
    }

    /**
     * Helper function to get the whole post instance
     */
    private fun getPost() : Post {

        val title : String = getPostTitle()
        val image : Bitmap = getPostImage()

        val post = Post(title, image)

        return post
    }

    /**
     * Share the post to the database
     */
    private fun sharePost() {

        val post = getPost()

        // TODO : Change it when the post will be shared to the database. Don't forget to change its test

        finish()

        /*val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        Snackbar.make(sharePostBtn, POST_SHARED_MESSAGE, Snackbar.LENGTH_SHORT).show()*/
    }
}
