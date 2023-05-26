package com.github.sdp_begreen.begreen.fragments

import android.widget.LinearLayout
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class UserViewAdapterTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val koinTestRule = KoinTestRule()

    private var userViewAdapter = UserViewAdapter(
        listOf(User("1", 0, "Test"), User("2", 1, "Test2")),
        null,
        TestLifecycleOwner().lifecycleScope,
        InstrumentationRegistry.getInstrumentation().targetContext.resources
    )
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val userList = listOf(
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie"),
        User("0", 10, "Alice"),
        User("1", 20, "Bob"),
        User("2", 15, "Charlie")
    )

    @Test
    fun userViewAdapterGetItemCountWorksOnTrivialList() {
        assertThat(userViewAdapter.itemCount, equalTo(2))
    }

    @Test
    fun userViewAdapterGetItemCountWorksOnEmptyList() {
        userViewAdapter = UserViewAdapter(
            listOf(),
            userViewAdapter.parentFragmentManager,
            TestLifecycleOwner().lifecycleScope,
            InstrumentationRegistry.getInstrumentation().targetContext.resources
        )
        assertThat(userViewAdapter.itemCount, equalTo(0))
    }

    @Test
    fun userViewAdapterGetItemCountWorksOnNullList() {
        userViewAdapter = UserViewAdapter(
            null,
            userViewAdapter.parentFragmentManager,
            TestLifecycleOwner().lifecycleScope,
            InstrumentationRegistry.getInstrumentation().targetContext.resources
        )
        assertThat(userViewAdapter.itemCount, equalTo(0))
    }

    @Test
    fun userViewAdapterOnBindViewHolderWorksOnTrivialList() {
        val vH = userViewAdapter.onCreateViewHolder(LinearLayout(appContext), 0)
        userViewAdapter.onBindViewHolder(vH, 0)
        assertThat(vH.userScore.text, equalTo("0"))
        assertThat(vH.userName.text, equalTo("Test"))
    }

    //This test should normally test the click on the user, but it doesn't work
    //If someone knows how to fix it, please do
    @Test
    fun userViewAdapterIsDisplayed() {
        val scenario = activityRule.scenario.onActivity {
            it.supportFragmentManager.commit {
                replace(R.id.mainFragmentContainer, UserFragment.newInstance(1,  true))
            }
        }

        onView(withId(R.id.user_fragment)).check(matches(isDisplayed()))

        //onView(withId(R.id.user_fragment)).perform(RecyclerViewActions.actionOnItemAtPosition<UserViewAdapter.ViewHolder>(0, click()))

        //onView(withId(R.id.fragment_profile_details_profile_name)).check(matches(withText("Bob")))
        scenario.close()
    }
}