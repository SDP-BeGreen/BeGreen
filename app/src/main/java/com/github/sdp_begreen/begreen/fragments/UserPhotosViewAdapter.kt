package com.github.sdp_begreen.begreen.fragments

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.sdp_begreen.begreen.databinding.FragmentUserPhotoBinding
import com.github.sdp_begreen.begreen.models.Photo

/**
 * [RecyclerView.Adapter] that can display a [Photo].
 * TODO: Replace the implementation with code for your data type.
 */
class UserPhotosViewAdapter(
    private val photos: List<Photo>, private val isFeed: Boolean
) : RecyclerView.Adapter<UserPhotosViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentUserPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]
        if(isFeed) {
            holder.avatarView.setImageBitmap(
                photo.takenBy?.avatar ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            )
        }
        holder.titleView.text = photo.title
        holder.subtitleView.text = (photo.takenOn?.toString() ?: "Unknown date") + " Category: " + photo.category
        holder.photoView.setImageBitmap(photo.getPhotoFromDataBase())
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