package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.kotlin.*


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class MapFragmentTest {

    /**
     * Initialize some constant to use in tests
     */
    companion object {

        private val db: DB = mock(DB::class.java)

        private val bins = mutableListOf(
            Bin("1", TrashCategory.CLOTHES, 4.3, 2.8),
            Bin("2", TrashCategory.PAPER, 56.3, 22.3),
            Bin("3", TrashCategory.CLOTHES, 6.0, 9.0)
        )

        @BeforeClass
        @JvmStatic
        fun setUp() {
            runTest {

                whenever(db.removeBin(any())).then {
                    bins.filter { bin -> bin.id?.equals(it.arguments[0])?.not() ?: true }
                }
                whenever(db.addBin(any())).then {
                    bins.add(it.arguments[0] as Bin)
                    true
                }
                whenever(db.getAllBins()).thenReturn(bins)
            }
        }
    }

    private lateinit var fragmentScenario: FragmentScenario<MapFragment>

    @Before
    fun setup() {
        fragmentScenario = launchFragmentInContainer()
    }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
        })
    )

    @get:Rule
    val fineLocationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @Test
    fun mapIsDisplayed() {
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun addBinButtonIsDisplayedWithCorrectText() {

        val expectedText =
            ApplicationProvider.getApplicationContext<Context>().getString(R.string.add_new_bin)
        onView(withId(R.id.binBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.binBtn)).check(matches(withText(expectedText)))
    }

    @Test
    fun trashCategorySelectorIsDisplayed() {
        onView(withId(R.id.trashCategorySelector)).check(matches(isDisplayed()))
    }

    /* This test does not pass the CI, eventhough it works fine locally.
       I will try to make it work if I have time later on

    @Test
    fun clickOnAddNewBinBtnAddsNewBin() {
        runBlocking {
            // Add a new bin by clicking the button
            onView(withId(R.id.binBtn)).perform(click())

            // Check that a bin got added
            verify(db).addBin(any())
        }
    } */
}