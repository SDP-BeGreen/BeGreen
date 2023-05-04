package com.github.sdp_begreen.begreen.matchers

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.CoreMatchers.any
import org.hamcrest.Matcher
import kotlin.test.fail

/**
 * Custom view action, used to click a button  inside a view contained in a
 * recycler view at a given position
 */
class ButtonClickAction(
    private val position: Int,
    private val id: Int
) : ViewAction {
    override fun getDescription(): String {
        return "Click on button inside recycler view element at position $position"
    }

    override fun getConstraints(): Matcher<View> {
        return any(View::class.java)
    }

    override fun perform(uiController: UiController?, view: View) {
        if (view !is RecyclerView)
            fail("The view is not a recycler view")

        val elemView = view.findViewHolderForAdapterPosition(position)?.itemView
            ?: fail("No view found at position: $position")


        uiController.apply {
            elemView.findViewById<Button>(id).performClick()
        }

    }

    companion object {
        fun clickButtonIdAtPosition(position: Int, id: Int) = ButtonClickAction(position, id)
    }
}