package com.github.sdp_begreen.begreen.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

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
        bottomBar.selectedItemId = R.id.bottomMenuPhoto

        bottomBar.setOnItemSelectedListener { item ->
            restoreOutlinedIcon(bottomBar.menu.findItem(bottomBar.selectedItemId))
            when(item.itemId) {
                R.id.bottomMenuFeed -> {
                    item.setIcon(R.drawable.ic_baseline_feed)
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FeedFragment>(R.id.mainFragmentContainer)
                    }
                    true
                }
                R.id.bottomMenuMap -> {
                    item.setIcon(R.drawable.ic_baseline_map)
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<MapFragment>(R.id.mainFragmentContainer)
                    }
                    true
                }
                R.id.bottomMenuPhoto -> {
                    item.setIcon(R.drawable.ic_baseline_photo_camera)
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<CameraFragment>(R.id.mainFragmentContainer)
                    }
                    true
                }
                R.id.bottomMenuAdvice -> {
                    item.setIcon(R.drawable.ic_baseline_lightbulb)
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<AdviceFragment>(R.id.mainFragmentContainer)
                    }
                    true
                }
                R.id.bottomMenuUser -> {
                    item.setIcon(R.drawable.ic_baseline_person)
                    drawerLayout.openDrawer(GravityCompat.END)
                    true
                }
                else -> {false}
            }
        }

        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawerLayout.closeDrawer(GravityCompat.END)
            when(item.itemId) {
                R.id.main_nav_draw_profile -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<ProfileFragment>(R.id.mainFragmentContainer)
                    }
                    true
                }
                R.id.main_nav_draw_followers -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FollowersFragment>(R.id.mainFragmentContainer)
                    }
                    true
                }
                R.id.main_nav_draw_settings -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<SettingsFragment>(R.id.mainFragmentContainer)
                    }
                    true
                }
                else -> {false}
            }
        }
    }

    /**
     * Helper function to restore outlined icon in bottom menu when other is selected
     *
     * @param item Previously selected menu item
     */
    private fun restoreOutlinedIcon(item: MenuItem) {
        when(item.itemId) {
            R.id.bottomMenuFeed -> {
                item.setIcon(R.drawable.ic_outline_feed)
            }
            R.id.bottomMenuMap -> {
                item.setIcon(R.drawable.ic_outline_map)
            }
            R.id.bottomMenuPhoto -> {
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
}