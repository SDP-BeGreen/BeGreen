package com.github.sdp_begreen.begreen.fragments

import android.graphics.BitmapFactory
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.databinding.FragmentUserPhotoBinding
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject
import java.net.URL


/**
 * [RecyclerView.Adapter] that can display a [Photo].
 */
class UserPhotosViewAdapter(
    val photos: List<PhotoMetadata>?,
    private val isFeed: Boolean,
    val lifecycleScope: LifecycleCoroutineScope,
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val photo = photos?.get(position)

        if(isFeed) {

            // Display avatar if on feed
            val drawable = ContextCompat.getDrawable(holder.avatarView.context, R.drawable.ic_baseline_person)
            val defaultAvatar = drawable?.toBitmap()
            // holder.avatarView.setImageBitmap(getFromDB(photo) ?: defaultAvatar)
            holder.avatarView.setImageBitmap( defaultAvatar)

        }else{
            // Do not display avatar if not on feed
            holder.avatarView.visibility = View.GONE
        }

        if (photo is TrashPhotoMetadata) {

            //Set default value
            holder.titleView.text = photo?.caption ?: "No title"
            holder.subtitleView.text = (photo?.takenOn?.toString()
                ?: "Unknown date") + " | " + (photo?.trashCategory?.title
                ?: "No category")

                // TODO : inject database
            lifecycleScope.launch {
                holder.photoView.setImageBitmap(
                    db.getImage(photo)
                )
            }
            //------------FOR DEMO -----------------

            // TODO : to change
            holder.descriptionView.text = photo?.caption
        }
    }

    override fun getItemCount(): Int = photos?.size ?: 0

    inner class ViewHolder(binding: FragmentUserPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val avatarView: ImageView = binding.avatarImage
        val titleView: TextView = binding.titleText
        val subtitleView: TextView = binding.subtitleText
        val photoView: ImageView = binding.mediaImage
        val descriptionView: TextView = binding.supportingText
    }

}