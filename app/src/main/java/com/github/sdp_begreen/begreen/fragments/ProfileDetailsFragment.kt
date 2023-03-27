package com.github.sdp_begreen.begreen.fragments

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.Actions
import com.github.sdp_begreen.begreen.models.Photo
import com.github.sdp_begreen.begreen.models.User
import kotlinx.coroutines.launch
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileDetailsFragment : Fragment() {
    var user: User? = null
    var recentPosts: List<Photo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_USER, User::class.java)
            }else {
                it.getParcelable(ARG_USER)
            }
            recentPosts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelableArrayList(ARG_RECENT_POSTS, Photo::class.java)
            } else {
                it.getParcelableArrayList(ARG_RECENT_POSTS)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_profile_details, container, false)
        val name = view.findViewById(R.id.fragment_profile_details_profile_name) as TextView
        val rating = view.findViewById(R.id.fragment_profile_details_profile_rating) as RatingBar
        val profileImgView = view.findViewById(R.id.fragment_profile_details_profile_image) as ImageView
        val profileDescription = view.findViewById(R.id.fragment_profile_details_profile_description) as TextView
        val profilePhone = view.findViewById(R.id.fragment_profile_details_profile_phone) as TextView
        val profileEmail = view.findViewById(R.id.fragment_profile_details_profile_email) as TextView
        val userTextLevel= view.findViewById(R.id.fragment_profile_details_level) as TextView
        val userProgressBar = view.findViewById(R.id.fragment_profile_details_user_progress) as ProgressBar
        val followButton = view.findViewById(R.id.fragment_profile_details_follow_button) as Button

        name.text = user?.name
        rating.rating = user?.score?.toFloat() ?: 0.0f
        val drawable = ContextCompat.getDrawable(inflater.context, R.drawable.ic_baseline_person)
        val defaultAvatar =
            drawable?.toBitmap()?.let { Bitmap.createScaledBitmap(it, 500, 500, false) }
        profileImgView.setImageBitmap(defaultAvatar)
        profileDescription.text = user?.description
        profilePhone.text = user?.phone
        profileEmail.text = user?.email
        userTextLevel.text = getString(
            R.string.user_details_level_text, user?.name ?: "Default User",
        )
        userProgressBar.progress = user?.progression ?: 0

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_recent_profile_photo, UserPhotoFragment.newInstance(1, recentPosts, false), "")
            ?.commit()

        followButton.setOnClickListener {
            if(followButton.text == Actions.FOLLOW.text) {
                followButton.text = Actions.UNFOLLOW.text
                lifecycleScope.launch {
                    //TODO : add currentUser
                    user?.addFollower(User.currentUser)
                }
            }else{
                followButton.text = Actions.FOLLOW.text
                lifecycleScope.launch {
                    //user?.removeFollower(User.currentUser)
                }
            }

        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param user user to show details.
         * @param recentPosts recent posts of the user.
         * @return A new instance of fragment ProfileDetailsFragment.
         */
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_USER = "USER"
        private const val ARG_RECENT_POSTS = "recent_posts"
        @JvmStatic
        fun newInstance(user: User, photos: List<Photo>) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                    putParcelableArrayList(ARG_RECENT_POSTS, photos.toCollection(ArrayList()))
                }
            }
    }
}