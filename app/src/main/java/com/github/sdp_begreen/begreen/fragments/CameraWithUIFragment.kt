package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.SharePostActivity
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread


class CameraWithUIFragment : Fragment() {
    // Get the db instance
    private val db by inject<DB>()
    private val auth by inject<Auth>()
    private val connectedUserViewModel by viewModels<ConnectedUserViewModel>()
    private lateinit var outputDirectory: File
    private var viewFinder = view?.findViewById<PreviewView>(R.id.viewFinder)
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_with_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFinder = view.findViewById(R.id.viewFinder)
        initView()
    }


    private fun initView() {
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        // Set up the listener for take photo button
        view?.findViewById<Button>(R.id.camera_capture_button).also {
            it?.setOnClickListener {
                takePhoto()
            }
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        handleClicks()
        setUpProfileBtn()
        setUpCancel()
        setUpShare()
        lifecycleScope.launch { setUpSearchBar() }
        preparePhoto()
    }

    private fun setUpCancel(){
        val cancelBtn = view?.findViewById<ImageView>(R.id.cancel_post)
        cancelBtn?.setOnClickListener {
            preparePhoto()
        }
    }

    private fun setUpShare(){
        val shareBtn = view?.findViewById<ImageView>(R.id.send_post)
        shareBtn?.setOnClickListener {
            view?.findViewById<TextInputEditText>(R.id.post_description).also {
                val description = it?.text.toString()
                val user = connectedUserViewModel.currentUser.value
                val date = ParcelableDate(Date())

                val photoMetadata = PhotoMetadata("photo?.id", description, date, user?.id, "", description)
            }
            val msg = "Photo sent successfully"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }
    private suspend fun setUpSearchBar() {

        val users = db.getAllUsers()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, users)
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
                    search?.requestFocus()
                    imm?.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
    }

    private fun setUpProfileBtn() {
        val profileBtn = view?.findViewById<ImageView>(R.id.profile_cam)
        profileBtn?.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            runBlocking {
                transaction.replace(R.id.mainFragmentContainer, getProfile())
            }
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    private suspend fun getProfile() : Fragment {
        val photos = listOf(
            PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()), "0", "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"), PhotoMetadata("1","Look at me cleaning!", ParcelableDate(
                Date()
            ), "0", "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!")
        )
        return (connectedUserViewModel.currentUser.value?.let {
            ProfileDetailsFragment.newInstance(it, photos)
        } ?: auth.getConnectedUserId().let { db.getUser(it!!) }
            ?.let { ProfileDetailsFragment.newInstance(it, photos) })!!
    }

    private fun handleClicks(){
        //on click switch camera
        view?.findViewById<ImageView>(R.id.img_switch_camera).also {
            it?.setOnClickListener {
                if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA) lensFacing = CameraSelector.DEFAULT_BACK_CAMERA;
                else if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA;
                startCamera()
            }
        }
    }

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
                    preparePost()
                    //the uri of photo captured here
                    val savedUri = Uri.fromFile(photoFile)
                    Picasso.Builder(requireContext()).build().load(savedUri).into(view?.findViewById(R.id.preview))
                    val msg = "Photo capture successfully"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun preparePost() {
        view?.findViewById<ImageView>(R.id.img_switch_camera).also {
            it?.visibility = View.GONE
        }
        view?.findViewById<ImageView>(R.id.profile_cam).also {
            it?.visibility = View.GONE
        }
        view?.findViewById<ImageView>(R.id.search_cam).also {
            it?.visibility = View.GONE
        }
        view?.findViewById<Button>(R.id.camera_capture_button).also {
            it?.visibility = View.GONE
        }
        view?.findViewById<PreviewView>(R.id.viewFinder).also {
            it?.visibility = View.GONE
        }
        view?.findViewById<ConstraintLayout>(R.id.post_background).also {
            it?.visibility = View.VISIBLE
        }

    }

    private fun preparePhoto() {
        view?.findViewById<ImageView>(R.id.img_switch_camera).also {
            it?.visibility = View.VISIBLE
        }
        view?.findViewById<ImageView>(R.id.profile_cam).also {
            it?.visibility = View.VISIBLE
        }
        view?.findViewById<ImageView>(R.id.search_cam).also {
            it?.visibility = View.VISIBLE
        }
        view?.findViewById<Button>(R.id.camera_capture_button).also {
            it?.visibility = View.VISIBLE
        }
        view?.findViewById<PreviewView>(R.id.viewFinder).also {
            it?.visibility = View.VISIBLE
        }
        view?.findViewById<ConstraintLayout>(R.id.post_background).also {
            it?.visibility = View.GONE
        }

    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

}