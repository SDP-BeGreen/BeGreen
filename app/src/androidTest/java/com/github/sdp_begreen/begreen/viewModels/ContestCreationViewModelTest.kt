package com.github.sdp_begreen.begreen.viewModels

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.sdp_begreen.begreen.models.CustomLatLng
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import kotlin.test.assertFalse


@RunWith(AndroidJUnit4::class)
@SmallTest
class ContestCreationViewModelTest {

    private fun fromLongToFormattedDate(date: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(date)
    }

    companion object {
        private lateinit var vm: ContestCreationViewModel
    }


    @Before
    fun setUp() {
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
        assertFalse(vm.editStartDate(mili))
        assertFalse(vm.editStartDate(date))

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
        assertFalse(vm.editEndDate(mili))
        assertFalse(vm.editEndDate(date))

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
        assertFalse(vm.editStartHour(null))
        assertFalse(vm.editStartHour(hour))

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
        assertFalse(vm.editEndHour(null))
        assertFalse(vm.editEndHour(hour))

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
        assertFalse(vm.editStartMinute(null))
        assertFalse(vm.editStartMinute(minutes))

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
        assertFalse(vm.editEndMinute(null))
        assertFalse(vm.editEndMinute(minutes))

    }

    @Test
    fun isEditCityRefusingInvalidInput() {
        val city = "Montreux123"
        assertFalse(vm.editCity(null))
        assertFalse(vm.editCity(city))

    }


    @Test
    fun isEditCountryRefusingInvalidInput() {
        val country = "France123"
        assertFalse(vm.editCountry(null))
        assertFalse(vm.editCountry(country))

    }


    @Test
    fun isEditPostalCodeRefusingInvalidInput() {
        val postalCode = "1234a"
        assertFalse(vm.editPostalCode(null))
        assertFalse(vm.editPostalCode(postalCode))

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
        assertFalse(vm.editRadius(null))
        assertFalse(vm.editRadius(radius))

    }


    @Test
    fun isEditCustomLongLatRefusingInvalidInput() {
        val longLat = CustomLatLng(-200.0, -200.0)
        assertFalse(vm.editLongLat(null))
        assertFalse(vm.editLongLat(longLat))

    }
}