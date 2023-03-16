package com.github.sdp_begreen.begreen.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.Photo
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
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
                replaceFragInMainContainer(FeedFragment())
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
                replaceFragInMainContainer(ProfileFragment())
            }
            R.id.mainNavDrawFollowers -> {
                replaceFragInMainContainer(FollowersFragment())
            }
            R.id.mainNavDrawUserList -> {
                replaceFragInMainContainer(UserFragment().apply {
                    arguments = Bundle().apply {
                        //TODO delete that
                        //----------------------FOR DEMO PURPOSES ONLY----------------------
                        val photo = Photo("erfs", ParcelableDate(Date()),User(0, "Lui",0), "Profile")
                        val desc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed suscipit consectetur ante quis euismod. Morbi tincidunt orci sit amet libero elementum dictum. Quisque blandit ornare vehicula. Pellentesque eget auctor quam. Sed consequat bibendum risus, vitae scelerisque sapien pharetra a. Nullam pulvinar ultrices molestie."
                        val userList: List<User> = listOf(
                            User(1, "Alice", 1, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(2, "Bob", 43, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(3, "Charlie", 330, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(4, "Dylan", 13, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(5, "Evan", 2, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(6, "Frederic", 5432, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(7, "Gaelle", 35, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(8, "Hellen", 3, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(9, "Irene", 33, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(10, "Julianne", 23, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Kennan", 36, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Léa", 14, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Manon", 845, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Ninon", 376, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Orianne", 16, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Pedro", 96, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Sullivan", 4, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Valentin", 6, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                            User(1, "Frank", 8, 1, photo, desc, "cc@gmail.com", "08920939459802", 67, null, null),
                        )
                        //----------------------FOR DEMO PURPOSES ONLY----------------------
                        putInt(UserFragment.ARG_COLUMN_COUNT, 1)
                        putParcelableArrayList(UserFragment.ARG_USER_LIST, userList.toCollection(ArrayList()))
                        putBoolean(UserFragment.ARG_IS_LIST_SORTED_BY_SCORE, true)
                    }
                })
            }
            R.id.mainNavDrawSettings -> {
                replaceFragInMainContainer(SettingsFragment())
            }
        }
    }
}