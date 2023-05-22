package com.github.sdp_begreen.begreen.fragments

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.databinding.FragmentUserPhotoBinding
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


/**
 * [RecyclerView.Adapter] that can display a [Photo].
 */
class UserPhotosViewAdapter(
    val photos: List<TrashPhotoMetadata>?,
    private val isFeed: Boolean,
    val lifecycleScope: LifecycleCoroutineScope,
    val resources: Resources
) : RecyclerView.Adapter<UserPhotosViewAdapter.ViewHolder>() {

    private val db by inject<DB>(DB::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //TODO-------------------FOR DEMO---------------------
        val policy = ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        //-------------------FOR DEMO---------------------
        return ViewHolder(
            FragmentUserPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Prefer this semantic instead of the .let { } so we don't have to embed too much blocks

        val photos = photos ?: return
        val photo = photos[position]
        val userId = photo.takenBy ?: return

        lifecycleScope.launch {

            val user = db.getUser(userId) ?: return@launch

            if (isFeed) {

                holder.avatarMaskView.visibility = View.VISIBLE

                // Display avatar if on feed
                user.profilePictureMetadata?.also {
                    val avatarImage = db.getUserProfilePicture(it, userId)
                    holder.avatarView.setImageBitmap(avatarImage)
                }

            } else {

                // Do not display avatar if not on feed
                holder.avatarMaskView.visibility = View.GONE
            }

            // Display post content
            val dateString = (photo.takenOn?.toString() ?: resources.getString(R.string.unknown_date))
            val categoryString = (photo.trashCategory?.title ?: resources.getString(R.string.no_category))

            holder.titleView.text = user.displayName ?: resources.getString(R.string.unknown_user)
            holder.subtitleView.text = resources.getString(R.string.post_date_and_category_info, dateString, categoryString)
            holder.descriptionView.text = photo.caption
            holder.photoView.setImageBitmap(db.getImage(photo))
        }
    }

    override fun getItemCount(): Int = photos?.size ?: 0

    inner class ViewHolder(binding: FragmentUserPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val avatarMaskView: CardView = binding.avatarMask
        val avatarView: ImageView = binding.avatarImage
        val titleView: TextView = binding.titleText
        val subtitleView: TextView = binding.subtitleText
        val photoView: ImageView = binding.mediaImage
        val descriptionView: TextView = binding.supportingText
    }

}