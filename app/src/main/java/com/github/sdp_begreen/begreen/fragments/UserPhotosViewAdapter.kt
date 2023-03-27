package com.github.sdp_begreen.begreen.fragments

import android.graphics.Bitmap
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
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.databinding.FragmentUserPhotoBinding
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import java.net.URL


/**
 * [RecyclerView.Adapter] that can display a [Photo].
 * TODO: Replace the implementation with code for your data type.
 */
class UserPhotosViewAdapter(
    private val photos: List<PhotoMetadata>, private val isFeed: Boolean
) : RecyclerView.Adapter<UserPhotosViewAdapter.ViewHolder>() {

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
    private suspend fun getFromDB( photo: PhotoMetadata) : Bitmap? {
        val img = photo.takenBy?.let { user ->
            user.profilePictureMetadata?.let {
                FirebaseDB.getUserProfilePicture(it, user.id)
            }
        }
        return img
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]
        if(isFeed) {
            val drawable = ContextCompat.getDrawable(holder.avatarView.context, R.drawable.ic_baseline_person)
            val defaultAvatar = drawable?.toBitmap()
            //holder.avatarView.setImageBitmap(getFromDB(photo) ?: defaultAvatar)
            holder.avatarView.setImageBitmap( defaultAvatar)
        }else{
            holder.avatarView.visibility = View.GONE
        }
        holder.titleView.text = photo.title
        holder.subtitleView.text = (photo.takenOn?.toString() ?: "Unknown date") + " | " + photo.category
        //holder.photoView.setImageBitmap(photo.getPhotoFromDataBase())
        //TODO------------FOR DEMO -----------------
        val url = URL("https://picsum.photos/400")
        holder.photoView.setImageBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()))
        //------------FOR DEMO -----------------
        holder.descriptionView.text = photo.description
    }

    override fun getItemCount(): Int = photos.size

    inner class ViewHolder(binding: FragmentUserPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val avatarView: ImageView = binding.avatarImage
        val titleView: TextView = binding.titleText
        val subtitleView: TextView = binding.subtitleText
        val photoView: ImageView = binding.mediaImage
        val descriptionView: TextView = binding.supportingText

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }

}