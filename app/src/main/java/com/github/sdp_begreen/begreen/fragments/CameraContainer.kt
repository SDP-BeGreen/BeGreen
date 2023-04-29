package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.github.sdp_begreen.begreen.R

class CameraContainer : Fragment() {
    companion object {
        fun newInstance() = CameraContainer()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        childFragmentManager.commit {
            if (savedInstanceState == null) {
                    setReorderingAllowed(true)
                    add<CameraWithUIFragment>(R.id.mainCameraFragmentContainer)
            }
        }
        return inflater.inflate(R.layout.fragment_camera_container, container, false)
    }
}