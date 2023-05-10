package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import com.github.sdp_begreen.begreen.models.ParcelableDate
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
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
}