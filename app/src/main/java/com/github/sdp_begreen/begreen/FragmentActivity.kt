package com.github.sdp_begreen.begreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.replace
import androidx.fragment.app.commit
import com.github.sdp_begreen.begreen.fragments.FavoriteFragment
import com.github.sdp_begreen.begreen.fragments.HomeFragment
import com.github.sdp_begreen.begreen.fragments.MailFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class FragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<HomeFragment>(R.id.fragmentContainerView)
                //add( R.id.fragmentContainerView, HomeFragment.newInstance("123", "345"))
            }
        }

        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)

        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.close()
            Log.v("menuitem is home", (menuItem.itemId == R.id.item_home).toString())
            Log.v("menuitem is favorit", (menuItem.itemId == R.id.item_favorite).toString())
            Log.v("menuitem is mail", (menuItem.itemId == R.id.item_mail).toString())
            when (menuItem.itemId) {
                R.id.item_home -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<HomeFragment>(R.id.fragmentContainerView)
                    }
                }
                R.id.item_mail -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<MailFragment>(R.id.fragmentContainerView)
                    }
                }
                R.id.item_favorite -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FavoriteFragment>(R.id.fragmentContainerView)
                    }
                }
            }
            true
        }


    }
}