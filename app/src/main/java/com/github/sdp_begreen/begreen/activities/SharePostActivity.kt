package com.github.sdp_begreen.begreen.activities

import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.Post

class SharePostActivity : AppCompatActivity() {

    private lateinit var postImageView : ImageView;
    private lateinit var postTitleEditText : EditText;
    private lateinit var sharePostBtn : Button;


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

        // Display the post image.
        displayPostImage()

        // Setup the share button action
        sharePostBtn.setOnClickListener {
            sharePost()
        }
    }

    /**
     * Helper function to return the image post which is an extra of the intent
     */
    private fun getPostImage() : Bitmap? {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.extras?.getParcelable(AddNewPostActivity.EXTRA_IMAGE_BITMAP, Bitmap::class.java)

        } else {
            @Suppress("DEPRECATION")
            return intent.extras?.get(AddNewPostActivity.EXTRA_IMAGE_BITMAP) as? Bitmap
        }
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
    private fun displayPostImage() {

        // If the intent that launched the activity doesn't have the correct extra, "image" will be null.
        // If so, we finish the activity. Otherwise we can display the image.

        val image = getPostImage()

        if (image != null) {
            postImageView.setImageBitmap(image)
        } else {
            finish()
        }
    }

    /**
     * Helper function to get the whole post instance
     */
    private fun getPost() : Post? {

        // "image" is non-null because we already checked it during the activity lauching. So we can force the casting.
        // In other words, (image == null) is an unreachable path

        val title : String = getPostTitle()
        val image : Bitmap = getPostImage()!!

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
    }
}
