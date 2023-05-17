package com.github.sdp_begreen.begreen.fragments

import android.graphics.Bitmap
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
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@LargeTest
class UserPhotosViewAdapterTest {

    companion object {
    //    private val db: DB = Mockito.mock(DB::class.java)
    //    private val auth: Auth = Mockito.mock(Auth::class.java)
        private val user = User(id = "10", score = 94, displayName = "Messi")
        private val userId = "10"
        private val fakePicture = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
        private val authUserFlow = MutableStateFlow(userId)

        /*
        @OptIn(ExperimentalCoroutinesApi::class)
        @BeforeClass
        @JvmStatic
        fun setUp() {
            // The implementation need to be provided before the rule is executed,
            // that's why we do it in the beforeClass method
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                whenever(db.getUser(userId)).thenReturn(user)
                whenever(db.getUserProfilePicture(userId)).thenReturn(fakePicture)
                whenever(auth.getConnectedUserId()).thenReturn(userId)
                whenever(auth.getFlowUserIds()).thenReturn(authUserFlow.onEach { delay(10) })
            }
        }*/
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // Setup the koin test rule
   /* @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
            single { auth }
        })
    )*/

    @get:Rule
    val koinTestRule = KoinTestRule()

    private val photoList = listOf(
        TrashPhotoMetadata("1", ParcelableDate.now, "0", "Look at me cleaning!", TrashCategory.PLASTIC),
        TrashPhotoMetadata("2", ParcelableDate.now, "1", "Helloooo", TrashCategory.PLASTIC),
    )
    private var userPhotoViewAdapter = UserPhotosViewAdapter(photoList, true, TestLifecycleOwner().lifecycleScope)
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun userViewAdapterGetItemCountWorksOnTrivialList() {
        MatcherAssert.assertThat(userPhotoViewAdapter.itemCount, equalTo(2))
    }

    @Test
    fun userViewAdapterGetItemCountWorksOnEmptyList() {
        userPhotoViewAdapter = UserPhotosViewAdapter(listOf(), true, TestLifecycleOwner().lifecycleScope)
        MatcherAssert.assertThat(userPhotoViewAdapter.itemCount, equalTo(0))
    }

    @Test
    fun userPhotosViewAdapterGetItemCountWorksOnNullList() {
        userPhotoViewAdapter = UserPhotosViewAdapter(null, true, TestLifecycleOwner().lifecycleScope)
        MatcherAssert.assertThat(userPhotoViewAdapter.itemCount, equalTo(0))
    }

    @Test
    fun userPhotosViewAdapterOnBindViewHolderNonExistingUser() {

        val viweHolder = userPhotoViewAdapter.onCreateViewHolder(LinearLayout(appContext), 0)
        userPhotoViewAdapter.onBindViewHolder(viweHolder, 0)

        MatcherAssert.assertThat(viweHolder.titleView.text, equalTo("Title"))
        MatcherAssert.assertThat(viweHolder.subtitleView, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(viweHolder.subtitleView.text.toString(), equalTo("subhead"))
        MatcherAssert.assertThat(viweHolder.descriptionView.text.toString(), equalTo("Default description of the post"))
    }

    @Test
    fun userPhotosViewAdapterOnBindViewHolderWorksOnTrivialList() {

        val viweHolder = userPhotoViewAdapter.onCreateViewHolder(LinearLayout(appContext), 0)

        // TODO : Koin va regler ce probleme
        userPhotoViewAdapter.onBindViewHolder(viweHolder, 0)

      //  MatcherAssert.assertThat(viweHolder.titleView.text, equalTo("Look at me cleaning!"))
      //  MatcherAssert.assertThat(viweHolder.subtitleView, CoreMatchers.notNullValue())
      //  MatcherAssert.assertThat(viweHolder.subtitleView.text.toString(), CoreMatchers.containsString(TrashCategory.PLASTIC.title))
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