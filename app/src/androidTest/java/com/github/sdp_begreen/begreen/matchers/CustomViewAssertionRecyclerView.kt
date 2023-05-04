package com.github.sdp_begreen.begreen.matchers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import kotlin.test.fail

/**
 * Custom view assertion, used to assert a [TextView]'s text inside a view contained in a
 * recycler view at a given position
 */
class TextViewAssertionView(
    private val position: Int,
    private val id: Int,
    private val text: String?
) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view !is RecyclerView)
            fail("The view ${view?.toString()}, is not a recycler view")

        val elemView = view.findViewHolderForAdapterPosition(position)?.itemView
            ?: fail("No view found at position: $position")

        assertThat(
            elemView.findViewById<TextView>(id).text,
            `is`(equalTo(text))
        )
    }

    companion object {
        fun atPositionTextViewWithText(position: Int, id: Int, text: String?): ViewAssertion =
            TextViewAssertionView(position, id, text)
    }
}

/**
 * Custom view assertion, used to assert a button's text inside a view contained in a
 * recycler view at a given position
 */
class ButtonAssertionView(
    private val position: Int,
    private val id: Int,
    private val text: String?
) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view !is RecyclerView)
            fail("The view ${view?.toString()} is not a recycler view")

        val elemView = view.findViewHolderForAdapterPosition(position)?.itemView
            ?: fail("No view found at position: $position")

        assertThat(
            elemView.findViewById<TextView>(id).text,
            `is`(equalTo(text))
        )
    }

    companion object {
        fun atPositionButtonWithText(position: Int, id: Int, text: String?): ViewAssertion =
            TextViewAssertionView(position, id, text)
    }
}