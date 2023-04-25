package com.github.sdp_begreen.begreen.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject
import java.net.URL

class FeedViewAdapter (private val isFeed: Boolean, private val lifecycleScope: LifecycleCoroutineScope) :
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
        return FeedViewHolder.getInstance(parent, lifecycleScope, isFeed)
    }

    /**
     * View holder class
     * @param view (View) : view to be inflated
     */
    class FeedViewHolder(   view: View,
                            private val lifecycleScope: LifecycleCoroutineScope,
                            private val isFeed: Boolean) : RecyclerView.ViewHolder(view) {

        private val db by inject<DB>(FirebaseDB::class.java)

        companion object {

            /**
             * function to get the instance of the view holder
             * @param parent (ViewGroup) : parent view
             * @return (FeedViewHolder) : instance of the view holder
             */
            fun getInstance(parent: ViewGroup, lifecycleScope: LifecycleCoroutineScope, isFeed: Boolean): FeedViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.fragment_user_photo, parent, false)
                return FeedViewHolder(view, lifecycleScope, isFeed)
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
            if(isFeed) {
                //Display avatar if on feed
                val drawable = ContextCompat.getDrawable(itemView.context , R.drawable.ic_baseline_person)
                val defaultAvatar = drawable?.toBitmap()
                //holder.avatarView.setImageBitmap(getFromDB(photo) ?: defaultAvatar)
                avatarView.setImageBitmap( defaultAvatar)
                lifecycleScope.launch {
                    val img = User.currentUser.let { user ->
                        user.profilePictureMetadata?.let { pMetadata ->
                            db.getUserProfilePicture(pMetadata, user.id)
                        }
                    }
                        ?: defaultAvatar
                    avatarView.load(img)
                }
            }
            val url = URL("https://picsum.photos/400")
            //loads image from network using coil extension function
            photoView.load(url) {
                placeholder(R.drawable.blank_profile_picture)
            }
            titleView.text = item?.title
            subtitleView.text =
                (item?.takenOn?.toString() ?: "Unknown date") + " | " + (item?.category ?: "")
            descriptionView.text = item?.description
        }
    }


}