package com.github.sdp_begreen.begreen.viewModels

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat

@RunWith(AndroidJUnit4::class)
@SmallTest
class ContestCreationViewModelTest {

    private lateinit var vm: ContestCreationViewModel

    private fun fromFormattedDateToLong(date: String): Long {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        if (date.contains(Regex("[a-zA-Z]"))) return 0
        val datetmp = formatter.parse(date)
        return datetmp?.time ?: 0
    }

    private fun fromLongToFormattedDate(date: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(date)
    }

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
        val mili = System.currentTimeMillis()+100
        val date = fromLongToFormattedDate(mili)
        vm.editStartDate(mili)
        assertThat(fromLongToFormattedDate( vm.startDate.value!!), `is`(date))
    }

    @Test
    fun isEditStartDateRefusingInvalidInput() {
        val mili = System.currentTimeMillis()-10000000000000
        val date = null
        assertThat(vm.editStartDate(mili), `is`(false))
        assertThat(vm.editStartDate(date), `is`(false))

    }

    @Test
    fun isEditEndDateCorrect() {
        val mili = System.currentTimeMillis()+100
        val date = fromLongToFormattedDate(mili)
        vm.editEndDate(mili)
        assertThat(fromLongToFormattedDate( vm.endDate.value!!), `is`(date))
    }

    @Test
    fun isEditEndDateRefusingInvalidInput() {
        val mili = System.currentTimeMillis()-10000000000000
        val date = null
        assertThat(vm.editEndDate(mili), `is`(false))
        assertThat(vm.editEndDate(date), `is`(false))

    }


}