package com.github.sdp_begreen.begreen.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.commit
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.databinding.FragmentUserBinding
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.Photo
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [User].
 * TODO: Replace the implementation with code for your data type.
 */
class UserViewAdapter(
    val users: List<User>?, val parentFragmentManager: androidx.fragment.app.FragmentManager?
) : RecyclerView.Adapter<UserViewAdapter.ViewHolder>() {
    //TODO----------------FOR DEMO------------------------
    private val photos = listOf(
        Photo("erfs","Look at me cleaning!", ParcelableDate(Date()),User(0, "SuperUser69",0), "Déchet organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"), Photo("erfs","Look at me cleaning!", ParcelableDate(
            Date()
        ),User(0, "SuperUser69",0), "Déchet organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"), Photo("erfs","Look at me cleaning!", ParcelableDate(
            Date()
        ),User(0, "SuperUser69",0), "Déchet organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"), Photo("erfs","Look at me cleaning!", ParcelableDate(
            Date()
        ),User(0, "SuperUser69",0), "Déchet organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"), Photo("erfs","Look at me cleaning!", ParcelableDate(
            Date()
        ),User(0, "SuperUser69",0), "Déchet organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!")
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
        val user : User = users?.get(position) ?: return
        holder.userScore.text = user.score.toString()
        holder.userName.text = user.displayName
        holder.setListener(holder.itemView, position, user)
    }

    override fun getItemCount(): Int = users?.size ?: 0
    inner class ViewHolder(binding: FragmentUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val userScore: TextView = binding.userFragmentUserNumber
        val userName: TextView = binding.userFragmentContent

        fun setListener(view: View, position: Int, user: User) {
            view.setOnClickListener {
                parentFragmentManager?.commit {
                    setReorderingAllowed(true)
                    replace(R.id.mainFragmentContainer,ProfileDetailsFragment.newInstance(user, photos))
                    addToBackStack(null)
                }
            }
        }
    }
}