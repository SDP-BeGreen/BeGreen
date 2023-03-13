package com.github.sdp_begreen.begreen.fragments

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
import com.github.sdp_begreen.begreen.User
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_USER = "USER"

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
            }else {
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
        val name: TextView = view?.findViewById(R.id.profile_name) as TextView
        val rating: RatingBar = view?.findViewById(R.id.profile_rating) as RatingBar
        val profileImgView: ImageView = view?.findViewById(R.id.profile_image) as ImageView
        val profileDescription: TextView = view?.findViewById(R.id.profile_description) as TextView
        val profilePhone: TextView = view?.findViewById(R.id.profile_phone) as TextView
        val profileEmail: TextView = view?.findViewById(R.id.profile_email) as TextView
        val userTextLevel: TextView = view?.findViewById(R.id.level) as TextView
        val userProgressBar: ProgressBar = view?.findViewById(R.id.user_progress) as ProgressBar
        val followButton: Button = view?.findViewById(R.id.follow_button) as Button
        name.text = user?.name
        rating.rating = user?.score?.toFloat() ?: 0.0f
        profileImgView.setImageBitmap(user?.img?.getPhotoFromDataBase())
        profileDescription.text = user?.description
        profilePhone.text = user?.phone
        profileEmail.text = user?.email
        userTextLevel.text = user?.name + " level's"
        userProgressBar.progress = user?.progression ?: 0

        followButton.setOnClickListener {
            if(followButton.text == "Follow") {
                followButton.text = "Unfollow"
                lifecycleScope.launch {
                    //TODO : add currentUser
                    user?.addFollower(User.currentUser)
                }
            }else{
                followButton.text = "Follow"
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
         * @return A new instance of fragment ProfileDetailsFragment.
         */
        @JvmStatic
        fun newInstance(user: User) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}