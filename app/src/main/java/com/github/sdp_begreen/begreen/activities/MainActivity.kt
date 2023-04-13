package com.github.sdp_begreen.begreen.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.fragments.*
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.social.GoogleAuth.mGoogleSignInClient
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private val connectedUserViewModel: ConnectedUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomBar: BottomNavigationView = findViewById(R.id.mainNavigationView)
        val drawerLayout: DrawerLayout = findViewById(R.id.mainDrawerLayout)
        val navigationView: NavigationView = findViewById(R.id.mainDrawerNavigationView)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CameraFragment>(R.id.mainFragmentContainer)
            }
        }

        val headerView: View = navigationView.getHeaderView(0)
        setupDrawerUserInfo(
            headerView.findViewById(R.id.nav_drawer_profile_picture_imageview),
            headerView.findViewById(R.id.nav_drawer_username_textview),
            headerView.findViewById(R.id.nav_drawer_description_textview))

        // By default select camera
        bottomBar.selectedItemId = R.id.bottomMenuCamera

        bottomBar.setOnItemSelectedListener { item ->
            restoreOutlinedIcon(bottomBar.menu.findItem(bottomBar.selectedItemId))
            handleBottomMenuItemClicked(item, drawerLayout)
            true
        }

        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawerLayout.closeDrawer(GravityCompat.END)
            handleDrawerMenuItemClick(item)
            true
        }
    }

    /**
     * Helper function to setup the user info, description and profile picture in the drawer menu
     *
     * @param imageView The view that contains the image in the drawer
     * @param usernameTW The view that contains the username in the drawer
     * @param descriptionTW The view that contains the description in the drawer
     */
    private fun setupDrawerUserInfo(
        imageView: ImageView, usernameTW: TextView, descriptionTW: TextView
    ) {
        if (connectedUserViewModel.currentUser.value == null) {
            setUpUserNameAndDescription(null, usernameTW, descriptionTW) // call with null to set default values
        }
        if (connectedUserViewModel.currentUserProfilePicture.value == null) {
            imageView.setImageBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.blank_profile_picture))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    connectedUserViewModel.currentUser.collect {
                        setUpUserNameAndDescription(it, usernameTW, descriptionTW)
                    }
                }

                launch {
                    connectedUserViewModel.currentUserProfilePicture.collect {
                        imageView.setImageBitmap(it ?:
                        BitmapFactory.decodeResource(resources, R.drawable.blank_profile_picture))
                    }
                }
            }
        }
    }

    /**
     * Helper method to set the username and the description of a user if it exists
     *
     * @param user The user from whom to display information
     * @param usernameTW The view that contains the username in the drawer
     * @param descriptionTW The view that contains the description in the drawer
     */
    private fun setUpUserNameAndDescription(
        user: User?, usernameTW: TextView, descriptionTW: TextView
    ) {
        usernameTW.text = user?.displayName ?: getString(R.string.nav_drawer_username)
        descriptionTW.text = user?.description ?: getString(R.string.nav_drawer_user_description)
    }

    /**
     * Helper function to replace the main fragment container by the fragment received as parameter
     *
     * @param frag The fragment to use as replacement
     */
    private fun replaceFragInMainContainer(frag: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.mainFragmentContainer, frag)
        }
    }

    /**
     * Helper function to restore outlined icon in bottom menu when other is selected
     *
     * @param item Previously selected menu item
     */
    private fun restoreOutlinedIcon(item: MenuItem) {
        when (item.itemId) {
            R.id.bottomMenuFeed -> {
                item.setIcon(R.drawable.ic_outline_feed)
            }
            R.id.bottomMenuMap -> {
                item.setIcon(R.drawable.ic_outline_map)
            }
            R.id.bottomMenuCamera -> {
                item.setIcon(R.drawable.ic_outline_photo_camera)
            }
            R.id.bottomMenuAdvice -> {
                item.setIcon(R.drawable.ic_outline_lightbulb)
            }
            R.id.bottomMenuUser -> {
                item.setIcon(R.drawable.ic_outline_person)
            }
        }
    }

    /**
     * Helper function to perform the correct action given that a menu item from
     * the bottom menu has been pressed.
     *
     * @param item The menu item that has been pressed
     * @param drawerLayout The drawer layout to interact which in the case of the
     *                      user menu being clicked
     */
    private fun handleBottomMenuItemClicked(item: MenuItem, drawerLayout: DrawerLayout) {
        when (item.itemId) {
            R.id.bottomMenuFeed -> {
                item.setIcon(R.drawable.ic_baseline_feed)
                val photos = listOf(PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()),User("0",100, "SuperUser69"), "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"), PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()),User("0",100, "SuperUser69"), "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"),PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()),User("0",100, "SuperUser69"), "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"),PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()),User("0",100, "SuperUser69"), "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"),PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()),User("0",100, "SuperUser69"), "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"))
                replaceFragInMainContainer(UserPhotoFragment.newInstance(1, photos, true))
            }
            R.id.bottomMenuMap -> {
                item.setIcon(R.drawable.ic_baseline_map)
                replaceFragInMainContainer(MapFragment())
            }
            R.id.bottomMenuCamera -> {
                item.setIcon(R.drawable.ic_baseline_photo_camera)
                replaceFragInMainContainer(CameraFragment())
            }
            R.id.bottomMenuAdvice -> {
                item.setIcon(R.drawable.ic_baseline_lightbulb)
                replaceFragInMainContainer(AdviceFragment())
            }
            R.id.bottomMenuUser -> {
                item.setIcon(R.drawable.ic_baseline_person)
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }
    }

    /**
     * Helper function to perform the correct action given that a menu item from
     * the drawer menu has been pressed
     *
     * @param item The menu item that has been pressed
     */
    private fun handleDrawerMenuItemClick(item: MenuItem) {
        when (item.itemId) {
            R.id.mainNavDrawProfile -> {
                connectedUserViewModel.currentUser.value?.also {
                    val photos = listOf(PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()),User("0",100, "SuperUser69"), "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"), PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()),User("0",100, "SuperUser69"), "Organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"))
                    replaceFragInMainContainer(ProfileDetailsFragment.newInstance(it, photos))
                }
            }
            R.id.mainNavDrawFollowers -> {
                replaceFragInMainContainer(FollowersFragment())
            }
            //------------------------FOR DEMO PURPOSES ONLY------------------------
            //TODO Remove this when demo will be over
            R.id.mainNavDrawUserList -> {
               val photoMetadata = PhotoMetadata("0", "title", ParcelableDate(Date()),User("0", 0, "Lui"), "Profile")
                val desc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed suscipit consectetur ante quis euismod. Morbi tincidunt orci sit amet libero elementum dictum. Quisque blandit ornare vehicula. Pellentesque eget auctor quam. Sed consequat bibendum risus, vitae scelerisque sapien pharetra a. Nullam pulvinar ultrices molestie."
                val userList: List<User> = listOf(
                    User("1",  1, "Alice", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("2",  43, "Bob", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("3",  330, "Charlie", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("4",  13, "Dylan", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("5",  2, "Evan", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("6",  5432, "Frederic", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("7",  35, "Gaelle", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("8",  3, "Hellen", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("9",  33, "Irene", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("10", 23,  "Julianne", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  36, "Kennan", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  14, "Léa", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  845, "Manon", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  376, "Ninon", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  16, "Orianne", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  96, "Pedro", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  4, "Sullivan", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  6, "Valentin", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                    User("1",  8, "Frank", 1, photoMetadata, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                )
                replaceFragInMainContainer(UserFragment.newInstance(1, userList.toCollection(ArrayList()), true))
            }
            //----------------------------------------------------------------------
            R.id.mainNavDrawSettings -> {
                replaceFragInMainContainer(SettingsFragment())
            }
            // handle the "Logout" button in the navigation drawer of the app responsible for
            // logging out a user who has signed in with Google Sign-In
            R.id.mainNavDrawLogout -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

                mGoogleSignInClient.signOut().addOnCompleteListener {
                    val intent = Intent(this, SignInActivity::class.java)

                    // short toast message to the user indicating that they are being logged out
                    Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()

                    // When the sign-out operation is complete, it starts SignInActivity again
                    startActivity(intent)

                    finish()
                }
            }
        }
    }
}