package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.map.BinType
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.ArgumentMatchers.*



//@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class MapFragmentTest {

    /**
     * Initialize some constant to use in tests
     */
    companion object {

        private val db: DB = Mockito.mock(DB::class.java)

        private val bins = mutableListOf(
            Bin("1", BinType.CLOTHES, 4.3, 2.8),
            Bin("2", BinType.PAPER, 56.3, 22.3),
            Bin("3", BinType.CLOTHES, 6.0, 9.0)
        )


        /*
        @BeforeClass
        @JvmStatic
        fun setUp() {
            runTest {

                Mockito.`when`(db.removeBin(anyString())).then {
                    bins.filter { bin -> bin.id?.equals(it.arguments[0])?.not() ?: true }
                }
                Mockito.`when`(db.addBin(any(Bin::class.java) )).thenReturn(true)
                Mockito.`when`(db.getAllBins()).thenReturn(bins)
            }
        } Doesnt work, for strange reasons */
    }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single {db}
        })
    )

    @get:Rule
    val fineLocationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    private val fragment = MapFragment()


    @Test
    fun testFragmentInflation() {

        /* The googlemaps library is quite hard to test. For now, we have only tested the launching of the mapFragment.

        According to the bootcamp https://github.com/sweng-epfl/public/blob/main/project/bootcamp/Maps.md :

        [...] since you cannot set the inputs, it will be difficult to achieve 100% of coverage.
        Therefore, we advise you to write as much as possible code that is independent from map components, and that you can easily test.
        */


        runBlocking {

            Mockito.`when`(db.getAllBins()).thenReturn(bins)

            launchFragmentInContainer { fragment }

            // Wait until the map fragment is displayed
            onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))
        }
    }

    /*

    This test doesn't pas CI tests for now but only local tests

    @Test
    fun clickOnAddNewBinBtnAddsNewBin() {

        val nbOfBinsOld = BinsFakeDatabase.fakeBins.size

        launchFragmentInContainer { fragment }

        // Wait until the map fragment is displayed
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))

        onView(withId(R.id.addNewBinBtn)).check(matches(isDisplayed()))

        // Click on the addNewBinBtn
        onView(withId(R.id.addNewBinBtn)).perform(ViewActions.click())

        val nbOfBinsNew = BinsFakeDatabase.fakeBins.size

        // Check that a new bin have been added
        assertThat(nbOfBinsNew, greaterThan(nbOfBinsOld))
    }

    */

    // After some researches, it seems that detecting a click on a googlemaps marker is not possible
    // in a clean way and is quite difficult. Therefore, the remove bin cannot be tested.
}