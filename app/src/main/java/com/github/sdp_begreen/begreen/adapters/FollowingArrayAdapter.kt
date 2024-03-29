package com.github.sdp_begreen.begreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// This adapter is responsible for displaying the users that matches a string written by the User
// This adapter also lets User follow other users by clicking the follow button
class FollowingArrayAdapter(context: Context,
                            @LayoutRes private val resource: Int,
                            private val users: List<User>,
                            private val db: DB,
                            private val auth: Auth,
                            private val lifeCycle: LifecycleCoroutineScope,
                            following: List<Boolean>,
                            private val connectedUser: StateFlow<User?>)

    : ArrayAdapter<User>(context, resource, users) {

    // The boolean at index i is "true" <=> the current user follows user at index i in [users]
    private val followingUsers = following.toMutableList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View{

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.user_search_bar, parent, false)
        val textView = view.findViewById<TextView>(R.id.item_text)
        val user = getItem(position)
        user?.let {
            textView.text = it.toString()
            followButtonClickListener(view, users.indexOf(it))
        }

        return view
    }



    // Sets up the actions to make when the user clicks on the follow/unfollow button
    // and initializes the icon of the button
    private fun followButtonClickListener(view: View, position: Int){

        val button = view.findViewById<MaterialButton>(R.id.item_button)
        // Change the icon if the logged in user already follows this user
        val icon = if (followingUsers[position]) R.drawable.baseline_person_add_disabled_24
                   else R.drawable.baseline_person_add_24
        button.icon = ContextCompat.getDrawable(context, icon)

        button.setOnClickListener {
            auth.getConnectedUserId()?.let {curUserId ->
                users[position].let { user ->
                    switchButtonIconAndSendToDB(button, followingUsers[position], curUserId, user.id)
                    followingUsers[position] = !followingUsers[position]
                }
            }

        }
    }

    private fun switchButtonIconAndSendToDB(button: MaterialButton, isFollowing: Boolean,
                                            followerId: String, followedId: String){

        // Changes the icon of the follow button and commits changes to the db
        if (isFollowing) {
            // If the current user already follows this user, unfollow it
            button.icon = ContextCompat.getDrawable(context, R.drawable.baseline_person_add_24)
            lifeCycle.launch { db.unfollow(followerId, followedId) }
        } else {
            // If the current user does not already follow this user, follow it
            button.icon = ContextCompat.getDrawable(context, R.drawable.baseline_person_add_disabled_24)
            lifeCycle.launch { db.follow(followerId, followedId) }
        }

        // Update current user for consistency.
        connectedUser.value?.also {

            if (isFollowing) {
                it.unfollow(followedId)
            } else {
                it.follow(followedId)
            }
        }
    }
}