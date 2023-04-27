package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import java.util.Date

private const val ARG_URI = "uri"

class SendPostFragment : Fragment() {
    private var param_uri: String? = null
    private val db by inject<DB>()
    private val connectedUserViewModel: ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param_uri = it.getString(ARG_URI)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        Picasso.Builder(requireContext()).build().load(param_uri).into(view?.findViewById(R.id.preview))
        setUpCancel()
        setUpShare()
    }

    private fun setUpCancel(){
        val cancelBtn = view?.findViewById<ImageView>(R.id.cancel_post)
        cancelBtn?.setOnClickListener {
            returnToCamera()
        }
    }

    private fun returnToCamera() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.detach(this).remove(this)
        runBlocking {
            transaction.replace(R.id.mainFragmentContainer, CameraWithUIFragment.newInstance())
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setUpShare(){
        val shareBtn = view?.findViewById<ImageView>(R.id.send_post)
        shareBtn?.setOnClickListener {
            var metadata : PhotoMetadata? = null
            view?.findViewById<TextInputEditText>(R.id.post_description).also {
                val description = it?.text.toString()
                val user = connectedUserViewModel.currentUser.value
                val date = ParcelableDate(Date())

                metadata = PhotoMetadata("photo?.id", description, date, user?.id, "", description)
            }
            view?.findViewById<ImageView>(R.id.preview)?.drawable?.toBitmap()?.let { bitmap ->
                lifecycleScope.launch {
                    //metadata = metadata?.let { it1 -> db.addImage(bitmap,1, it1) }
                }
            }

            val msg = "Photo sent successfully"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            returnToCamera()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            SendPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URI, param1)
                }
            }
    }
}