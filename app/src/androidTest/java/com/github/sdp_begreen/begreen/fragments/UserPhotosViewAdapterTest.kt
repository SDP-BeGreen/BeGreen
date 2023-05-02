package com.github.sdp_begreen.begreen.fragments

import android.widget.LinearLayout
import androidx.fragment.app.commit
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.databinding.FragmentUserPhotoBinding
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import java.util.*

class UserPhotosViewAdapterTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    @get:Rule
    val koinTestRule = KoinTestRule()

    private val photoList = listOf(
        TrashPhotoMetadata("1", ParcelableDate.now, "0", "Look at me cleaning!", TrashCategory.PLASTIC),
        TrashPhotoMetadata("1", ParcelableDate.now, "0", "Look at me cleaning!", TrashCategory.PLASTIC),
    )
    private var userPhotoViewAdapter = UserPhotosViewAdapter(photoList, true)
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun userViewAdapterGetItemCountWorksOnTrivialList() {
        MatcherAssert.assertThat(userPhotoViewAdapter.getItemCount(), CoreMatchers.equalTo(2))
    }

    @Test
    fun userViewAdapterGetItemCountWorksOnEmptyList() {
        userPhotoViewAdapter = UserPhotosViewAdapter(listOf(), true)
        MatcherAssert.assertThat(userPhotoViewAdapter.getItemCount(), CoreMatchers.equalTo(0))
    }

    @Test
    fun userPhotosViewAdapterGetItemCountWorksOnNullList() {
        userPhotoViewAdapter = UserPhotosViewAdapter(null, true)
        MatcherAssert.assertThat(userPhotoViewAdapter.getItemCount(), CoreMatchers.equalTo(0))
    }

    @Test
    fun userPhotosViewAdapterOnBindViewHolderWorksOnTrivialList() {
        val viweHolder = userPhotoViewAdapter.onCreateViewHolder(LinearLayout(appContext), 0)
        userPhotoViewAdapter.onBindViewHolder(viweHolder, 0)
        MatcherAssert.assertThat(viweHolder.titleView.text, CoreMatchers.equalTo("title"))
        MatcherAssert.assertThat(viweHolder.subtitleView, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(viweHolder.subtitleView.text.subSequence(15,35), CoreMatchers.equalTo("Gros vilain pas beau"))
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