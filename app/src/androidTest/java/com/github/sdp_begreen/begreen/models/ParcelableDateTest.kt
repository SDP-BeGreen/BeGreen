package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import com.github.sdp_begreen.begreen.models.ParcelableDate
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class ParcelableDateTest {

    @Test
    fun parcelableDateWriteToParcelCorrectly() {
        val date = ParcelableDate.now
        val parcel = Parcel.obtain()
        date.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val createdFromParcel = ParcelableDate.CREATOR.createFromParcel(parcel)
        assertThat(date.date?.time, equalTo(createdFromParcel.date?.time))
    }

    @Test
    fun parcelableDateDescribeContentsEqualsZero() {
        val date = ParcelableDate.now
        assertThat(date.describeContents(), equalTo(0))
    }

    @Test
    fun parcelableDateNewArrayHasCorrectSize() {
        val array = ParcelableDate.CREATOR.newArray(1)
        assertThat(array.size, equalTo(1))
    }

    @Test
    fun parelableDateConstructorWriteCorrectDate() {
        val date = Date()
        val parcel = Parcel.obtain()
        parcel.writeLong(date.time)
        parcel.setDataPosition(0)
        val createdFromParcel = ParcelableDate(parcel)
        assertThat(date.time, equalTo(createdFromParcel.date?.time))
    }

    @Test
    fun parcelableDateCreatorWriteCorrectDate() {
        val date = Date()
        val parcel = Parcel.obtain()
        parcel.writeLong(date.time)
        parcel.setDataPosition(0)
        val createdFromParcel = ParcelableDate.CREATOR.createFromParcel(parcel)
        assertThat(date.time, equalTo(createdFromParcel.date?.time))
    }

    @Test
    fun parcelableDateSetDateCorrectly() {
        val date = ParcelableDate.now
        val newDate = Date()
        date.date = newDate
        assertThat(date.date?.time, equalTo(newDate.time))
    }


    @Test
    fun parcelableDateComparableReturnsTrueForNewestDate() {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val dateString1 = "2023-05-01"
        val date1: Date = dateFormat.parse(dateString1)

        val dateString2 = "2023-05-10"
        val date2: Date = dateFormat.parse(dateString2)

        val parcelableDate1 = ParcelableDate(date1)
        val parcelableDate2 = ParcelableDate(date2)

        assertTrue(parcelableDate1 < parcelableDate2)
    }

    @Test
    fun toStringReturnsExpectedValue() {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val dateString1 = "2023-05-01"
        val date: Date = dateFormat.parse(dateString1)
        val parcelableDate = ParcelableDate(date)

        val expected = date.toString().substring(4,16)

        assertThat(parcelableDate.toString(), equalTo(expected))
    }
}