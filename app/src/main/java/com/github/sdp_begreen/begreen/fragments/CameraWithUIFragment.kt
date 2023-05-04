package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.adapters.FollowingArrayAdapter
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraWithUIFragment : Fragment() {
    // Get the db instance
    private val db by inject<DB>()
    private val auth by inject<Auth>()
    private val connectedUserViewModel: ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })
    private lateinit var outputDirectory: File
    private var viewFinder = view?.findViewById<PreviewView>(R.id.previewView)
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_with_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFinder = view.findViewById(R.id.previewView)
        initView()
    }


    /**
     * Initialize the view
     */
    private fun initView() {
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
            setupCameraViewInteractions()
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
               if(it) {
                   startCamera()
                   setupCameraViewInteractions()
               } else {
                   Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
               }
            }.launch(Manifest.permission.CAMERA)

        }
        setUpProfileBtn()
        lifecycleScope.launch { setUpSearchBar() }
    }


    /**
     * Handle the clicks on the search button
     */
    private suspend fun setUpSearchBar() {

        // Get all users and remove currently logged user
        val users = db.getAllUsers().filter { it.id != auth.getConnectedUserId() }

        val following = MutableList(users.size) { false }
        auth.getConnectedUserId()?.let{
            val followingIds = db.getFollowedIds(it)
            // Set to true every user that the current user already follows
            for (i in users.indices){
                if (followingIds.contains(users[i].id))
                    following[i] = true
            }
        }

        val adapter = FollowingArrayAdapter(requireContext(),
            android.R.layout.select_dialog_item, users, db, auth, lifecycleScope, following)

        val searchBtn = view?.findViewById<ImageView>(R.id.search_cam)

        //Getting the instance of AutoCompleteTextView
        view?.findViewById<AutoCompleteTextView>(R.id.userSearch).also {
            // Will start working from first character
            it?.threshold = 1
            // Setting the adapter data into the AutoCompleteTextView
            it?.setAdapter(adapter)
            it?.visibility = View.INVISIBLE
        }

        searchBtn?.setOnClickListener {
            val searchBar = view?.findViewById<AutoCompleteTextView>(R.id.userSearch)
            searchBar.also { search ->
                val imm = getSystemService(requireContext(), InputMethodManager::class.java)
                if(search?.visibility == View.VISIBLE) {
                    search.visibility = View.GONE
                    view?.clearFocus()
                    imm?.hideSoftInputFromWindow(search.windowToken, InputMethodManager.SHOW_IMPLICIT)
                } else {
                    search?.visibility = View.VISIBLE
                    hideKeyboard()
                    search?.requestFocus()
                    imm?.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
    }

    /**
     * Helper function to hide the soft keyboard
     */
    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /**
     * Handle the clicks on the Profile button
     */
    private fun setUpProfileBtn() {
        // Set up the listener for profile button
        val profileBtn = view?.findViewById<ImageView>(R.id.profile_cam)
        profileBtn?.setOnClickListener {
            // Create a transaction to replace the current fragment by the profile fragment
            lifecycleScope.launch {
                requireActivity().supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.mainFragmentContainer, getProfile())
                }
            }
        }
    }

    /**
     * Retrieves the current user profile fragment
     */
    private suspend fun getProfile() : Fragment {
        //TODO remove this after demo
        //_______________________________________________________
        val photos = listOf(
            PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()), "0", "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"), PhotoMetadata("1","Look at me cleaning!", ParcelableDate(
                Date()
            ), "0", "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!")
        )
        //_______________________________________________________
        return (connectedUserViewModel.currentUser.value?.let {
            ProfileDetailsFragment.newInstance(it, photos)
        } ?: auth.getConnectedUserId().let { db.getUser(it!!) }
            ?.let { ProfileDetailsFragment.newInstance(it, photos) })!!
    }

    /**
     *  Handles the clicks on the switch button
     */
    private fun setUpSwitch(){
        //on click switch camera
        view?.findViewById<ImageView>(R.id.img_switch_camera).also {
            it?.setOnClickListener {
                // Flip between front and back lens
                if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA)
                    lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
                else if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA)
                    lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA
                startCamera()
            }
        }
    }

    /**
     * Handle the capture of a photo
     */
    private fun takePhoto(){

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        setupPictureListener(imageCapture, photoFile, outputOptions)

    }

    /**
     * Setup the listener for the picture capture
     */
    private fun setupPictureListener(imageCapture: ImageCapture, photoFile: File, outputOptions: ImageCapture.OutputFileOptions){
        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    //the uri of photo captured here
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture successfully"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    // Create a transaction to replace the current fragment by the send post fragment
                   lifecycleScope.launch {
                        parentFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(R.id.mainCameraFragmentContainer, SendPostFragment.newInstance(savedUri.toString()))
                        }
                    }
                }
            })

    }

    /**
     * Get the output directory
     */
    private fun getOutputDirectory(): File {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    /**
     * Start the camera
     */
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder?.surfaceProvider)
                }
            viewFinder?.scaleType = PreviewView.ScaleType.FILL_CENTER

            imageCapture = ImageCapture.Builder()
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this, lensFacing, imageCapture, preview)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))

    }

    /**
     * Setup the camera view interactions
     */
    private fun setupCameraViewInteractions() {
        view?.findViewById<Button>(R.id.camera_capture_button).also {
            it?.setOnClickListener {
                takePhoto()
            }
        }

        setUpSwitch()
        // Set up the listener for take photo button
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    /**
     * Check if all permissions are granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    /**
     * Companion object to create the fragment
     */
    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = listOf(Manifest.permission.CAMERA)

        @JvmStatic
        fun newInstance() = CameraWithUIFragment()
    }
}