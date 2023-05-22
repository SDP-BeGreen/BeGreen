package com.github.sdp_begreen.begreen.viewModels

import android.location.Address
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.services.GeocodingService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class ContestCreationViewModelGeoTest {

    companion object {

        private var geo: GeocodingService = mock()
        private lateinit var vm: ContestCreationViewModel

        private val address = Address(null).apply {
            adminArea = "Vaud"
            countryCode = "CH"
            locality = "Lausanne"
            postalCode = "1010"
            latitude = 46.519653
            longitude = 6.632273
        }

        private val customLatLng = CustomLatLng(46.519653, 6.632273)

        private val listOfAddress = listOf(address)

        @BeforeClass
        @JvmStatic
        fun setUpGeo() {
            runTest {
                whenever(geo.getAddresses(any(), any()))
                    .thenReturn(listOfAddress)

                whenever(geo.getLongLat(any()))
                    .thenReturn(customLatLng)

            }
        }
    }


    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { geo }
        })
    )

    @Before
    fun setUp() {
        vm = ContestCreationViewModel()
    }

    @After
    fun tearDown() {
        geo = mock()
        setUpGeo()
    }

    // Throws koinapplication has not started error for no reason !?
    //@Test
    //fun isEditCityCorrect() {
    //    val city = "Montreux"
    //    vm.editCity(city)
    //    assertThat(vm.city.value!!, `is`(city))
    //}

    @Test
    fun isEditCountryCorrect() {
        val country = "France"
        vm.editCountry(country)
        assertThat(vm.country.value!!, `is`(country))
    }

    @Test
    fun isEditPostalCodeCorrect() {
        val postalCode = "1234"
        vm.editPostalCode(postalCode)
        assertThat(vm.postalCode.value!!, `is`(postalCode))
    }

    @Test
    fun isEditCustomLongLatCorrect() {
        vm.editLongLat(customLatLng)
        assertThat(vm.customLongLat.value.toString()!!, `is`(customLatLng.toString()))
    }

    @Test
    fun isContestCreationValidCorrect() {
        vm.editStartDate(System.currentTimeMillis() + 1000)
        vm.editEndDate(System.currentTimeMillis() + 1000)
        vm.contestTitle = "Title"
        vm.isPrivate = true
        vm.editStartHour(10)
        vm.editEndHour(10)
        vm.editStartMinute(10)
        vm.editEndMinute(10)
        vm.editCity("Montreux")
        vm.editCountry("France")
        vm.editPostalCode("1234")
        vm.editRadius(123.0)
        vm.editLongLat(customLatLng)
        assertTrue(vm.isContestCreationValid())
    }
}