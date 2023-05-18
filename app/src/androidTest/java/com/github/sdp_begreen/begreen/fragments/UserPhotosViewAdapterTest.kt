package com.github.sdp_begreen.begreen.fragments

import android.graphics.Bitmap
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.ProfilePhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
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
class UserPhotosViewAdapterTest {

    companion object {
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)
        private val userId = "10"
        private val profilePhotoMetadata = ProfilePhotoMetadata("1", null, userId)
        val user = User(userId, 2, "test", 5, "test",
            "test", "test", 15, listOf("1", "3", "6"), listOf("2", "4"), profilePhotoMetadata)
        val users = listOf(
            User("1", 123, "Alice"),
            User("2", 0, "Bob Zeu bricoleur"),
            User("3", 14, "Charlie Chaplin"),
            User("4", 23, "David Pujadas"),
            User("5", 10492, "Euler"),
            User("6", 1234, "Alain Berset"),
            User("7", 1235, "Mister Alix")
        )

        @BeforeClass
        @JvmStatic
        fun setUp() {
            // The implementation need to be provided before the rule is executed,
            // that's why we do it in the beforeClass method
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                `when`(db.getUser(user.id))
                    .thenReturn(user)
                `when`(auth.getFlowUserIds())
                    .thenReturn(MutableStateFlow(user.id))
                `when`(auth.getConnectedUserId())
                    .thenReturn(user.id)
                `when`(db.getAllUsers())
                    .thenReturn(users)
                `when`(db.getFollowedIds(user.id))
                    .thenReturn(user.following)
            }
        }
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // Setup the koin test rule
   @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
            single { auth }
        })
    )

    private val photoList = listOf(
        TrashPhotoMetadata("1", ParcelableDate.now, userId, "Look at me cleaning!", TrashCategory.PLASTIC),
        TrashPhotoMetadata("1", ParcelableDate.now, userId, "Look at me cleaning!", TrashCategory.PLASTIC),
    )
    private var userPhotoViewAdapter = UserPhotosViewAdapter(photoList, true, TestLifecycleOwner().lifecycleScope, InstrumentationRegistry.getInstrumentation().targetContext.resources)
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun userViewAdapterGetItemCountWorksOnTrivialList() {
        assertThat(userPhotoViewAdapter.itemCount, equalTo(2))
    }

    @Test
    fun userViewAdapterGetItemCountWorksOnEmptyList() {

        val userPhotoViewAdapter = UserPhotosViewAdapter(listOf(), true, TestLifecycleOwner().lifecycleScope, InstrumentationRegistry.getInstrumentation().targetContext.resources)
        assertThat(userPhotoViewAdapter.itemCount, equalTo(0))
    }

    @Test
    fun userPhotosViewAdapterGetItemCountWorksOnNullList() {

        val userPhotoViewAdapter = UserPhotosViewAdapter(null, true, TestLifecycleOwner().lifecycleScope, InstrumentationRegistry.getInstrumentation().targetContext.resources)
        assertThat(userPhotoViewAdapter.itemCount, equalTo(0))
    }

    @Test
    fun userPhotosViewAdapterOnBindViewHolderNonExistingUser() {

        runTest {

            `when`(db.getUser(user.id))
                .thenReturn(null)

            val viewHolder = userPhotoViewAdapter.onCreateViewHolder(LinearLayout(appContext), 0)

            assertThat(viewHolder.titleView.text, equalTo("Title"))
            assertThat(viewHolder.subtitleView.text.toString(), equalTo("subhead"))
            assertThat(
                viewHolder.descriptionView.text.toString(),
                equalTo("Default description of the post")
            )
        }
    }

    @Test
    fun userPhotosViewAdapterOnBindViewHolderWorksOnTrivialList() {

        runTest {

            `when`(db.getUser(user.id))
                .thenReturn(user)

            val photoList = listOf(
                TrashPhotoMetadata("1", ParcelableDate.now, userId, "Look at me cleaning!", TrashCategory.PLASTIC),
                TrashPhotoMetadata("1", ParcelableDate.now, userId, "Look at me cleaning!", TrashCategory.PLASTIC),
            )

            val userPhotoViewAdapter =
                UserPhotosViewAdapter(photoList, true, TestLifecycleOwner().lifecycleScope, InstrumentationRegistry.getInstrumentation().targetContext.resources)
            val viewHolder = userPhotoViewAdapter.onCreateViewHolder(LinearLayout(appContext), 0)

            assertThat(viewHolder.avatarView.visibility, equalTo(View.VISIBLE))

            /*
            assertThat(viewHolder.titleView.text, equalTo(user.displayName))
            assertThat(viewHolder.subtitleView, CoreMatchers.notNullValue())
            assertThat(viewHolder.subtitleView.text.toString(), CoreMatchers.containsString(TrashCategory.PLASTIC.title))
            assertThat(
                viewHolder.descriptionView.text.toString(),
                equalTo("Look at me cleaning!")
            )*/
        }
    }

    @Test
    //This test will be usefull for the next task (show image in big)
    fun userPhotosViewAdapterSetListenerWorks() {
        activityRule.scenario.onActivity {
            it.supportFragmentManager.commit {
                replace(R.id.mainFragmentContainer, UserPhotoFragment.newInstance(1, photoList, true))
            }
        }
        //Here is an important test but i didn't manage to make it work, if it can give you some ideas to continue you're welcome
        //Espresso.onView(ViewMatchers.withId(R.id.user_fragment))
        //    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //Espresso.onView(ViewMatchers.withId(R.id.user_fragment)).perform(
        //    RecyclerViewActions.actionOnItemAtPosition<UserPhotosViewAdapter.ViewHolder>(0,
        //        ViewActions.click()
        //    ))
        //Espresso.onView(ViewMatchers.withId(R.id.fragment_profile_details_profile_name))
        //    .check(ViewAssertions.matches(ViewMatchers.withText("Bob")))
    }
}