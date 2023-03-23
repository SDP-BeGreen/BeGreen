package com.github.sdp_begreen.begreen.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.Actions
import com.github.sdp_begreen.begreen.models.User
import kotlinx.coroutines.launch




/**
 * A simple [Fragment] subclass.
 * Use the [ProfileDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileDetailsFragment : Fragment() {
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_USER, User::class.java)
            } else {
                it.getParcelable(ARG_USER)
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

        name.text = user?.displayName
        rating.rating = user?.score?.toFloat() ?: 0.0f
        lifecycleScope.launch {
            val img = user?.let { user ->
                user.profilePictureMetadata?.let {
                    FirebaseDB.getUserProfilePicture(it, user.id)
                }
            }
            profileImgView.setImageBitmap(img
                ?: BitmapFactory.decodeResource(resources, R.drawable.blank_profile_picture))
        }
        profileDescription.text = user?.description
        profilePhone.text = user?.phone
        profileEmail.text = user?.email
        userTextLevel.text = getString(
            R.string.user_details_level_text, user?.displayName ?: "Default User",
        )
        userProgressBar.progress = user?.progression ?: 0

        setupFollowListener(followButton)
        return view
    }

    private fun setupFollowListener(followButton: Button) {
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param user user to show details.
         * @return A new instance of fragment ProfileDetailsFragment.
         */
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_USER = "USER"
        @JvmStatic
        fun newInstance(user: User) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}