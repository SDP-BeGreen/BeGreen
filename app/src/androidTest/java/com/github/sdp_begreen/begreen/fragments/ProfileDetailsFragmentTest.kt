package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.*
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import junit.framework.AssertionFailedError
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class ProfileDetailsFragmentTest {
    companion object {
        @BeforeClass
        @JvmStatic fun setupEmulator() {
            try {
                Firebase.database.useEmulator("10.0.2.2", 9000)
                Firebase.storage.useEmulator("10.0.2.2", 9199)
                Firebase.auth.useEmulator("10.0.2.2", 9099)
            } catch (_:java.lang.IllegalStateException){}
        }
    }

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    private val ARG_USER = "USER"
    lateinit var fragScenario: FragmentScenario<ProfileDetailsFragment>
    private val ARG_RECENT_POSTS = "recent_posts"
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        val photos = listOf(
            PhotoMetadata("erfs","Look at me cleaning!", ParcelableDate(Date()),User("0", 111, "SuperUser69",0), "Déchet organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"),
            PhotoMetadata("1", "title", ParcelableDate(Date()), User("1", 812, "Alice", 33, ), "Gros vilain pas beau", "desc"),
        )
        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        launchFragmentInContainer {
            ProfileDetailsFragment.newInstance(
                user = User("1",142, "Alice", 56, photos[0], "Description poutou poutou", "cc@gmail.com", "08920939459802", 67, null, null),
                photos = photos
            )
        }
    }
    @Test
    fun testProfileDetailsFragmentIsCorrectlyDisplayed() {

        onView(withId(R.id.fragment_profile_details)).check(matches(isDisplayed()))
    }

    @Test
    fun testProfileDetailsFragmentFollowButton(){
        onView(withId(R.id.fragment_profile_details_follow_button)).perform(click())
        onView(withId(R.id.fragment_profile_details_follow_button)).check(matches(ViewMatchers.withText("Unfollow")))
    }

    @Test
    fun testProfileDetailsFragmentFollowButton2(){
        onView(withId(R.id.fragment_profile_details_follow_button)).perform(click())
        onView(withId(R.id.fragment_profile_details_follow_button)).perform(click())
        onView(withId(R.id.fragment_profile_details_follow_button)).check(matches(ViewMatchers.withText("Follow")))
    }

    @Test
    fun testProfileDetailsWithCompleteUserFragmentIsCorrectlyDisplayed() {
        onView(withId(R.id.fragment_profile_details)).check(matches(isDisplayed()))
    }

    @Test
    fun editAndSaveButtonHiddenNonPersonalProfileDetails() {

        val user = User("1", 1, "Test")
        val user2 = User("2", 2, "Test 2")

        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val frag = launchFragmentInContainer<ProfileDetailsFragment>(bundle)

        frag.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
        connectedUserViewModel.setCurrentUser(user2)
        }

        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.fragment_profile_details_save_profile))
            .check(matches(not(isDisplayed())))

        frag.close()
    }

    @Test
    fun editButtonDisplayedPersonalProfileDetails() {

        val user = User("1", 1, "Test")

        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val frag = launchFragmentInContainer<ProfileDetailsFragment>(bundle)
        Firebase.auth.signOut()

        frag.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(user)
        }

        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))

        frag.close()
    }

    @Test
    fun saveButtonDisplayedClickOnEditButton() {
        val user = User("1", 1, "Test")

        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val frag = launchFragmentInContainer<ProfileDetailsFragment>(bundle)

        frag.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(user)
        }

        onView(withId(R.id.fragment_profile_details_save_profile))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.fragment_profile_details_save_profile))
            .check(matches(isDisplayed()))

        frag.close()
    }

    @Test
    fun takePictureButtonDisplayedClickEditButton() {
        val user = User("1", 1, "Test")

        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val frag = launchFragmentInContainer<ProfileDetailsFragment>(bundle)

        frag.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(user)
        }

        onView(withId(R.id.fragment_profile_details_take_picture))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.fragment_profile_details_take_picture))
            .check(matches(isDisplayed()))

        frag.close()
    }

    @Test
    fun takingPictureCorrectlyStoresPictureInDatabase() {


        val user = User("Test_take_picture", 1, "Test")
        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val fakePicture = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        // fake test registry for testing camera
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I : Any?, O : Any?> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, fakePicture)
            }
        }

        with(launchFragmentInContainer(bundle) { ProfileDetailsFragment(testRegistry) }) {
            onFragment {
                val connectedUserViewModel:
                        ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
                connectedUserViewModel.setCurrentUser(user)
            }

            // initially test that the user does not contains any profile picture metadata
            assertThat(user.profilePictureMetadata, `is`(nullValue()))

            // store user in db, so it exists
            runBlocking {
                FirebaseDB.addUser(user, user.id)
            }

            // click on button to edit profile
            onView(withId(R.id.fragment_profile_details_edit_profile))
                .check(matches(isDisplayed()))
                .perform(click())

            // take picture
            onView(withId(R.id.fragment_profile_details_take_picture))
                .check(matches(isDisplayed()))
                .perform(click())

            // go back to normal view
            onView(withId(R.id.fragment_profile_details_save_profile))
                .check(matches(isDisplayed()))
                .perform(click())

            // check that the user has now its picture stored on the db
            runBlocking {
                // It may take times to store picture in db, so retry a couple of time to fetch user
                // that contains the profile picture
                repeat(10) { iter ->// retry at most 10 times
                    try {
                        FirebaseDB.getUser(user.id)?.also {
                            assertThat(it.profilePictureMetadata, `is`(not(nullValue())))
                        }
                        // Early return once no error is caught
                        return@repeat
                    } catch (e: AssertionFailedError) {
                        // if number of iteration is done rethrow exception
                        if (iter == 9) throw e
                    }
                    // retry after 1 seconds
                    delay(1000)
                }

            }
            close()
        }
    }

    @Test
    fun takingPictureCorrectlyStoresPictureInLiveData() {


        val user = User("Test_take_picture", 1, "Test")
        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val fakePicture = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        // fake test registry for testing camera
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I : Any?, O : Any?> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, fakePicture)
            }
        }

        with(launchFragmentInContainer(bundle) { ProfileDetailsFragment(testRegistry) }) {
            onFragment {
                val connectedUserViewModel:
                        ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
                connectedUserViewModel.setCurrentUser(user)

                // initially check that no profile picture is associated with this user
                assertThat(connectedUserViewModel.currentUserProfilePicture.value, `is`(nullValue()))
            }

            // click on button to edit profile
            onView(withId(R.id.fragment_profile_details_edit_profile))
                .check(matches(isDisplayed()))
                .perform(click())

            // take picture
            onView(withId(R.id.fragment_profile_details_take_picture))
                .check(matches(isDisplayed()))
                .perform(click())

            // check that the user has now its picture stored on the db

            onFragment {
                val connectedUserViewModel:
                        ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })

                // check that the value is now the taken picture
                assertThat(connectedUserViewModel.currentUserProfilePicture.value, `is`(sameInstance(fakePicture)))
            }
            close()
        }
    }

    @Test
    fun correctInfoDisplayedExistingUserDifferentFromAuthenticatedOne() {
        val fakeAuthUser = User("1", 1, "Test")
        val existingUser = User("VaRgQioAuiGtfDlv5uNuosNsACCJ",  0, description = "That's the awesome description of test user 1", displayName = "User Test 1", email = "user1@email.ch", phone = "+41245285397", profilePictureMetadata = PhotoMetadata("VaRgQioAuiGtfDlv5uNuosNsACCJ_profile_picture"))

        val bundle = Bundle().apply { putParcelable(ARG_USER, existingUser) }
        val frag = launchFragmentInContainer<ProfileDetailsFragment>(bundle)

        frag.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(fakeAuthUser)
        }

        onView(withId(R.id.fragment_profile_details_profile_name))
            .check(matches(withText("User Test 1")))

        onView(withId(R.id.fragment_profile_details_profile_description))
            .check(matches(withText("That's the awesome description of test user 1")))

        onView(withId(R.id.fragment_profile_details_profile_email))
            .check(matches(withText("user1@email.ch")))

        onView(withId(R.id.fragment_profile_details_profile_phone))
            .check(matches(withText("+41245285397")))

        frag.close()
    }

    //Todo find how we can test async function throwing errors
    //@Test
    //fun testListenerForProfileImage() {
    //    val args = Bundle().apply {
    //        putParcelable(ARG_USER, User("1",142, "Alice", 56, PhotoMetadata(), "Description poutou poutou", "cc@gmail.com", "08920939459802", 67, null, null, PhotoMetadata("VaRgQioAuiGtfDlv5uNuosNsACCJ_profile_picture")))
    //    }
//
    //    // Launch fragment with arguments
    //    val scenario = FragmentScenario.launchInContainer(ProfileDetailsFragment::class.java, args)
    //    assertNotNull(scenario.onFragment{fragment ->fragment.parentFragmentManager.beginTransaction().remove(fragment).commit()
    //    })
    //}
}