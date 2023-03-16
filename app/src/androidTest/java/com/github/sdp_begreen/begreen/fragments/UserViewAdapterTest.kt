package com.github.sdp_begreen.begreen.fragments

import android.widget.LinearLayout
import androidx.fragment.app.commit
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdp_begreen.begreen.User
import com.github.sdp_begreen.begreen.activities.MainActivity
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import com.github.sdp_begreen.begreen.R

class UserViewAdapterTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private var userViewAdapter = UserViewAdapter(listOf(User(1, "Test", 0), User(2, "Test2", 1)), null)
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val userList = listOf(
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(1,"Bob", 20),
        User(2,"Charlie", 15),
        User(0,"Alice", 10),
        User(1,"Bob", 20),
        User(2,"Charlie", 15)
    )
    @Test
    fun userViewAdapterGetItemCountWorksOnTrivialList() {
        assertThat(userViewAdapter.getItemCount(), equalTo(2))
    }

    @Test
    fun userViewAdapterGetItemCountWorksOnEmptyList() {
        userViewAdapter = UserViewAdapter(listOf(), userViewAdapter.parentFragmentManager)
        assertThat(userViewAdapter.getItemCount(), equalTo(0))
    }

    @Test
    fun userViewAdapterGetItemCountWorksOnNullList() {
        userViewAdapter = UserViewAdapter(null, userViewAdapter.parentFragmentManager)
        assertThat(userViewAdapter.getItemCount(), equalTo(0))
    }

    @Test
    fun userViewAdapterOnBindViewHolderWorksOnTrivialList() {
        val vH = userViewAdapter.onCreateViewHolder(LinearLayout(appContext), 0)
        userViewAdapter.onBindViewHolder(vH, 0)
        assertThat(vH.idView.text, equalTo("0"))
        assertThat(vH.contentView.text, equalTo("Test"))
    }

    //@Test
    //fun userViewAdapterToStringWorks() {
    //    val vH = userViewAdapter.onCreateViewHolder(LinearLayout(appContext), 0)
    //    assertThat((vH.toString()).substring(19), equalTo("position=-1 id=-1, oldPos=-1, pLpos:-1 unbound no parent} ''"))
    //}
    @Test
    fun userViewAdapterSetListenerWorks() {
        activityRule.scenario.onActivity {
            it.supportFragmentManager.commit {
                replace(R.id.mainFragmentContainer, UserFragment.newInstance(1, userList, true))
            }
        }

        onView(withId(R.id.user_fragment_user_list)).check(matches(isDisplayed()))

        onView(withId(R.id.user_fragment_user_list)).perform(RecyclerViewActions.actionOnItemAtPosition<UserViewAdapter.ViewHolder>(0, click()))

        onView(withId(R.id.fragment_profile_details_profile_name)).check(matches(withText("Bob")))
    }
}