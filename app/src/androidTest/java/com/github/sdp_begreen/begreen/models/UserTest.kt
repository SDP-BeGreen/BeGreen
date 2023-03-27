package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher.Companion.hasProp
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

//Need to be in Android Test to use Parcel
class UserTest {
    var user: User = User("1",  0, "Test")
    val photoMetadata: PhotoMetadata =
        PhotoMetadata("1", "title", ParcelableDate(Date()), User("1",  33, "Alice"), "Gros vilain pas beau", "desc")
    var user1: User = User(
        "1",
        33,
        "Alice",
        1,
        photoMetadata,
        "Description poutou poutou",
        "cc@gmail.com",
        "08920939459802",
        67,
        listOf(user),
        listOf(user)
    )

    @Before
    fun setup() {
        user = User("1",  0, "Test")
    }

    @Test
    fun userConstructorIsNotNull() {
        assertThat(User("0",  12, "default"), notNullValue())
    }

    @Test
    fun userToStringIsCorrect() {
        assertThat(user.toString(), equalTo("Test"))
        assertThat(user1.toString(), equalTo("Alice"))
    }

    @Test
    fun userCompareToOtherCorrectly() {
        val other = User("2",  1, "Test2")
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
    fun userGettersReturnsCorrectValues() {
        assertThat(user.id, equalTo(1))
        assertThat(user.displayName, equalTo("Test"))
        assertThat(user.rating, equalTo(0))
        assertThat(user.followers, equalTo(listOf<User>()))
        assertThat(user.following, equalTo(listOf<User>()))
    }

    @Test
    fun userGetCurrentUserReturnsCorrectValues() {
        User.currentUser = user
        assertThat(User.currentUser, equalTo(user))
    }

    @Test
    fun userSettersWorksCorrectly() {
        user.description = "test"
        user.phone = "test"
        user.email = "test"
        user.followers = listOf(user)
        user.following = listOf(user)
        user.rating = 1
        user.progression = 1
        assertThat(
            user, allOf(
                hasProp("description", equalTo("test")),
                hasProp("phone", equalTo("test")),
                hasProp("followers", equalTo(listOf(user))),
                hasProp("following", equalTo(listOf(user))),
                hasProp("rating", equalTo(1)),
                hasProp("progression", equalTo(1))
            )
        )

    }
}