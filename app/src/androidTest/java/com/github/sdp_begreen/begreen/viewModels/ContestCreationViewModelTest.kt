package com.github.sdp_begreen.begreen.viewModels

import android.location.Address
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.services.GeocodingService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import java.text.SimpleDateFormat

@RunWith(AndroidJUnit4::class)
@SmallTest
class ContestCreationViewModelTest {

    private fun fromLongToFormattedDate(date: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(date)
    }

    companion object {

        private val geo: GeocodingService = mock()
        private lateinit var vm: ContestCreationViewModel

        private val address = Address( null).apply {
            adminArea = "Vaud"
            countryCode = "CH"
            locality = "Lausanne"
            postalCode = "1010"
            latitude = 46.519653
            longitude = 6.632273
        }

        private val customLatLng = CustomLatLng(46.519653, 6.632273)

        private val listOfAddress = listOf(address)

        @OptIn(ExperimentalCoroutinesApi::class)
        @BeforeClass
        @JvmStatic
        fun setUpGeo() {
            // The implementation need to be provided before the rule is executed,
            // that's why we do it in the beforeClass method
            runTest {

                Mockito.`when`(geo.getAddresses(any(), any()))
                    .thenReturn(listOfAddress)

                Mockito.`when`(geo.getLongLat(any()))
                    .thenReturn(customLatLng)

                Mockito.`when`(geo.getLongLat(anyString())).thenReturn(null)

            }
        }

    }

    @get:Rule
    val koinTestRule = com.github.sdp_begreen.begreen.rules.KoinTestRule(
        modules = listOf(module {
            single { geo }
        })
    )

    @Before
    fun setUpVM() {
        vm = ContestCreationViewModel()
    }

    @Test
    fun isCityInitiallyCorrect() {
        assertThat(vm.city.value, `is`("Lausanne"))
    }

    @Test
    fun isCountryInitiallyCorrect() {
        assertThat(vm.country.value, `is`("Switzerland"))
    }

    @Test
    fun isPostalCodeInitiallyCorrect() {
        assertThat(vm.postalCode.value, `is`("1010"))
    }

    @Test
    fun isRadiusInitiallyCorrect() {
        assertThat(vm.radius.value, `is`(1000.0))
    }

    @Test
    fun isLatLongInitiallyCorrect() {
        assertThat(vm.customLongLat.value, `is`(nullValue()))
    }

    @Test
    fun isStartDateInitiallyCorrect() {
        assertThat(vm.startDate.value, `is`(nullValue()))
    }

    @Test
    fun isEndDateInitiallyCorrect() {
        assertThat(vm.endDate.value, `is`(nullValue()))
    }

    @Test
    fun isStartHourInitiallyCorrect() {
        assertThat(vm.startHour.value, `is`(nullValue()))
    }

    @Test
    fun isStartMinuteInitiallyCorrect() {
        assertThat(vm.startMinute.value, `is`(nullValue()))
    }

    @Test
    fun isEndHourInitiallyCorrect() {
        assertThat(vm.endHour.value, `is`(nullValue()))
    }

    @Test
    fun isEndMinuteCorrect() {
        assertThat(vm.endMinute.value, `is`(nullValue()))
    }

    @Test
    fun isEditStartDateCorrect() {
        val mili = System.currentTimeMillis() + 100
        val date = fromLongToFormattedDate(mili)
        vm.editStartDate(mili)
        assertThat(fromLongToFormattedDate(vm.startDate.value!!), `is`(date))
    }

    @Test
    fun isEditStartDateRefusingInvalidInput() {
        val mili = System.currentTimeMillis() - 10000000000000
        val date = null
        assertThat(vm.editStartDate(mili), `is`(false))
        assertThat(vm.editStartDate(date), `is`(false))

    }

    @Test
    fun isEditEndDateCorrect() {
        val mili = System.currentTimeMillis() + 100
        val date = fromLongToFormattedDate(mili)
        vm.editEndDate(mili)
        assertThat(fromLongToFormattedDate(vm.endDate.value!!), `is`(date))
    }

    @Test
    fun isEditEndDateRefusingInvalidInput() {
        val mili = System.currentTimeMillis() - 10000000000000
        val date = null
        assertThat(vm.editEndDate(mili), `is`(false))
        assertThat(vm.editEndDate(date), `is`(false))

    }


    @Test
    fun isEditStartHourCorrect() {
        val hour = 10
        vm.editStartHour(hour)
        assertThat(vm.startHour.value!!, `is`(hour))
    }

    @Test
    fun isEditStartHourRefusingInvalidInput() {
        val hour = 25
        assertThat(vm.editStartHour(null), `is`(false))
        assertThat(vm.editStartHour(hour), `is`(false))

    }

    @Test
    fun isEditEndHourCorrect() {
        val hour = 10
        vm.editEndHour(hour)
        assertThat(vm.endHour.value!!, `is`(hour))
    }

    @Test
    fun isEditEndHourRefusingInvalidInput() {
        val hour = 25
        assertThat(vm.editEndHour(null), `is`(false))
        assertThat(vm.editEndHour(hour), `is`(false))

    }

    @Test
    fun isEditStartMinuteCorrect() {
        val minutes = 10
        vm.editStartMinute(minutes)
        assertThat(vm.startMinute.value!!, `is`(minutes))
    }

    @Test
    fun isEditStartMinuteRefusingInvalidInput() {
        val minutes = 100
        assertThat(vm.editStartMinute(null), `is`(false))
        assertThat(vm.editStartMinute(minutes), `is`(false))

    }

    @Test
    fun isEditEndMinuteCorrect() {
        val minutes = 10
        vm.editEndMinute(minutes)
        assertThat(vm.endMinute.value!!, `is`(minutes))
    }

    @Test
    fun isEditEndMinuteRefusingInvalidInput() {
        val minutes = 100
        assertThat(vm.editEndMinute(null), `is`(false))
        assertThat(vm.editEndMinute(minutes), `is`(false))

    }

    //@Test
    //fun isEditCityCorrect() {
    //    val city = "Montreux"
    //    vm.editCity(city)
    //    assertThat(vm.city.value!!, `is`(city))
    //}

    @Test
    fun isEditCityRefusingInvalidInput() {
        val city = "Montreux123"
        assertThat(vm.editCity(null), `is`(false))
        assertThat(vm.editCity(city), `is`(false))

    }

    //@Test
    //fun isEditCountryCorrect() {
    //    val country = "France"
    //    vm.editCountry(country)
    //    assertThat(vm.country.value!!, `is`(country))
    //}

    @Test
    fun isEditCountryRefusingInvalidInput() {
        val country = "France123"
        assertThat(vm.editCountry(null), `is`(false))
        assertThat(vm.editCountry(country), `is`(false))

    }

    //@Test
    //fun isEditPostalCodeCorrect() {
    //    val postalCode = "1234"
    //    vm.editPostalCode(postalCode)
    //    assertThat(vm.postalCode.value!!, `is`(postalCode))
    //}

    @Test
    fun isEditPostalCodeRefusingInvalidInput() {
        val postalCode = "1234a"
        assertThat(vm.editPostalCode(null), `is`(false))
        assertThat(vm.editPostalCode(postalCode), `is`(false))

    }

    @Test
    fun isEditRadiusCorrect() {
        val radius = 123.0
        vm.editRadius(radius)
        assertThat(vm.radius.value!!, `is`(radius))
    }

    @Test
    fun isEditRadiusRefusingInvalidInput() {
        val radius = -123.0
        assertThat(vm.editRadius(null), `is`(false))
        assertThat(vm.editRadius(radius), `is`(false))

    }

    //@Test
    //fun isEditCustomLongLatCorrect() {
    //    vm.editLongLat(customLatLng)
    //    assertThat(vm.customLongLat.value.toString()!!, `is`(customLatLng.toString()))
    //}

    @Test
    fun isEditCustomLongLatRefusingInvalidInput() {
        val longLat = CustomLatLng(-200.0, -200.0)
        assertThat(vm.editLongLat(null), `is`(false))
        assertThat(vm.editLongLat(longLat), `is`(false))

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
        assertThat(vm.isContestCreationValid(), `is`(true))
    }


}