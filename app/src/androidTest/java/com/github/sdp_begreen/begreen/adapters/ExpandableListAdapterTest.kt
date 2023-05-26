package com.github.sdp_begreen.begreen.adapters

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.models.Tips
import com.github.sdp_begreen.begreen.models.TipsGroup
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ExpandableListAdapterTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val NB_OF_TIPS_PER_GROUP = 5
    private val NB_OF_GROUPS = 10
    private val tipsList = List(NB_OF_TIPS_PER_GROUP) { Tips("test") }
    private val tipsGroup = TipsGroup(0, "Group test", tipsList)
    private val tipsGroups = List(NB_OF_GROUPS) { tipsGroup }

    private val adapter = ExpandableListAdapter(context, tipsGroups)

    @Test
    fun testGetGroupCount() {

        assertThat(adapter.groupCount, `is`(NB_OF_GROUPS))
    }

    @Test
    fun testGetChildrenCountReturnsCorrectValueForTrivialGroupIndex() {

        assertThat(adapter.getChildrenCount(0), `is`(1))
    }

    @Test
    fun testGetChildrenCountThrowsIllegalArgumentExceptionForNegativeGroupIndex() {

        assertThrows(IllegalArgumentException::class.java) {
            adapter.getChildrenCount(-1)
        }
    }

    @Test
    fun testGetChildrenCountThrowsIllegalArgumentExceptionForNonExistingGroupIndex() {

        assertThrows(IllegalArgumentException::class.java) {
            adapter.getChildrenCount(NB_OF_GROUPS + 1)
        }
    }

    @Test
    fun testGetGroupReturnsExpectedGroupForTrivialGroupIndex() {

        assertThat(adapter.getGroup(0), `is`(tipsGroups[0]))
    }

    @Test
    fun testGetGroupThrowsIllegalArgumentExceptionForNegativeGroupIndex() {

        assertThrows(IllegalArgumentException::class.java) {
            adapter.getGroup(-1)
        }
    }

    @Test
    fun testGetGroupThrowsIllegalArgumentExceptionForNonExistingGroupIndex() {

        assertThrows(IllegalArgumentException::class.java) {
            adapter.getGroup(NB_OF_GROUPS + 1)
        }
    }

    @Test
    fun testGetChildReturnsChildForTrivialGroupIndex() {

        assertThat(adapter.getChild(0, 3), instanceOf(Tips::class.java))
    }

    @Test
    fun testGetChildThrowsIllegalArgumentExceptionForNegativeGroupIndex() {

        assertThrows(IllegalArgumentException::class.java) {
            adapter.getChild(-1, 3)
        }
    }

    @Test
    fun testGetChildThrowsIllegalArgumentExceptionForNonExistingGroupIndex() {

        assertThrows(IllegalArgumentException::class.java) {
            adapter.getChild(NB_OF_GROUPS + 1, 3)
        }
    }

    @Test
    fun testGetChildIdReturnsChildForTrivialGroupIndex() {

        assertThat(adapter.getChildId(0, 3), `is`(3))
    }

    @Test
    fun testGetChildIdThrowsIllegalArgumentExceptionForNegativeGroupIndex() {

        assertThrows(IllegalArgumentException::class.java) {
            adapter.getChildId(-1, 3)
        }
    }

    @Test
    fun testGetChildIdThrowsIllegalArgumentExceptionForNonExistingGroupIndex() {

        assertThrows(IllegalArgumentException::class.java) {
            adapter.getChildId(NB_OF_GROUPS + 1, 3)
        }
    }

    @Test
    fun testHasStableIdsReturnsFalse() {

        // We prefer to use assertThat is false instead of assertFalse because it makes more sense in this case.
        assertThat(adapter.hasStableIds(), `is`(false))
    }

    @Test
    fun testIsChildSelectableReturnsTrue() {

        // We prefer to use assertThat is true instead of assertTrue because it makes more sense in this case.
        assertThat(adapter.isChildSelectable(0, 0), `is`(true))
    }
}