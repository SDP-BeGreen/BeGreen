package com.github.sdp_begreen.begreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

// This adapter is responsible for displaying the users that matches a string written by the User
// This adapter also lets User follow other users by clicking the follow button
class FollowingArrayAdapter(context: Context,
                            @LayoutRes private val resource: Int,
                            users: List<User>,
                            private val db: DB,
                            private val auth: Auth,
                            private val lifeCycle: LifecycleCoroutineScope,
                            following: List<Boolean> = List(users.size) { false })
    : ArrayAdapter<User>(context, resource, users) {

    // The boolean at index i is "true" <=> the current user follows user at index i in [users]
    private val followingUsers = following.toMutableList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.user_search_bar, parent, false)
        val textView = view.findViewById<TextView>(R.id.item_text)
        textView.text = getItem(position).toString()

        followButtonClickListener(view, position)

        return view
    }



    // Sets up the actions to make when the user clicks on the follow/unfollow button
    private fun followButtonClickListener(view: View, position: Int){
        val button = view.findViewById<MaterialButton>(R.id.item_button)

        button.setOnClickListener {
            auth.getConnectedUserId()?.let {curUserId ->
                getItem(position)?.let { user ->
                    switchButtonIconAndSendToDB(button, followingUsers[position], curUserId, user.id)
                    followingUsers[position] = !followingUsers[position]
                }
            }

        }
    }

    // Changes the icon of the button and commits changes to the db
    private fun switchButtonIconAndSendToDB(button: MaterialButton, isFollowing: Boolean,
                                            followerId: String, followedId: String){
        if (isFollowing) {
            // If the current user already follows this user, unfollow it
            button.icon = ContextCompat.getDrawable(context, R.drawable.baseline_person_add_24)
            lifeCycle.launch { db.unfollow(followerId, followedId) }
        } else {
            // If the current user does not already follow this user, follow it
            button.icon = ContextCompat.getDrawable(context, R.drawable.baseline_person_add_disabled_24)
            lifeCycle.launch { db.follow(followerId, followedId) }
        }
    }
}