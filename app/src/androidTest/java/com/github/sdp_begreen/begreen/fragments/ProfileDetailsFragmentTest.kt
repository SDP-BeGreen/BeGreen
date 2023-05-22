package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.*
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.Actions
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.ProfilePhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import java.util.*


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class ProfileDetailsFragmentTest {
    companion object {

        private const val userId1 = "1234"

        val photos = arrayListOf(
            TrashPhotoMetadata(
                "erfs",
                ParcelableDate.now,
                userId1
            ),

            TrashPhotoMetadata(
                "erfs",
                ParcelableDate.now,
                userId1
            )
        )
        private val userProfilePicturePhotoMetadata = ProfilePhotoMetadata("user1_profile_picture")
        val userId2 = "1243"

        private val user1 = User(
            userId1,
            142,
            "Alice",
            "Description poutou poutou",
            "08920939459802",
            "cc@gmail.com",
            67,
            null,
            null,
          //  listOf(userId2),
            userProfilePicturePhotoMetadata
        )
        private val user2 = User(
            userId2,
            142,
            "Alice",
            "Description poutou poutou",
            "08920939459802",
            "cc@gmail.com",
            67,
            null,
            null,
            userProfilePicturePhotoMetadata
        )
        private val fakePicture1 = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
        private val fakePicture2 = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)

        @BeforeClass
        @JvmStatic
        fun setUp() {
            // The implementation need to be provided before the rule is executed,
            // that's why we do it in the beforeClass method
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                whenever(db.getUser(userId1))
                    .thenReturn(user1)
                whenever(db.getUser(userId2))
                    .thenReturn(user2)
                whenever(db.getUserProfilePicture(userProfilePicturePhotoMetadata, userId1))
                    .thenReturn(fakePicture1)
                // add a small delay, just to be sure that it is triggered after initialization
                // and arrive second, after the initial null value
                // user between tests, by simply pushing a new userId
                whenever(auth.getFlowUserIds())
                    .thenReturn(MutableStateFlow(userId1))
            }
        }
    }

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    private val ARG_USER = "USER"
    private val ARG_RECENT_POSTS = "recent_posts"
    private lateinit var fragmentScenario: FragmentScenario<ProfileDetailsFragment>

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
            single { auth }
        })
    )

    @Before
    fun setup() {

        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        fragmentScenario = launchFragmentInContainer(
            Bundle().apply {
                putParcelable(ARG_USER, user1)
                putParcelableArrayList(ARG_RECENT_POSTS, photos)
            }
        )
    }

    @Test
    fun testProfileDetailsFragmentIsCorrectlyDisplayed() {

        onView(withId(R.id.fragment_profile_details)).check(matches(isDisplayed()))
    }



    @Test
    fun testFollowButtonCorrectlyInitializedWhenNotFollowingUser() {

        runTest {

            // user1 doesn't follow this user. Therefore
            val notFollowedUser = User("B", 0)

            fragmentScenario = launchFragmentInContainer(
                Bundle().apply {
                    putParcelable(ARG_USER, notFollowedUser)
                    putParcelableArrayList(ARG_RECENT_POSTS, photos)
                }
            )


            onView(withId(R.id.fragment_profile_details_follow_button)).check(matches(withText(Actions.FOLLOW.text)))

            fragmentScenario.close()
        }
    }
/*
    @Test
    fun testFollowButtonCorrectlyInitializedWhenAlreadyFollowingUser() {

        runTest {

            // user1 is following user2

            fragmentScenario = launchFragmentInContainer(
                Bundle().apply {
                    putParcelable(ARG_USER, user2)
                    putParcelableArrayList(ARG_RECENT_POSTS, photos)
                }
            )

            onView(withId(R.id.fragment_profile_details_follow_button)).check(matches(withText(Actions.UNFOLLOW.text)))

            fragmentScenario.close()
        }
    }*/

    @Test
    fun testProfileDetailsFragmentFollowButtonCorrectlyToggles() {

        runTest {

            val notFollowedUser = User("B", 0)

            fragmentScenario = launchFragmentInContainer(
                Bundle().apply {
                    putParcelable(ARG_USER, notFollowedUser)
                    putParcelableArrayList(ARG_RECENT_POSTS, photos)
                }
            )

            onView(withId(R.id.fragment_profile_details_follow_button)).check(matches(withText(Actions.FOLLOW.text)))
            onView(withId(R.id.fragment_profile_details_follow_button)).perform(click())
            onView(withId(R.id.fragment_profile_details_follow_button)).check(matches(withText(Actions.UNFOLLOW.text)))
            onView(withId(R.id.fragment_profile_details_follow_button)).perform(click())
            onView(withId(R.id.fragment_profile_details_follow_button)).check(matches(withText(Actions.FOLLOW.text)))

            fragmentScenario.close()
        }
    }

    @Test
    fun testProfileDetailsWithCompleteUserFragmentIsCorrectlyDisplayed() {
        onView(withId(R.id.fragment_profile_details)).check(matches(isDisplayed()))
    }

    @Test
    fun correctInfoDisplayedAuthenticatedUser() {
        checkViewsContainsText(
            listOf(
                R.id.fragment_profile_details_profile_name to user1.displayName,
                R.id.fragment_profile_details_profile_description to user1.description,
                R.id.fragment_profile_details_profile_email to user1.email,
                R.id.fragment_profile_details_profile_phone to user1.phone
            )
        )
    }

    @Test
    fun editAndSaveAndCancelButtonHiddenNonPersonalProfileDetails() {

        val user = User("1", 1, "Test")
        val user2 = User("2", 2, "Test 2")

        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val frag = launchFragmentInContainer<ProfileDetailsFragment>(bundle)

        frag.onFragment {
            val connectedUserViewModel
                    by it.viewModels<ConnectedUserViewModel>(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(user2)
        }

        checkViewsMatchesMatcher(
            listOf(
                R.id.fragment_profile_details_edit_profile to not(isDisplayed()),
                R.id.fragment_profile_details_save_profile to not(isDisplayed()),
                R.id.fragment_profile_details_cancel_modification to not(isDisplayed())
            )
        )

        frag.close()
    }

    @Test
    fun editButtonDisplayedPersonalProfileDetails() {
        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
    }

    @Test
    fun saveAndCancelButtonDisplayedClickOnEditButton() {
        checkViewsMatchesMatcher(
            listOf(
                R.id.fragment_profile_details_save_profile to not(isDisplayed()),
                R.id.fragment_profile_details_cancel_modification to not(isDisplayed()),
            )
        )

        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        checkViewsMatchesMatcher(
            listOf(
                R.id.fragment_profile_details_save_profile to isDisplayed(),
                R.id.fragment_profile_details_cancel_modification to isDisplayed(),
                R.id.fragment_profile_details_edit_profile to not(isDisplayed())
            )
        )
    }

    @Test
    fun editableFieldInitiallyHidden() {
        checkViewsMatchesMatcher(
            listOf(
                R.id.fragment_profile_details_profile_name_edit to not(isDisplayed()),
                R.id.fragment_profile_details_profile_description_edit to not(isDisplayed()),
                R.id.fragment_profile_details_profile_email_edit to not(isDisplayed()),
                R.id.fragment_profile_details_profile_phone_edit to not(isDisplayed())
            )
        )
    }

    @Test
    fun editableFieldDisplayedStartedEditing() {
        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        checkViewsMatchesMatcher(
            listOf(
                R.id.fragment_profile_details_profile_name_edit to isDisplayed(),
                R.id.fragment_profile_details_profile_description_edit to isDisplayed(),
                R.id.fragment_profile_details_profile_email_edit to isDisplayed(),
                R.id.fragment_profile_details_profile_phone_edit to isDisplayed()
            )
        )
    }

    @Test
    fun displayingFieldHiddenStartedEditing() {
        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        checkViewsMatchesMatcher(
            listOf(
                R.id.fragment_profile_details_profile_name to not(isDisplayed()),
                R.id.fragment_profile_details_profile_description to not(isDisplayed()),
                R.id.fragment_profile_details_profile_email to not(isDisplayed()),
                R.id.fragment_profile_details_profile_phone to not(isDisplayed())
            )
        )
    }

    @Test
    fun takePictureButtonDisplayedClickEditButton() {
        onView(withId(R.id.fragment_profile_details_take_picture))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.fragment_profile_details_take_picture))
            .check(matches(isDisplayed()))
    }

    @Test
    fun editableFieldsContainsCorrectValuesUponStartEditing() {
        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        checkViewsContainsText(
            listOf(
                R.id.fragment_profile_details_profile_name_edit to user1.displayName,
                R.id.fragment_profile_details_profile_description_edit to user1.description,
                R.id.fragment_profile_details_profile_email_edit to user1.email,
                R.id.fragment_profile_details_profile_phone_edit to user1.phone
            )
        )
    }

    @Test
    fun takingPictureCorrectlyStoresPictureInDatabase() {
        val user = User("Test_take_picture", 1, "Test")
        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }

        // fake test registry for testing camera
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I : Any?, O : Any?> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, fakePicture2)
            }
        }
        var savedPicture: Bitmap? = null
        runTest {
            `when`(db.storeUserProfilePicture(eq(fakePicture2), eq(user.id), any()))
                .then {
                    savedPicture =
                        it.getArgument(0) // retrieved the fake pictured passed in arg, to ensure that the method has been called
                    it.arguments[2] // return the same metadata as received
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

            // click on button to edit profile
            onView(withId(R.id.fragment_profile_details_edit_profile))
                .check(matches(isDisplayed()))
                .perform(click())

            // take picture
            onView(withId(R.id.fragment_profile_details_take_picture))
                .check(matches(isDisplayed()))
                .perform(click())

            // save modifications
            onView(withId(R.id.fragment_profile_details_save_profile))
                .check(matches(isDisplayed()))
                .perform(click())

            // check that picture has been saved
            assertThat(savedPicture, `is`(fakePicture2))

            close()
        }
    }

    @Test
    fun takingPictureCorrectlyStoresPictureViewModel() {
        val user = User("Test_take_picture", 1, "Test")
        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }

        // fake test registry for testing camera
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I : Any?, O : Any?> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, fakePicture2)
            }
        }

        with(launchFragmentInContainer(bundle) { ProfileDetailsFragment(testRegistry) }) {
            onFragment {
                val connectedUserViewModel:
                        ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })
                connectedUserViewModel.setCurrentUser(user)

                // initially check that no profile picture is associated with this user
                assertThat(
                    connectedUserViewModel.currentUserProfilePicture.value,
                    `is`(nullValue())
                )
            }

            // click on button to edit profile
            onView(withId(R.id.fragment_profile_details_edit_profile))
                .check(matches(isDisplayed()))
                .perform(click())

            // take picture
            onView(withId(R.id.fragment_profile_details_take_picture))
                .check(matches(isDisplayed()))
                .perform(click())

            // save modifications
            onView(withId(R.id.fragment_profile_details_save_profile))
                .check(matches(isDisplayed()))
                .perform(click())

            onFragment {
                val connectedUserViewModel:
                        ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })

                // check that the value is now the taken picture
                assertThat(
                    connectedUserViewModel.currentUserProfilePicture.value,
                    `is`(sameInstance(fakePicture2))
                )
            }
            close()
        }
    }

    @Test
    fun editingUserDetailsCorrectlySavedInDBUponSaving() {
        val newUser = user1.copy(
            description = "My new description",
            displayName = "My new name",
            phone = "114123",
            email = "My new email address"
        )
        var savedUser: User? = null
        runTest {
            `when`(db.addUser(newUser, newUser.id)).then {
                savedUser = it.arguments[0] as User
                return@then Unit
            }
        }

        // start with a null savedUser
        assertThat(savedUser, `is`(nullValue()))

        editAndSaveUserValues(newUser)

        // check that picture has been saved
        assertThat(savedUser, `is`(newUser))

    }

    @Test
    fun editingUserDetailsCorrectlySavedInViewModelUponSave() {
        fragmentScenario.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })

            // initially check that no profile picture is associated with this user
            assertThat(
                connectedUserViewModel.currentUser.value,
                `is`(user1)
            )
        }

        val newUser = user1.copy(
            description = "My new description",
            displayName = "My new name",
            phone = "114123",
            email = "My new email address"
        )

        editAndSaveUserValues(newUser)

        fragmentScenario.onFragment {
            val connectedUserViewModel:
                    ConnectedUserViewModel by it.viewModels(ownerProducer = { it.requireActivity() })

            // initially check that no profile picture is associated with this user
            assertThat(
                connectedUserViewModel.currentUser.value,
                `is`(newUser)
            )
        }
    }

    @Test
    fun cancelEditedValuesCorrectlyResetValuesInEditableView() {

        val newUser = user1.copy(
            description = "My new description",
            displayName = "My new name",
            phone = "114123",
            email = "My new email address"
        )

        editUserValues(newUser)

        // cancel modifications
        onView(withId(R.id.fragment_profile_details_cancel_modification))
            .check(matches(isDisplayed()))
            .perform(click())

        // check that editable field have again initial value when starting new edition
        // cancel modifications
        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())


        checkViewsContainsText(
            listOf(
                R.id.fragment_profile_details_profile_name_edit to user1.displayName,
                R.id.fragment_profile_details_profile_description_edit to user1.description,
                R.id.fragment_profile_details_profile_email_edit to user1.email,
                R.id.fragment_profile_details_profile_phone_edit to user1.phone
            )
        )

    }

    @Test
    fun correctValueDisplayedAfterProfileEdited() {

        val newUser = user1.copy(
            description = "My new description",
            displayName = "My new name",
            phone = "114123",
            email = "My new email address"
        )

        editAndSaveUserValues(newUser)

        checkViewsContainsText(
            listOf(
                R.id.fragment_profile_details_profile_name to newUser.displayName,
                R.id.fragment_profile_details_profile_description to newUser.description,
                R.id.fragment_profile_details_profile_email to newUser.email,
                R.id.fragment_profile_details_profile_phone to newUser.phone
            )
        )

    }

    @Test
    fun correctInfoDisplayedExistingUserDifferentFromAuthenticatedOne() {
        val user = User(
            "VaRgQioAuiGtfDlv5uNuosNsACCJ",
            0,
            description = "That's the awesome description of test user 1",
            displayName = "User Test 1",
            email = "user1@email.ch",
            phone = "1984z719848",
            profilePictureMetadata = ProfilePhotoMetadata("VaRgQioAuiGtfDlv5uNuosNsACCJ_profile_picture")
        )

        val bundle = Bundle().apply { putParcelable(ARG_USER, user) }
        val frag = launchFragmentInContainer<ProfileDetailsFragment>(bundle)

        checkViewsContainsText(
            listOf(
                R.id.fragment_profile_details_profile_name to user.displayName,
                R.id.fragment_profile_details_profile_description to user.description,
                R.id.fragment_profile_details_profile_email to user.email,
                R.id.fragment_profile_details_profile_phone to user.phone
            )
        )

        frag.close()
    }

    private fun editAndSaveUserValues(newUser: User) {

        editUserValues(newUser)

        // save modifications
        onView(withId(R.id.fragment_profile_details_save_profile))
            .check(matches(isDisplayed()))
            .perform(click())
    }

    private fun editUserValues(newUser: User) {
        // click on button to edit profile
        onView(withId(R.id.fragment_profile_details_edit_profile))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.fragment_profile_details_profile_name_edit))
            .perform(clearText())
            .perform(typeText(newUser.displayName))

        onView(withId(R.id.fragment_profile_details_profile_description_edit))
            .perform(clearText())
            .perform(typeText(newUser.description))

        onView(withId(R.id.fragment_profile_details_profile_phone_edit))
            .perform(clearText())
            .perform(typeText(newUser.phone))

        onView(withId(R.id.fragment_profile_details_profile_email_edit))
            .perform(clearText())
            .perform(typeText(newUser.email))
            .perform(closeSoftKeyboard())
    }

    /**
     * Helper function to check that each view in the list matches the corresponding matchers
     *
     * @param list A list of pair, the first element of the pair is the id of the view,
     * and the second element is the matcher that should match
     */
    private fun checkViewsMatchesMatcher(list: List<Pair<Int, Matcher<View>>>) {
        list.forEach {
            onView(withId(it.first))
                .check(matches(it.second))
        }
    }

    /**
     * Helper function to check that each view in the list contains its corresponding text
     *
     * @param list A list of pair, the first element of the pair is the id of the view,
     * and the second element is the text that should be contained in the view
     */
    private fun checkViewsContainsText(list: List<Pair<Int, String?>>) {
        checkViewsMatchesMatcher(
            list.map {
                it.first to withText(it.second)
            }
        )
    }
}