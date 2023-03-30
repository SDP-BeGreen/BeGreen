package com.github.sdp_begreen.begreen.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.fragments.*
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private val connectedUserViewModel: ConnectedUserViewModel by viewModels()
    private var drawerInitialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        retrieveUserWithProfilePicture()

        val bottomBar: BottomNavigationView = findViewById(R.id.mainNavigationView)
        val drawerLayout: DrawerLayout = findViewById(R.id.mainDrawerLayout)
        val navigationView: NavigationView = findViewById(R.id.mainDrawerNavigationView)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CameraFragment>(R.id.mainFragmentContainer)
            }
        }

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
     * Helper function to retrieve the connected User along with its profile picture.
     *
     * The purpose of this method is to be called while creating the activity, to prefetch
     * the user and the image from the database, and have them directly loaded and ready to display
     * when opening the drawer menu
     */
    private fun retrieveUserWithProfilePicture() {
        lifecycleScope.launch {
            getConnectedUser()?.also { user ->
                connectedUserViewModel.currentUser.value = user
                connectedUserViewModel.currentUserProfilePicture.value = user.profilePictureMetadata?.let {
                    FirebaseDB.getUserProfilePicture(it, user.id)
                }
            }
        }
    }

    /**
     * Helper function to setup the user info, description and profile picture in the drawer menu
     *
     * If the observable value have already been initialized then simply return
     * avoid setting observe each time we open the drawer
     */
    private fun setupDrawerUserInfo() {
        if (drawerInitialized) return

        drawerInitialized = true
        val imageView: ImageView = findViewById(R.id.nav_drawer_profile_picture_imageview)
        if (connectedUserViewModel.currentUser.value == null) {
            setUpUserNameAndDescription(null) // call with null to set default values
        }
        if (connectedUserViewModel.currentUserProfilePicture.value == null) {
            imageView.setImageBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.blank_profile_picture))
        }

        connectedUserViewModel.currentUser.observe(this) {
            setUpUserNameAndDescription(it)
        }

        connectedUserViewModel.currentUserProfilePicture.observe(this) {
            imageView.setImageBitmap(
                it ?:
                BitmapFactory.decodeResource(resources, R.drawable.blank_profile_picture)
            )
        }
    }

    /**
     * Helper method to get the currently logged in user if it exists
     */
    private suspend fun getConnectedUser(): User? =
        Firebase.auth.currentUser?.uid?.let { FirebaseDB.getUser(it) }

    /**
     * Helper method to set the username and the description of a user if it exists
     */
    private fun setUpUserNameAndDescription(user: User?) {
        findViewById<TextView>(R.id.nav_drawer_username_textview).text =
            user?.displayName ?: getString(R.string.nav_drawer_username)

        findViewById<TextView>(R.id.nav_drawer_description_textview).text =
            user?.description ?: getString(R.string.nav_drawer_user_description)
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
                val adviceList= arrayListOf(
                "Ecology helps us to understand how our actions affect the environment. It shows the individuals the extent of damage we cause to the environment.",
                "With the knowledge of ecology, we are able to know which resources are necessary for the survival of different organisms. Lack of ecological knowledge has led to scarcity and deprivation of these resources, leading to competition.",
                "All organisms require energy for their growth and development. Lack of ecological understanding leads to the over-exploitation of energy resources such as light, nutrition and radiation, leading to its depletion.",
                "Ecology encourages harmonious living within the species and the adoption of a lifestyle that protects the ecology of life.",
                "It focuses on the relationship between humans and the environment. It emphasizes the impact human beings have on the environment and gives knowledge on how we can improve ourselves for the betterment of humans and the environment.",
                "It deals with the study of how organisms alter the environment for the benefit of themselves and other living beings. For eg, termites create a 6 feet tall mound and at the same time feed and protect their entire population.",
                "Ecology plays a significant role in forming new species and modifying the existing ones. Natural selection is one of the many factors that influences evolutionary change.",
                "Ecology was first devised by Ernst Haeckel, a German Zoologist. However, ecology has its origins in other sciences such as geology, biology, and evolution among others.",
                "Habitat ecology is the type of natural environment in which a particular species of an organism live, characterized by both physical and biological features.",
                "An organism free from the interference of other species and can use a full range of biotic and abiotic resources in which it can survive and reproduce is known as its fundamental niche."
                )
                replaceFragInMainContainer(AdviceFragment.newInstance(adviceList))
            }
            R.id.bottomMenuUser -> {
                item.setIcon(R.drawable.ic_baseline_person)
                setupDrawerUserInfo()
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
        }
    }
}