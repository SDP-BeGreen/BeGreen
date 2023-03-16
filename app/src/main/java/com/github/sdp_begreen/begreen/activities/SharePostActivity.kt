package com.github.sdp_begreen.begreen.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.entities.Post

class SharePostActivity : AppCompatActivity() {

    lateinit var imageView : ImageView;
    private lateinit var title : EditText;
    private lateinit var shareBtn : Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_post)

        imageView = findViewById(R.id.imageView);
        title = findViewById(R.id.editTextTextPersonName);
        shareBtn = findViewById(R.id.shareBtn)

        // Display the picture that has been taken (given as a parameter from the caller activity)
        val bitmap : Bitmap = intent.getParcelableExtra<Bitmap>("image") as Bitmap
        imageView.setImageBitmap(bitmap)

        // Configure the share action
        /*shareBtn.setOnClickListener {
            sharePost()
        }*/
    }

    fun getPost() : Post {

        val title : String = title.text.toString()
        val image : Bitmap = imageView.drawable.toBitmap()

        val post = Post(title, image)

        return post
    }

    /**
     * Share the post to the database
     */
    /*fun sharePost() {

        val post = getPost()

        //TODO
    }*/
}
