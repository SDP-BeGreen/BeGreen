package com.github.sdp_begreen.begreen

import android.os.Parcel
import com.github.sdp_begreen.begreen.models.ParcelableDate
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

class ParcelableDateTest {
    @Test
    fun parcelableDateWriteToParcelWorks() {
        val date = ParcelableDate(Date())
        val parcel = Parcel.obtain()
        date.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val createdFromParcel = ParcelableDate.CREATOR.createFromParcel(parcel)
        assertThat(date.date?.time, equalTo(createdFromParcel.date?.time))
    }

    @Test
    fun parcelableDateDescribeContentsWorks() {
        val date = ParcelableDate(Date())
        assertThat(date.describeContents(), equalTo(0))
    }

    @Test
    fun parcelableDateNewArrayWorks() {
        val date = ParcelableDate(Date())
        val array = ParcelableDate.CREATOR.newArray(1)
        assertThat(array.size, equalTo(1))
    }

    @Test
    fun parelableDateConstructorWorks() {
        val date = Date()
        val parcel = Parcel.obtain()
        parcel.writeLong(date.time)
        parcel.setDataPosition(0)
        val createdFromParcel = ParcelableDate(parcel)
        assertThat(date.time, equalTo(createdFromParcel.date?.time))
    }

    @Test
    fun parcelableDateCreatorWorks() {
        val date = Date()
        val parcel = Parcel.obtain()
        parcel.writeLong(date.time)
        parcel.setDataPosition(0)
        val createdFromParcel = ParcelableDate.CREATOR.createFromParcel(parcel)
        assertThat(date.time, equalTo(createdFromParcel.date?.time))
    }

    @Test
    fun parcelableDateSetDateWorks() {
        val date = ParcelableDate(Date())
        val newDate = Date()
        date.date = newDate
        assertThat(date.date?.time, equalTo(newDate.time))
    }
}