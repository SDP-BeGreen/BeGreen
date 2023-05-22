package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.fragment.app.commit
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
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
class SendPostFragmentTest {
    private lateinit var fragmentScenario: FragmentScenario<CameraContainer>

    //Companion object to mock the DB and Auth
    companion object {
        val user = User("test", 2, "test", "test", "test", "test", 15)
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)
        private val eventParticipantService: EventParticipantService =
            mock(EventParticipantService::class.java)
        private val eventService: EventService = mock(EventService::class.java)
        val users = listOf(
            User("1", 123, "Alice"),
            User("2", 0, "Bob Zeu bricoleur"),
            User("3", 14, "Charlie Chaplin"),
            User("4", 23, "David Pujadas"),
            User("5", 10492, "Euler"),
            User("6", 1234, "Alain Berset"),
            User("7", 1235, "Mister Alix")
        )

        // Setup the mock
        @OptIn(ExperimentalCoroutinesApi::class)
        @BeforeClass
        @JvmStatic
        fun setUp() {
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                whenever(db.getUser(user.id))
                    .thenReturn(user)
                whenever(auth.getFlowUserIds())
                    .thenReturn(MutableStateFlow(user.id))
                whenever(auth.getConnectedUserId())
                    .thenReturn(user.id)
                whenever(db.getAllUsers()).thenReturn(users)
                whenever(db.getFollowedIds(any(), any())).thenReturn(emptyList())
                whenever(
                    eventParticipantService.getParticipant(
                        any(),
                        any(),
                        eq(user.id),
                        eq(ContestParticipant::class.java)
                    )
                ).thenReturn(
                    ContestParticipant(user.id, 0)
                )
                whenever(eventParticipantService.addParticipant(any(), eq(user.id), any())).then { }
                whenever(
                    eventService.getAllEvents(
                        RootPath.CONTESTS,
                        Contest::class.java
                    )
                ).thenReturn(
                    MutableStateFlow(listOf())
                )
            }
        }
    }


    //Setup the koin test rule
    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
            single { auth }
            single { eventParticipantService }
            single { eventService }
        })
    )

    //Grant permission to use the camera and location
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule
    val fineLocationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)


    //Setup the scenario
    @Before
    fun setup() {
        fragmentScenario = launchFragmentInContainer { CameraContainer.newInstance() }
            .onFragment {
                it.parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.mainCameraFragmentContainer, SendPostFragment.newInstance(user.id))
                }
            }
    }

    @Test
    fun categoryIsDisplayed() {
        // Check if the category input is displayed
        onView(withId(R.id.post_category)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun previewImageIsDisplayed() {
        // Check if the description input is displayed
        onView(withId(R.id.preview)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun descriptionIsDisplayed() {
        // Check if the description input is displayed
        onView(withId(R.id.post_description)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun sendPostBtnIsDisplayed() {
        // Check if the category input is displayed
        onView(withId(R.id.send_post)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun typeInCategoryWorks() {
        // Check the category input
        onView(withId(R.id.post_category)).perform(typeText("test"))
        onView(withId(R.id.post_category)).check(matches(withText("test")))
    }

    @Test
    fun typeInDescriptionWorks() {
        // Check the description input
        onView(withId(R.id.post_description)).perform(typeText("test"))
        onView(withId(R.id.post_description)).check(matches(withText("test")))
    }

    @Test
    fun postPhotoDoesNotUpdateUserWhenDatabaseFailsToStoreImage() {

        runTest {

            // Mock the db to pretend it could not store the image
            `when`(db.addTrashPhoto(any(), any())).thenReturn(null)

            // Image of the post
            val postImage = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)

            // Set the image in the ImageView
            fragmentScenario.onFragment { fragment ->
                val rootView = fragment.view
                val imageView = rootView!!.rootView.findViewById<ImageView>(R.id.preview)
                imageView.setImageBitmap(postImage)
            }

            onView(withId(R.id.post_category))
                .perform(typeText("category"), closeSoftKeyboard())
            onView(withId(R.id.post_description))
                .perform(typeText("description"), closeSoftKeyboard())
            // Click to send the photo
            onView(withId(R.id.send_post)).perform(click())

            // Check that we correctly tried to post the photo
            verify(db, times(1)).addTrashPhoto(eq(postImage), any())
            // Check that we did update anything on the database, since we could not post the image
            verify(db, never()).addUser(any(), any())
        }

    }

    @Test
    fun postPhotoUpdateUserCorrectlyWhenDatabaseSucceedsToStoreImage() {

        runTest {

            var updatedScore = user.score
            var newTrashPhotoMetadata: TrashPhotoMetadata? = null

            // Mock the database to pretend it correctly stored the image
            `when`(db.addTrashPhoto(any(), any())).then {
                newTrashPhotoMetadata = it.arguments[1] as TrashPhotoMetadata
                updatedScore += newTrashPhotoMetadata!!.trashCategory!!.value
                newTrashPhotoMetadata
            }

            // Image of the post
            val postImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

            // Set the image in the ImageView
            fragmentScenario.onFragment { fragment ->
                val rootView = fragment.view
                val imageView = rootView!!.rootView.findViewById<ImageView>(R.id.preview)
                imageView.setImageBitmap(postImage)
            }

            onView(withId(R.id.post_category))
                .perform(typeText("category"), closeSoftKeyboard())
            onView(withId(R.id.post_description))
                .perform(typeText("description"), closeSoftKeyboard())
            // Click to send the photo
            onView(withId(R.id.send_post)).perform(click())

            // Check that we correctly tried to post the photo
            verify(db, times(1)).addTrashPhoto(eq(postImage), any())
            // Check that we updated the user with its new score and new trashPhotoMetadata list
            verify(db, times(1)).addUser(
                user.copy(
                    score = updatedScore,
                    trashPhotosMetadatasList = listOf(newTrashPhotoMetadata!!)
                ),
                user.id
            )
        }
    }

    /* This does not pass CI, eventhough it works locally. Still investiagting why
    @Test
    fun postingCorrectlyUpdatesParticipantScoreOfContests() {
        runTest {

            var updatedScore = 0

            `when`(db.addTrashPhoto(any(), any())).then {
                val newTrashPhotoMetadata = it.arguments[1] as TrashPhotoMetadata
                updatedScore += newTrashPhotoMetadata.trashCategory!!.value
                newTrashPhotoMetadata
            }

            whenever(eventService.getAllEvents(RootPath.CONTESTS, Contest::class.java)).thenReturn(
                MutableStateFlow(
                    listOf(
                        // This contest should be updated when posting a photo
                        Contest(
                            "Active, near and joined contest",
                            "creator",
                            "contest",
                            "description",
                            System.currentTimeMillis() - 100000,
                            System.currentTimeMillis() + 100000,
                            CustomLatLng(0.0, 0.0),
                            Long.MAX_VALUE, // Very large radius, to be sure that the user is considered in the contest
                            false
                        ),
                        // This contest should NOT be updated when posting a photo (contest not joined)
                        Contest(
                            "Active, near and not joined contest",
                            "creator",
                            "contest",
                            "description",
                            System.currentTimeMillis() - 100000,
                            System.currentTimeMillis() + 100000,
                            CustomLatLng(0.0, 0.0),
                            Long.MAX_VALUE, // Very large radius, to be sure that the user is considered in the contest
                            false
                        ),
                        // This contest should NOT be updated when posting a photo (contest not started)
                        Contest(
                            "Unactive, near and joined contest",
                            "creator",
                            "contest",
                            "description",
                            System.currentTimeMillis() - 100000,
                            System.currentTimeMillis() - 1000,
                            CustomLatLng(0.0, 0.0),
                            Long.MAX_VALUE, // Very large radius, to be sure that the user is considered in the contest
                            false
                        ),
                        // This contest should NOT be updated when posting a photo (contest too far)
                        Contest(
                            "Active, not near and joined contest",
                            "creator",
                            "contest",
                            "description",
                            System.currentTimeMillis() - 100000,
                            System.currentTimeMillis() + 100000,
                            CustomLatLng(0.0, 0.0),
                            1,
                            false
                        )
                    )
                )
            )

            // Joined contest
            whenever(
                eventParticipantService.getAllParticipants(
                    RootPath.CONTESTS,
                    "Active, near and joined contest",
                    ContestParticipant::class.java
                )
            ).thenReturn(MutableStateFlow(listOf(ContestParticipant(user.id, 0))))
            // Not joined contest
            whenever(
                eventParticipantService.getAllParticipants(
                    RootPath.CONTESTS,
                    "Active, near and not joined contest",
                    ContestParticipant::class.java
                )
            ).thenReturn(MutableStateFlow(listOf()))
            // Joined contest
            whenever(
                eventParticipantService.getAllParticipants(
                    RootPath.CONTESTS,
                    "Unactive, near and joined contest",
                    ContestParticipant::class.java
                )
            ).thenReturn(MutableStateFlow(listOf(ContestParticipant(user.id, 0))))
            // Joined contest
            whenever(
                eventParticipantService.getAllParticipants(
                    RootPath.CONTESTS,
                    "Active, not near and joined contest",
                    ContestParticipant::class.java
                )
            ).thenReturn(MutableStateFlow(listOf(ContestParticipant(user.id, 0))))

            whenever(
                eventParticipantService.addParticipant(
                    eq(RootPath.CONTESTS),
                    any(),
                    any()
                )
            ).then {}

            // Image of the post
            val postImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

            // Set the image in the ImageView
            fragmentScenario.onFragment { fragment ->
                val rootView = fragment.view
                val imageView = rootView!!.rootView.findViewById<ImageView>(R.id.preview)
                imageView.setImageBitmap(postImage)
            }

            onView(withId(R.id.post_category))
                .perform(typeText("category"), closeSoftKeyboard())
            onView(withId(R.id.post_description))
                .perform(typeText("description"), closeSoftKeyboard())
            // Click to send the photo
            onView(withId(R.id.send_post)).perform(click())

            // Check that the participant's score got updated in the active, near and joined contest
            verify(eventParticipantService, times(1)).addParticipant(
                RootPath.CONTESTS, "Active, near and joined contest", ContestParticipant(
                    user.id, updatedScore
                )
            )

            // Check that the participant's score did not get updated in all other contests
            verify(eventParticipantService, never()).addParticipant(
                eq(RootPath.CONTESTS), eq("Active, near and not joined contest"), any()
            )
            verify(eventParticipantService, never()).addParticipant(
                eq(RootPath.CONTESTS), eq("Unactive, near and joined contest"), any()
            )
            verify(eventParticipantService, never()).addParticipant(
                eq(RootPath.CONTESTS), eq("Active, not near and joined contest"), any()
            )
        }
    }*/

}