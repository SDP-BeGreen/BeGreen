package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.*
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class ProfileDetailsFragmentTest {

    private val ARG_USER = "USER"
    lateinit var fragScenario: FragmentScenario<ProfileDetailsFragment>

    @Before
    fun setup() {
        val photoMetadata: PhotoMetadata = PhotoMetadata("1", ParcelableDate(Date()), User("1",  33, "Alice"), "Gros vilain pas beau")
        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        fragScenario = launchFragmentInContainer {
            ProfileDetailsFragment.newInstance(user = User("1",  33, "Alice", 1, photoMetadata, "Description poutou poutou", "cc@gmail.com", "08920939459802", 67, null, null))
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
            connectedUserViewModel.currentUser.value = user2
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

        frag.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.currentUser.value = user
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
            connectedUserViewModel.currentUser.value = user
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
            connectedUserViewModel.currentUser.value = user
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
    fun takingPictureContainsTheCorrectIntent() {

        Intents.init()

        val user = User("1", 1, "Test")

        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val frag = launchFragmentInContainer<ProfileDetailsFragment>(bundle)

        frag.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.currentUser.value = user
        }

        onView(withId(R.id.fragment_profile_details_take_picture))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.fragment_profile_details_take_picture))
            .check(matches(isDisplayed()))
            .perform(click())

        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        frag.close()

        Intents.release()
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
            connectedUserViewModel.currentUser.value = fakeAuthUser
            connectedUserViewModel.currentUserProfilePicture.value = null
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
}