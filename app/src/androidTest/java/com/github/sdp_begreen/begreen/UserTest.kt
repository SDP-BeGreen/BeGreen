package com.github.sdp_begreen.begreen

import android.os.Parcel
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.Photo
import com.github.sdp_begreen.begreen.models.User
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasProperty
import org.junit.Before
import org.junit.Test
import java.util.*

//Need to be in Android Test to use Parcel
class UserTest {
    var  user: User = User(1, "Test", 0)
    val photo: Photo = Photo("1", ParcelableDate(Date()), User(1, "Alice", 33, ), "Gros vilain pas beau")
    var user1: User = User(1, "Alice", 33, 1, photo, "Description poutou poutou", "cc@gmail.com", "08920939459802", 67, listOf(user), listOf(user))

    @Before
    fun setup() {
        user = User(1, "Test", 0)
    }

    @Test
    fun userConstructorWorks() {
        assertThat(User(0,"default", 12), notNullValue())
    }
    @Test
    fun userToStringWorks() {
        assertThat(user.toString(), equalTo("Test"))
        assertThat(user1.toString(), equalTo("Alice"))
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
        assertThat(user, equalTo(result))
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
        assertThat(user, equalTo(result))
    }

    @Test
    fun userCreatorNewArrayCorrectly() {
        val result = User.CREATOR.newArray(1)
        assertThat(result.size, equalTo(1))
    }

    @Test
    fun userGettersWorks() {
        assertThat(user.id, equalTo(1))
        assertThat(user.name, equalTo("Test"))
        assertThat(user.rating, equalTo(0))
        assertThat(user.followers, equalTo(listOf<User>()))
        assertThat(user.following, equalTo(listOf<User>()))
    }

    @Test
    fun userSettersWorks(){
        user.description = "test"
        user.phone = "test"
        user.email = "test"
        user.followers = listOf(user)
        user.following = listOf(user)
        user.rating = 1
        user.img = photo
        user.progression = 1
        assertThat(user, allOf(
                hasProperty("rating", equalTo(1)),
                hasProperty("followers", equalTo(listOf(user))),
                hasProperty("following", equalTo(listOf(user))),
                hasProperty("description", equalTo("test")),
                hasProperty("phone", equalTo("test")),
                hasProperty("email", equalTo("test")),
                hasProperty("img", equalTo(photo)),
                hasProperty("progression", equalTo(1))))

    }
}