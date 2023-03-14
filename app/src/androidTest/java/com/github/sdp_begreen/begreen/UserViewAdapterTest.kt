package com.github.sdp_begreen.begreen

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdp_begreen.begreen.fragments.HomeFragment
import com.github.sdp_begreen.begreen.fragments.UserFragment
import com.github.sdp_begreen.begreen.fragments.UserViewAdapter
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import kotlin.concurrent.thread

class UserViewAdapterTest {
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
        val args = Bundle().apply {
            putInt(UserFragment.ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(UserFragment.ARG_USER_LIST, userList.toCollection(ArrayList()))
            putBoolean(UserFragment.ARG_IS_LIST_SORTED_BY_SCORE, true)
        }
        //launchFragmentInContainer(args) {
        //    UserFragment()
        //}
        val scenario = launchFragmentInContainer<UserFragment>(args)
        onView(withId(R.id.userlist)).perform(click())
    }
}