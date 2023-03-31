package com.github.sdp_begreen.begreen.activities

import androidx.core.view.GravityCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.*
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    companion object {
        @BeforeClass
        @JvmStatic fun setup() {
            try {
                Firebase.database.useEmulator("10.0.2.2", 9000)
                Firebase.storage.useEmulator("10.0.2.2", 9199)
                Firebase.auth.useEmulator("10.0.2.2", 9099)
            } catch (_:java.lang.IllegalStateException){}
        }

    }

    @Test
    fun bottomNavigationBarVisible() {
        onView(withId(R.id.mainNavigationView)).check(matches(isDisplayed()))
    }

    @Test
    fun defaultDisplayedFragmentIsCamera() {
        onView(withId(R.id.cameraFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun pressFeedMenuDisplayFeedFragment() {
        onView(withId(R.id.bottomMenuFeed))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.feed_list)).check(matches(isDisplayed()))

        // Go back to camera to test restore outline version of feed menu icon
        // Hard to compare icon in test
        onView(withId(R.id.bottomMenuCamera))
            .perform(click())
    }

    @Test
    fun pressMapMenuDisplayMapFragment() {
        onView(withId(R.id.bottomMenuMap))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))

        onView(withId(R.id.bottomMenuCamera))
            .perform(click())
    }

    @Test
    fun pressCameraMenuDisplayCameraFragment() {
        onView(withId(R.id.bottomMenuCamera))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.cameraFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun pressAdviceMenuDisplayAdviceFragment() {
        onView(withId(R.id.bottomMenuAdvice))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.adviceFragment)).check(matches(isDisplayed()))

        onView(withId(R.id.bottomMenuCamera))
            .perform(click())
    }

    @Test
    fun menuDrawerClosedByDefault() {
        onView(withId(R.id.mainDrawerLayout))
            .check(matches(DrawerMatchers.isClosed()))
    }

    @Test
    fun pressUserMenuOpenDrawer() {
        onView(withId(R.id.bottomMenuUser))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.mainDrawerLayout))
            .check(matches(DrawerMatchers.isOpen(GravityCompat.END)))

        onView(withId(R.id.mainDrawerLayout))
            .perform(DrawerActions.close(GravityCompat.END))

        onView(withId(R.id.bottomMenuCamera))
            .perform(click())
    }

    @Test
    fun pressDrawerMenuProfileDisplayProfileFragment() {
        onView(withId(R.id.mainDrawerLayout)).perform(DrawerActions.open(GravityCompat.END))

        onView(withId(R.id.mainNavDrawProfile))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.profileFragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun pressDrawerMenuFollowersDisplayFollowersFragment() {
        onView(withId(R.id.mainDrawerLayout)).perform(DrawerActions.open(GravityCompat.END))

        onView(withId(R.id.mainNavDrawFollowers))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())
    }

    @Test
    fun pressDrawerMenuUsersDisplayUserFragment() {
        onView(withId(R.id.mainDrawerLayout)).perform(DrawerActions.open(GravityCompat.END))

        onView(withId(R.id.mainNavDrawUserList))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.user_fragment))
            .check(matches(isDisplayed()))
    }


    @Test
    fun pressDrawerMenuSettingsDisplaySettingsFragment() {
        onView(withId(R.id.mainDrawerLayout)).perform(DrawerActions.open(GravityCompat.END))

        onView(withId(R.id.mainNavDrawSettings))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.settingsFragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun pressDrawerMenuLogoutDisplaySignInActivity() {
        Intents.init()
        // Open the navigation drawer
        onView(withId(R.id.mainDrawerLayout)).perform(DrawerActions.open(GravityCompat.END))

        // Click on the Logout button
        onView(withId(R.id.mainNavDrawLogout))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        // Verify that the SignInActivity is opened
        intended(hasComponent(SignInActivity::class.java.name))
        Intents.release()
    }

    // TODO All the following test requiring a comparison of bitmap have been disabled for now
    // TODO Need to find a better way to compare bitmap image

    @Test
    fun correctInfoDisplayedForAuthenticatedUser() {

        runBlocking {
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user1@email.ch", "123456").await()

        }

        onView(withId(R.id.bottomMenuUser))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.nav_drawer_username_textview))
            .check(matches(withText("User Test 1")))

        onView(withId(R.id.nav_drawer_description_textview))
            .check(matches(withText("That's the awesome description of test user 1")))

        /*activityRule.scenario.onActivity {
            val drawable: BitmapDrawable =
                it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable as BitmapDrawable

            val expectedBitmap = BitmapFactory.decodeResource(it.resources, R.drawable.marguerite_test_image)
            assertThat(drawable.bitmap, equalsBitmap(expectedBitmap))
        }*/
    }

    @Test
    fun defaultValueDisplayedForUnauthenticatedUser() {
        Firebase.auth.signOut() // Ensure signed out

        assertThat(Firebase.auth.currentUser, nullValue())

        onView(withId(R.id.bottomMenuUser))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.nav_drawer_username_textview))
            .check(matches(withText("Username")))

        onView(withId(R.id.nav_drawer_description_textview))
            .check(matches((withText("More Info on user"))))

        /*activityRule.scenario.onActivity {
            val drawable: BitmapDrawable =
                it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable as BitmapDrawable

            val expectedBitmap = BitmapFactory.decodeResource(it.resources, R.drawable.blank_profile_picture)
            assertThat(drawable.bitmap, equalsBitmap(expectedBitmap))
        }*/

    }

    @Test
    fun defaultValueDisplayedForAuthenticatedUserNotInDB() {
        runBlocking {
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("notInDb@email.com", "123456").await()
        }

        assertThat(Firebase.auth.currentUser, notNullValue())

        onView(withId(R.id.bottomMenuUser))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.nav_drawer_username_textview))
            .check(matches(withText("Username")))

        onView(withId(R.id.nav_drawer_description_textview))
            .check(matches((withText("More Info on user"))))

        /*activityRule.scenario.onActivity {
            val drawable: BitmapDrawable =
                it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable as BitmapDrawable

            val expectedBitmap = BitmapFactory.decodeResource(it.resources, R.drawable.blank_profile_picture)
            assertThat(drawable.bitmap, equalsBitmap(expectedBitmap))
        }*/
    }

    @Test
    fun defaultProfilePicturesDisplayedAuthenticatedUserNoProfilePicturedRegistered() {
        runBlocking {
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user2@email.com", "123456").await()
        }

        assertThat(Firebase.auth.currentUser, notNullValue())

        onView(withId(R.id.bottomMenuUser))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.nav_drawer_username_textview))
            .check(matches(withText("User Test 2")))

        onView(withId(R.id.nav_drawer_description_textview))
            .check(matches((withText("User 2 descriptions"))))

        /*activityRule.scenario.onActivity {
            val drawable: BitmapDrawable =
                it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable as BitmapDrawable

            val expectedBitmap = BitmapFactory.decodeResource(it.resources, R.drawable.blank_profile_picture)
            assertThat(drawable.bitmap, equalsBitmap(expectedBitmap))
        }*/

    }

    @Test
    fun defaultValueDisplayedForAuthenticatedExistingUserWithoutExistingValues() {
        runBlocking {
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user3@email.com", "123456").await()
        }

        assertThat(Firebase.auth.currentUser, notNullValue())

        onView(withId(R.id.bottomMenuUser))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.nav_drawer_username_textview))
            .check(matches(withText("Username")))

        onView(withId(R.id.nav_drawer_description_textview))
            .check(matches((withText("More Info on user"))))

        /*activityRule.scenario.onActivity {
            val drawable: BitmapDrawable =
                it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable as BitmapDrawable

            val expectedBitmap = BitmapFactory.decodeResource(it.resources, R.drawable.blank_profile_picture)
            assertThat(drawable.bitmap, equalsBitmap(expectedBitmap))
        }*/
    }
}