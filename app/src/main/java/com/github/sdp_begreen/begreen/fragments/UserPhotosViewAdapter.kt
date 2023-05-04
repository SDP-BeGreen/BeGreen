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
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.databinding.FragmentUserPhotoBinding
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import java.net.URL


/**
 * [RecyclerView.Adapter] that can display a [Photo].
 */
class UserPhotosViewAdapter(
    val photos: List<TrashPhotoMetadata>?,
    private val isFeed: Boolean
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos?.get(position)
        if(isFeed) {
            //Display avatar if on feed
            val drawable = ContextCompat.getDrawable(holder.avatarView.context, R.drawable.ic_baseline_person)
            val defaultAvatar = drawable?.toBitmap()
            //holder.avatarView.setImageBitmap(getFromDB(photo) ?: defaultAvatar)
            holder.avatarView.setImageBitmap( defaultAvatar)
        }else{
            //Donot display avatar if not on feed
            holder.avatarView.visibility = View.GONE
        }
        //Set default value
        holder.titleView.text = photo?.caption ?: "No title"
        holder.subtitleView.text = (photo?.takenOn?.toString() ?: "Unknown date") + " | " + (photo?.trashCategory?.title
            ?: "No category")
        //holder.photoView.setImageBitmap(photo.getPhotoFromDataBase())
        //TODO------------FOR DEMO -----------------
        val url = URL("https://picsum.photos/400")
        holder.photoView.setImageBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()))
        //------------FOR DEMO -----------------

        // TODO : to change
        holder.descriptionView.text = photo?.caption
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