package com.github.sdp_begreen.begreen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.PhotoMetadata

class FeedViewAdapter :
    PagingDataAdapter<PhotoMetadata, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        /**
         * comparator for the recyclerview
         */
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<PhotoMetadata>() {
            override fun areItemsTheSame(oldItem: PhotoMetadata, newItem: PhotoMetadata): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: PhotoMetadata, newItem: PhotoMetadata): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? FeedViewHolder)?.bind(item = getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FeedViewHolder.getInstance(parent)
    }

    /**
     * View holder class
     * @param view (View) : view to be inflated
     */
    class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {

            /**
             * function to get the instance of the view holder
             * @param parent (ViewGroup) : parent view
             * @return (FeedViewHolder) : instance of the view holder
             */
            fun getInstance(parent: ViewGroup): FeedViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.fragment_user_photo, parent, false)
                return FeedViewHolder(view)
            }
        }

        val avatarView: ImageView = view.findViewById(R.id.avatar_image)
        val titleView: TextView = view.findViewById(R.id.title_text)
        val subtitleView: TextView = view.findViewById(R.id.subtitle_text)
        val photoView: ImageView = view.findViewById(R.id.media_image)
        val descriptionView: TextView = view.findViewById(R.id.supporting_text)


        /**
         * function to bind the data to the view
         * @param item (PhotoMetadata?) : data to be bound
         */
        fun bind(item: PhotoMetadata?) {
            //loads image from network using coil extension function
            photoView.load(item) {
                placeholder(R.drawable.blank_profile_picture)
            }
        }
    }


}