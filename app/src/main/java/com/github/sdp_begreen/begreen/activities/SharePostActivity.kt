package com.github.sdp_begreen.begreen.activities

import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.fragments.CameraFragment
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.firebase.FirebaseAuth
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import kotlinx.coroutines.launch

class SharePostActivity : AppCompatActivity() {

    private lateinit var postImageView : ImageView
    private lateinit var postTitleEditText : EditText
    private lateinit var sharePostBtn : Button


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
        postTitleEditText = findViewById(R.id.postTitleEditText)
        sharePostBtn = findViewById(R.id.sharePostBtn)
        postImageView = findViewById(R.id.postImageView)

        // Display the post image.
        displayPostImage()

        // Setup the share button action
        sharePostBtn.setOnClickListener {
            lifecycleScope.launch {
                sharePost()
            }
        }
    }

    /**
     * Helper function to return the image post which is an extra of the intent
     */
    private fun getPostImage() : Bitmap? {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.extras?.getParcelable(CameraFragment.EXTRA_IMAGE_BITMAP, Bitmap::class.java)

        } else {
            @Suppress("DEPRECATION")
            return intent.extras?.get(CameraFragment.EXTRA_IMAGE_BITMAP) as? Bitmap
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
     * Share the post to the database
     */
    private suspend fun sharePost() {

        // "image" is non-null because we already checked it during the activity launching. So we can force the casting.
        // In other words, (image == null) is an unreachable path

        val title: String = getPostTitle()
        val date = ParcelableDate.now
        val trashCategory: TrashCategory = TrashCategory.PLASTIC
        val userId = FirebaseAuth().getConnectedUserId()

        val image: Bitmap = getPostImage()!!
        val photoMetadata = TrashPhotoMetadata(null, date, userId, title, trashCategory)

        // Check if the post has correctly been shared
        val hasBeenShared = (FirebaseDB.addImage(image, photoMetadata) != null)

        if (hasBeenShared) {
            finish()

        } else {
            Toast.makeText(this, R.string.post_cannot_be_shared_error, Toast.LENGTH_SHORT).show()
        }
    }
}
