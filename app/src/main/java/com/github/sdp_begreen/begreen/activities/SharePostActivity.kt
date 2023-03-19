package com.github.sdp_begreen.begreen.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.github.sdp_begreen.begreen.R

class SharePostActivity : AppCompatActivity() {

    private lateinit var imageView : ImageView;
    private lateinit var title : EditText;
    private lateinit var shareBtn : Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_post)

        imageView = findViewById(R.id.postImageView);
        title = findViewById(R.id.postTitleEditText);
        shareBtn = findViewById(R.id.shareBtn)

        // Display the picture that has been taken (given as a parameter from the caller activity)
        val bitmap : Bitmap? = intent.getParcelableExtra<Bitmap>(AddNewPostActivity.EXTRA_IMAGE_BITMAP)  as Bitmap?

        if (intent.hasExtra(AddNewPostActivity.EXTRA_IMAGE_BITMAP) && bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            throw IllegalArgumentException("Intent does not contain Bitmap extra")
        }

        // Configure the share action
        /*shareBtn.setOnClickListener {
            sharePost()
        }*/
    }

    /*

    // TODO this method will be called by sharePost() method

    private fun getPost() : Post {

        val title : String = title.text.toString()
        val image : Bitmap = imageView.drawable.toBitmap()

        val post = Post(title, image)

        return post
    }
     */

    /**
     * Share the post to the database
     */
    /*fun sharePost() {

        val post = getPost()

        //TODO
    }*/
}
