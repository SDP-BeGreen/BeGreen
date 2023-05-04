package com.github.sdp_begreen.begreen.adapters

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@LargeTest
class FollowingArrayAdapterTest {

    @Test
    fun testGetView() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val adapter = FollowingArrayAdapter(context, R.layout.user_search_bar, listOf(
            User("user1", 1, "Alain Berset"),
            User("user2", 2, "Bob le bricoleur")
        ),
            mock(DB::class.java),
            mock(Auth::class.java),
            mock(LifecycleCoroutineScope::class.java),
            listOf(false, false)
        )

        val view = adapter.getView(0, null,  LinearLayout(context))
        val textView = view.findViewById<TextView>(R.id.item_text)

        assertThat("Alain Berset", `is`(textView.text))
    }
}