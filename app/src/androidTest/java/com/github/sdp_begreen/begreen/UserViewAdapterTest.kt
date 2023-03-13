package com.github.sdp_begreen.begreen

import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdp_begreen.begreen.fragments.UserViewAdapter
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class UserViewAdapterTest {
    private var userViewAdapter = UserViewAdapter(listOf(User(1, "Test", 0), User(2, "Test2", 1)), null)
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
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
}