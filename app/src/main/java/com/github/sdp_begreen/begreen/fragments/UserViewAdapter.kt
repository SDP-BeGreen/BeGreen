package com.github.sdp_begreen.begreen.fragments

import android.nfc.tech.NfcB
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.User
import com.github.sdp_begreen.begreen.databinding.FragmentUserBinding


/**
 * [RecyclerView.Adapter] that can display a [User].
 * TODO: Replace the implementation with code for your data type.
 */
class UserViewAdapter(
     val users: List<User>?, val parentFragmentManager: androidx.fragment.app.FragmentManager?
) : RecyclerView.Adapter<UserViewAdapter.ViewHolder>() {
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
        val user : User = users?.get(position) ?: return
        holder.idView.text = user.score.toString()
        holder.contentView.text = user.name
        holder.setListener(holder.itemView, position, user)
    }

    override fun getItemCount(): Int = users?.size ?: 0
    inner class ViewHolder(binding: FragmentUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        fun setListener(view: View, position: Int, user: User) {
            view.setOnClickListener {
                parentFragmentManager?.commit {
                    setReorderingAllowed(true)
                    replace<ProfileDetailsFragment>(R.id.mainFragmentContainer,"", Bundle().apply {
                        putParcelable(ARG_USER, user)
                    })
                    addToBackStack(null)
                }
            }
        }
    }
}