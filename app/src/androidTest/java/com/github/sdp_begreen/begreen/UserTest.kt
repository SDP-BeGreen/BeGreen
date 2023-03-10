package com.github.sdp_begreen.begreen

import android.os.Parcel
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test

//Need to be in Android Test to use Parcel
class UserTest {
    var  user: User = User(1, "Test", 0)

    @Test
    fun userToStringWorks() {
        assertThat(user.toString(), equalTo("Test"))
    }

    @Test
    fun userCompareToOtherCorrectly() {
        val other = User(2, "Test2", 1)
        assertThat(user.compareTo(other), equalTo(-1))
    }

    @Test
    fun userCompareToSelfCorrectly() {
        assertThat(user.compareTo(user), equalTo(0))
    }

    @Test
    fun userWriteToParcelCorrectly() {
        val parcel = Parcel.obtain()
        user.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = User(parcel)
        Assert.assertEquals(user, result)
    }

    @Test
    fun userDescribeContentsCorrectly() {
        assertThat(user.describeContents(), equalTo(0))
    }

    @Test
    fun userCreatorCreateFromParcelCorrectly() {
        val parcel = Parcel.obtain()
        user.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = User.CREATOR.createFromParcel(parcel)
        Assert.assertEquals(user, result)
    }

    @Test
    fun userCreatorNewArrayCorrectly() {
        val result = User.CREATOR.newArray(1)
        assertThat(result.size, equalTo(1))
    }
}