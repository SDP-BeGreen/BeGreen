package com.github.sdp_begreen.begreen.fragments

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleCoroutineScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.databinding.FragmentUserBinding
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashCategory
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [User].
 */
class UserViewAdapter(
    val users: List<User>?,
    val parentFragmentManager: androidx.fragment.app.FragmentManager?,
    val lifecycleScope: LifecycleCoroutineScope,
    val resources: Resources
) : RecyclerView.Adapter<UserViewAdapter.ViewHolder>() {
    //inject the database
    private val db by inject<DB>(DB::class.java)

    //TODO----------------FOR DEMO------------------------
    private val photos = listOf(

        PhotoMetadata(
            "erfs",
            "Look at me cleaning!",
            ParcelableDate.now,
            "0",
            TrashCategory.PLASTIC,
        ),

        PhotoMetadata(
            "erfs",
            "Look at me cleaning!",
            ParcelableDate.now,
            "0",
            TrashCategory.PLASTIC,
        ),

        PhotoMetadata(
            "erfs",
            "Look at me cleaning!",
            ParcelableDate.now,
            "0",
            TrashCategory.PLASTIC,
        ),

        PhotoMetadata(
            "erfs",
            "Look at me cleaning!",
            ParcelableDate.now,
            "0",
            TrashCategory.PLASTIC,
        ),

        PhotoMetadata(
            "erfs",
            "Look at me cleaning!",
            ParcelableDate.now,
            "0",
            TrashCategory.PLASTIC,
        )
    )

    //----------------FOR DEMO-----------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = users?.get(position) ?: return
        //Set all attributes of the user
        holder.userScore.text = user.score.toString()
        holder.userName.text = user.displayName
        //Set the user's profile picture asynchronously
        holder.userPhoto.let {
            lifecycleScope.launch {
                val img = user.let { user ->
                    user.profilePictureMetadata?.let { pMetadata ->
                        db.getUserProfilePicture(pMetadata, user.id)
                    }
                }
                    ?: BitmapFactory.decodeResource(resources, R.drawable.blank_profile_picture)

                it.setImageBitmap(img)
            }
        }
        //Set the listener for the user to profile details
        holder.setListener(holder.itemView, position, user)
    }

    override fun getItemCount(): Int = users?.size ?: 0

    /**
     * ViewHolder for the user
     * @param binding the binding for the user
     * @return the ViewHolder
     */
    inner class ViewHolder(binding: FragmentUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val userScore: TextView = binding.userFragmentUserNumber
        val userName: TextView = binding.userFragmentContent
        val userPhoto: ImageView = binding.avatarImage

        fun setListener(view: View, position: Int, user: User) {
            view.setOnClickListener {
                parentFragmentManager?.commit {
                    setReorderingAllowed(true)
                    //Go to the profile details fragment
                    replace(
                        R.id.mainFragmentContainer,
                        ProfileDetailsFragment.newInstance(user, photos)
                    )
                    addToBackStack(null)
                }
            }
        }
    }
}