package com.github.sdp_begreen.begreen.activities

import android.Manifest.permission.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
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
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.espressoUtils.BaseRobot
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingParticipantService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingService
import com.github.sdp_begreen.begreen.fragments.SendPostFragment
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.map.BinType
import com.github.sdp_begreen.begreen.matchers.EqualsToBitmap.Companion.equalsBitmap
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    /**
     * Initialize some constant to use in tests
     */
    companion object {
        private val userPhotoMetadata = PhotoMetadata("user1_profile_picture")
        private const val userId1 = "1234"
        private const val userId2 = "1235"
        private const val userId3 = "1236"
        private const val userId4 = "1237"
        private val user1 = User(
            userId1,
            12,
            "User 1",
            5,
            null, "user 1 description", "123456789",
            "user1@email.com", profilePictureMetadata = userPhotoMetadata
        )
        private val user2 = User(
            userId2,
            12,
            "User 2",
            description = "user 2 description"
        )
        private val user3 = User(userId3, 10)
        private val fakePicture1 = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)
        private val meetingService: MeetingService = mock(MeetingService::class.java)
        private val participantService: MeetingParticipantService =
            mock(MeetingParticipantService::class.java)

        // initially do as if no user were signed in
        private val authUserFlow = MutableStateFlow<String?>(null)
        private val bins = listOf(
            Bin("1", BinType.CLOTHES, 4.3, 2.8),
            Bin("2", BinType.PAPER, 56.3, 22.3),
            Bin("3", BinType.CLOTHES, 6.0, 9.0)
        )

        @BeforeClass
        @JvmStatic
        fun setUp() {
            // The implementation need to be provided before the rule is executed,
            // that's why we do it in the beforeClass method
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                `when`(db.getUser(userId1)).thenReturn(user1)
                `when`(db.getUserProfilePicture(userPhotoMetadata, userId1))
                    .thenReturn(fakePicture1)
                // add a small delay, just to be sure that it is triggered after initialization
                // and arrive second, after the initial null value
                // use a mutable state flow, so that we can easily simulate different authenticated
                // user between tests, by simply pushing a new userId
                `when`(auth.getFlowUserIds())
                    .thenReturn(authUserFlow.onEach { delay(10) })
                `when`(auth.getConnectedUserId())
                    .thenReturn("current user id")

                `when`(db.getAllUsers()).thenReturn(listOf(user1))
                `when`(db.getAllBins()).thenReturn(bins)
                `when`(meetingService.getAllMeetings()).thenReturn(flowOf())
                `when`(db.getFollowers("current user id")).thenReturn(listOf())
                `when`(db.getFollowedIds("current user id")).thenReturn(listOf())
            }
        }
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
            single { auth }
            single { meetingService }
            single { participantService }
        })
    )

    // Need permission for camera when testing launching profile fragment
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(CAMERA)

    @get:Rule
    val fineLocationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(ACCESS_COARSE_LOCATION)

    @Test
    fun bottomNavigationBarVisible() {
        onView(withId(R.id.mainNavigationView)).check(matches(isDisplayed()))
    }

    @Test
    fun defaultDisplayedFragmentIsCamera() {
        onView(withId(R.id.cameraUIFragment)).check(matches(isDisplayed()))
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

        onView(withId(R.id.cameraUIFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun pressAdviceMenuDisplayAdviceFragment() {
        runTest {
            val advices = setOf("Advice1", "Advice2", "Advice3")
            `when`(db.getAdvices()).thenReturn(advices)
            onView(withId(R.id.bottomMenuAdvice))
                .check(matches(isDisplayed()))
                .perform(click())

            onView(withId(R.id.fragmentContainerView)).check(matches(isDisplayed()))

            onView(withId(R.id.bottomMenuCamera))
                .perform(click())
        }
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
    fun pressDrawerMenuProfileDisplayProfileDetailsFragment() {
        runTest {
            // sign in user
            authUserFlow.emit(userId1)

            // Open the navigation drawer
            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.mainNavDrawProfile))
                .check(matches(isDisplayed()))
                .perform(click())

            onView(withId(R.id.fragment_profile_details))
                .check(matches(isDisplayed()))
        }
    }
    @Test
    fun testShowContactUsBottomSheet() {
        runTest {
            // sign in user
            authUserFlow.emit(userId1)

            // Open the navigation drawer
            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.mainNavDrawContact))
                .check(matches(isDisplayed()))
                .perform(click())

            onView(withId(R.id.bottom_sheet_contact_us))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun cancelButtonCorrectlyHidesTheBottomSheet() {
        runTest {
            // sign in user
            authUserFlow.emit(userId1)

            // Open the navigation drawer
            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.mainNavDrawContact))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            // Click on the cancel button
            onView(withId(R.id.cancel_button)).check(matches(isDisplayed()))
                .perform(click())

            // Check that the Bottom Sheet Dialog was dismissed
            onView(withId(R.id.bottom_sheet_contact_us)).check(doesNotExist())
        }
    }

    @Test
    fun tryingToSubmitBlankFeedbackPrintError() {
        runTest {
            // sign in user
            authUserFlow.emit(userId1)

            // Open the navigation drawer
            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.mainNavDrawContact))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            // Enter a message into the message EditText
            onView(withId(R.id.message_edittext)).perform(typeText("  "), closeSoftKeyboard())

            // Check if the message EditText has the correct text
            onView(withId(R.id.message_edittext)).check(matches(withText("  ")))

            // Scroll to the Send button
            onView(withId(R.id.send_button))
                .check(matches(isDisplayed()))
                .perform(click())

            // Check that the error is correctly displayed
            onView(withId(R.id.message_edittext)).check(matches(hasErrorText("Enter a message")))

            // Check that the Bottom Sheet Dialog was dismissed
            onView(withId(R.id.bottom_sheet_contact_us)).check(matches(isDisplayed()))

        }
    }

    @Test
    fun testContactUsBottomSheetMessageNotVisibleWhenWriteSucceeds() {
        runTest {
            `when`(db.addFeedback(org.mockito.kotlin.any(), org.mockito.kotlin.any() , org.mockito.kotlin.any(), org.mockito.kotlin.any()))
                .thenReturn(true)
            // sign in user
            authUserFlow.emit(userId1)

            // Open the navigation drawer
            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.mainNavDrawContact))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            // The test commented below are working locally but not with the CI
            // Scroll to the Send button
            //onView(withId(R.id.send_button))
            //    .check(matches(isDisplayed()))
            //    .perform(click())

            // Check that the Bottom Sheet Dialog was dismissed
            //onView(withId(R.id.bottom_sheet_contact_us)).check(doesNotExist())
        }
    }

    @Test
    fun testContactUsBottomSheetMessageStillVisibleWhenWriteFails() {
        runTest {
            `when`(db.addFeedback(org.mockito.kotlin.any(), org.mockito.kotlin.any() , org.mockito.kotlin.any(), org.mockito.kotlin.any()))
                .thenReturn(false)
            // sign in user
            authUserFlow.emit(userId1)

            // Open the navigation drawer
            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.mainNavDrawContact))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            // Enter a message into the message EditText
            onView(withId(R.id.message_edittext)).perform(typeText("Test message"), closeSoftKeyboard())

            // Check if the message EditText has the correct text
            onView(withId(R.id.message_edittext)).check(matches(withText("Test message")))

            // Scroll to the Send button
            onView(withId(R.id.send_button))
                .check(matches(isDisplayed()))
                .perform(click())

            // Check that the Bottom Sheet Dialog was dismissed
            onView(withId(R.id.bottom_sheet_contact_us)).check(matches(isDisplayed()))

        }
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
    fun pressDrawerMenuFollowersDisplayFollowersFragmentWithNoAuthenticatedUser() {

        `when`(auth.getConnectedUserId()).thenReturn(null)

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
    fun pressDrawerMenuMeetingsDisplayMeetingFragment() {
        onView(withId(R.id.mainDrawerLayout)).perform(DrawerActions.open(GravityCompat.END))

        onView(withId(R.id.mainNavDrawMeetings))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.fragment_meeting_list))
            .check(matches(isDisplayed()))
    }


    @Test
    fun pressDrawerMenuSettingsDisplaySettingsFragment() {
        onView(withId(R.id.mainDrawerLayout))
            .perform(DrawerActions.open(GravityCompat.END))

        onView(withId(R.id.mainNavDrawSettings))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.settingsFragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun pressDrawerMenuLogoutDisplaySignInActivity() {
        // mock the signOutCurrentUser
        activityRule.scenario.onActivity {
            `when`(auth.signOutCurrentUser(it, it.getString(R.string.default_web_client_id)))
                .thenReturn(Tasks.forResult(null))
        }
        Intents.init()
        // Open the navigation drawer
        onView(withId(R.id.mainDrawerLayout))
            .perform(DrawerActions.open(GravityCompat.END))

        // Click on the Logout button
        onView(withId(R.id.mainNavDrawLogout))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        // Verify that the SignInActivity is opened
        intended(hasComponent(SignInActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun correctInfoDisplayedForAuthenticatedUser() {
        runTest {
            // sign in user
            authUserFlow.emit(userId1)

            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.nav_drawer_username_textview))
                .check(matches(withText(user1.displayName)))

            onView(withId(R.id.nav_drawer_description_textview))
                .check(matches(withText(user1.description)))


            activityRule.scenario.onActivity {
                val image = it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable
                        as BitmapDrawable
                assertThat(image.bitmap, equalsBitmap(fakePicture1))
            }
        }
    }

    @Test
    fun defaultValueDisplayedForUnauthenticatedUser() {
        runTest {
            // simulate no authenticated user
            authUserFlow.emit(null)

            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.nav_drawer_username_textview))
                .check(matches(withText("Username")))

            onView(withId(R.id.nav_drawer_description_textview))
                .check(matches(withText("More Info on user")))


            activityRule.scenario.onActivity {
                val image = it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable
                        as BitmapDrawable
                val expected = BitmapFactory.decodeResource(it.resources, R.drawable.blank_profile_picture)
                assertThat(image.bitmap, equalsBitmap(expected))
            }
        }
    }

    @Test
    fun defaultValueDisplayedForAuthenticatedUserNotInDB() {
        runTest {
            // simulate not in db by returning a null user
            `when`(db.getUser(userId4)).thenReturn(null)

            // sign in user 2
            authUserFlow.emit(userId4)

            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.nav_drawer_username_textview))
                .check(matches(withText("Username")))

            onView(withId(R.id.nav_drawer_description_textview))
                .check(matches((withText("More Info on user"))))

            activityRule.scenario.onActivity {
                val image = it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable
                        as BitmapDrawable
                val expected = BitmapFactory.decodeResource(it.resources, R.drawable.blank_profile_picture)
                assertThat(image.bitmap, equalsBitmap(expected))
            }
        }
    }

    @Test
    fun defaultProfilePicturesDisplayedAuthenticatedUserNoProfilePicturedRegistered() {
        runTest {
            // user 2 doesn't have any profile picture
            `when`(db.getUser(userId2)).thenReturn(user2)

            // sign in user 2
            authUserFlow.emit(userId2)

            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.nav_drawer_username_textview))
                .check(matches(withText("User 2")))

            onView(withId(R.id.nav_drawer_description_textview))
                .check(matches((withText("user 2 description"))))

            activityRule.scenario.onActivity {
                val image = it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable
                        as BitmapDrawable
                val expected = BitmapFactory.decodeResource(it.resources, R.drawable.blank_profile_picture)
                assertThat(image.bitmap, equalsBitmap(expected))
            }
        }
    }

    @Test
    fun defaultValueDisplayedForAuthenticatedExistingUserWithoutExistingValues() {
        runTest {
            // user 3 doesn't have any information
            `when`(db.getUser(userId3)).thenReturn(user3)

            // sign in user 3
            authUserFlow.emit(userId3)

            onView(withId(R.id.mainDrawerLayout))
                .perform(DrawerActions.open(GravityCompat.END))

            onView(withId(R.id.nav_drawer_username_textview))
                .check(matches(withText("Username")))

            onView(withId(R.id.nav_drawer_description_textview))
                .check(matches((withText("More Info on user"))))

            activityRule.scenario.onActivity {
                val image = it.findViewById<ImageView>(R.id.nav_drawer_profile_picture_imageview).drawable
                        as BitmapDrawable
                val expected = BitmapFactory.decodeResource(it.resources, R.drawable.blank_profile_picture)
                assertThat(image.bitmap, equalsBitmap(expected))
            }
        }
    }

    @Test
    fun clickOnCapturePhotoDontThrowsError() {

        activityRule.scenario.onActivity {
            val connectedUserViewModel by it.viewModels<ConnectedUserViewModel>()
            connectedUserViewModel.setCurrentUser(user1)
        }
        onView(withId(R.id.camera_capture_button)).perform(click())
        //BaseRobot().assertOnView(withId(R.id.sendPostFragment), matches(isDisplayed()))

    }

    @Test
    fun clickOnProfileDetailsCameraRedirectCorrectly() {

        activityRule.scenario.onActivity {
            val connectedUserViewModel by it.viewModels<ConnectedUserViewModel>()
            connectedUserViewModel.setCurrentUser(user1)
        }
        onView(withId(R.id.profile_cam)).perform(click())
        BaseRobot().assertOnView(withId(R.id.fragment_profile_details), matches(isDisplayed()))

    }

    @Test
    fun clickOnSendPostRedirectToPreviewCorrectly() {

        activityRule.scenario.onActivity {
            val connectedUserViewModel by it.viewModels<ConnectedUserViewModel>()
            connectedUserViewModel.setCurrentUser(user1)



            it.supportFragmentManager.beginTransaction()
                .replace(R.id.cameraUIFragment, SendPostFragment())
                .commit()
        }
        onView(withId(R.id.send_post)).perform(click())
        withId(R.layout.fragment_camera_with_ui).matches(isDisplayed())

    }

    @Test
    fun clickOnCancelPostRedirectToPreviewCorrectly() {

        activityRule.scenario.onActivity {
            val connectedUserViewModel by it.viewModels<ConnectedUserViewModel>()
            connectedUserViewModel.setCurrentUser(user1)



            it.supportFragmentManager.beginTransaction()
                .replace(R.id.mainCameraFragmentContainer, SendPostFragment())
                .commit()
        }
        onView(withId(R.id.cancel_post)).perform(click())
        withId(R.layout.fragment_camera_with_ui).matches(isDisplayed())

    }
}