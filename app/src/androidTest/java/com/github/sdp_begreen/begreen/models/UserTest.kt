package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.Photo
import com.github.sdp_begreen.begreen.models.User
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.TypeSafeMatcher
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
    fun userConstructorIsNotNull() {
        assertThat(User(0,"default", 12), notNullValue())
    }
    @Test
    fun userToStringIsCorrect() {
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
    fun userGettersReturnsCorrectValues() {
        assertThat(user.id, equalTo(1))
        assertThat(user.name, equalTo("Test"))
        assertThat(user.rating, equalTo(0))
        assertThat(user.followers, equalTo(listOf<User>()))
        assertThat(user.following, equalTo(listOf<User>()))
    }

    @Test
    fun userSettersWorksCorrectly(){
        user.description = "test"
        user.phone = "test"
        user.email = "test"
        user.followers = listOf(user)
        user.following = listOf(user)
        user.rating = 1
        user.img = photo
        user.progression = 1
        assertThat(user, allOf(
            HasProp("description", equalTo("test")),
            HasProp("phone", equalTo("test")),
            HasProp("followers", equalTo(listOf(user))),
            HasProp("following", equalTo(listOf(user))),
            HasProp("rating", equalTo(1)),
            HasProp("img", equalTo(photo)),
            HasProp("progression", equalTo(1))
        ))

    }
    @Suppress("UNCHECKED_CAST")
    fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
        return instance.javaClass.getMethod("get" + propertyName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }).invoke(instance) as R
    }

    inner class HasProp<T>(private val name: String, private val matcher: Matcher<*>): TypeSafeMatcher<T>() {
        override fun describeTo(description: Description?) {
            description?.appendText("Check that given elem has property name")
        }

        override fun matchesSafely(item: T?): Boolean {
            return matcher.matches(readInstanceProperty(item!!, name))
        }
    }

}