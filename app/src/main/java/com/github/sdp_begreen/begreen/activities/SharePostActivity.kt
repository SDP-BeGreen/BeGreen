package com.github.sdp_begreen.begreen.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.entities.Post

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
    private fun getPostImage() : Bitmap {

        val image = intent.extras?.get(AddNewPostActivity.EXTRA_IMAGE_BITMAP) as? Bitmap

        if (image != null) {

            return image
        }

        throw java.lang.IllegalArgumentException()
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

        var image : Bitmap

        // Exception thrown while launching the activity cannot be caught by UI tests.
        // So we prevent them during activity launching
        try {
            image = getPostImage()
        } catch (e : IllegalArgumentException) {

            // If there is no image as extra, display a fake image. Anyway the share button will
            // prevent the user from sharing the image.
            image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        }

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

        //Snackbar.make(sharePostBtn, POST_SHARED_MESSAGE, Snackbar.LENGTH_SHORT).show()
    }
}
