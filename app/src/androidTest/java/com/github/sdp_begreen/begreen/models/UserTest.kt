package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher.Companion.hasProp
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

//Need to be in Android Test to use Parcel
class UserTest {

    private val profilePhotoMetadata =
        ProfilePhotoMetadata("1", ParcelableDate.now, "0")

    private val trashPhotoMetadata = TrashPhotoMetadata("1")

    private val trashPhotosMetadatasList = mutableListOf<TrashPhotoMetadata>(trashPhotoMetadata)

    var user: User = User(
        "1",
        0,
        "Test",
        "desc",
        "phone",
        "email",
        50,
        null,
        null,
    null,
    null)

    var user1: User = User(
        "1",
        33,
        "Alice",
        "Description poutou poutou",
        "08920939459802",
        "cc@gmail.com",
        67,
        listOf(user.id),
        listOf(user.id),
        profilePhotoMetadata,
        trashPhotosMetadatasList
    )

    @Test
    fun userConstructorIsNotNull() {
        assertThat(User("0",  12, "default"), notNullValue())
    }

    @Test
    fun userToStringIsCorrect() {
        assertThat(user.toString(), equalTo("Test"))
        assertThat(user1.toString(), equalTo("Alice"))
        assertThat(User("0",  12).toString(), equalTo("Username"))
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
    fun userDescribeContentsCorrectly() {
        assertThat(user.describeContents(), equalTo(0))
    }

    @Test
    fun userGettersReturnsCorrectValues() {
        assertThat(user.id, equalTo("1"))
        assertThat(user.score, equalTo(0))
        assertThat(user.displayName, equalTo("Test"))
        assertThat(user.description, equalTo("desc"))
        assertThat(user.phone, equalTo("phone"))
        assertThat(user.email, equalTo("email"))
        assertThat(user.progression, equalTo(50))
        assertThat(user.followers, equalTo(null))
        assertThat(user.following, equalTo(null))
        assertThat(user.profilePictureMetadata, equalTo(null))
        assertThat(user.trashPhotosMetadatasList, equalTo(null))
    }


    @Test
    fun userGetCurrentUserReturnsCorrectValues() {
        User.currentUser = user
        assertThat(User.currentUser, equalTo(user))
    }

    @Test
    fun addFirstTrashPhotoMetadataWhenListWasNull() {

        val user2 = user.copy(trashPhotosMetadatasList = null)

        assertThat(user2.trashPhotosMetadatasList, `is`(nullValue()))
        user2.addPhotoMetadata(trashPhotoMetadata)
        assertThat(user2.trashPhotosMetadatasList, hasItem(trashPhotoMetadata))
    }

    @Test
    fun addTrashPhotoMetadataWhenListWasNotNullAndNotEmpty() {

        val user2 = user.copy(trashPhotosMetadatasList = mutableListOf(trashPhotoMetadata))
        val newTrashPhotoMetadata = TrashPhotoMetadata("2")

        assertThat(user2.trashPhotosMetadatasList, not(hasItem(newTrashPhotoMetadata)))
        user2.addPhotoMetadata(newTrashPhotoMetadata)
        assertThat(user2.trashPhotosMetadatasList, hasItem(newTrashPhotoMetadata))
    }

    @Test
    fun addTrashPhotoMetadataWhenListWasEmpty() {

        val user2 = user.copy(trashPhotosMetadatasList = mutableListOf())
        val newTrashPhotoMetadata = TrashPhotoMetadata("2")

        assertThat(user2.trashPhotosMetadatasList, not(hasItem(newTrashPhotoMetadata)))
        user2.addPhotoMetadata(newTrashPhotoMetadata)
        assertThat(user2.trashPhotosMetadatasList, hasItem(newTrashPhotoMetadata))
    }

    @Test
    fun userSettersWorksCorrectly() {

        val user2 = user.copy()

        user2.score = 123
        user2.description = "test"
        user2.phone = "test"
        user2.email = "test"
        user2.progression = 123
        user2.followers = listOf(user.id)
        user2.following = listOf(user.id)
        user2.profilePictureMetadata = null
        user2.trashPhotosMetadatasList = null

        assertThat(
            user2, allOf(
                hasProp("score", equalTo(123)),
                hasProp("rating", equalTo(123)),
                hasProp("description", equalTo("test")),
                hasProp("phone", equalTo("test")),
                hasProp("email", equalTo("test")),
                hasProp("progression", equalTo(123)),
                hasProp("followers", equalTo(listOf(user.id))),
                hasProp("following", equalTo(listOf(user.id))),
                hasProp("profilePictureMetadata", `is`(nullValue())),
                hasProp("trashPhotosMetadatasList", `is`(nullValue())),
            )
        )
    }

    @Test
    fun testFollowNonFollowedUserAndNonNullList() {
        val user = User("1", 5, following = listOf("3"))

        // Check that the user is not following user with id "2"
        assertThat(user.following, not(hasItem("2")))

        user.follow("2")

        // Check that the user is now following user with id "2"
        assertThat(user.following, hasItem("2"))
    }

    @Test
    fun testFollowAlreadyFollowedUser() {
        val user = User("1", 5, following = listOf("2"))

        // Check that the user is already following user with id "2"
        assertThat(user.following, hasItem("2"))

        user.follow("2")

        // Check that the user is still following user with id "2"
        assertThat(user.following, hasItem("2"))
    }

    @Test
    fun testFollowWithEmptyFollowings() {
        val user = User("1", 5, following = listOf())

        // Check that the user is already following user with id "2"
        assertNotNull(user.following)
        assertTrue(user.following!!.isEmpty())

        user.follow("2")

        // Check that the user is still following user with id "2"
        assertThat(user.following, hasItem("2"))
    }

    @Test
    fun testUnfollowAlreadyFollowedUser() {

        val user = User("1", 5, following = listOf("2"))

        // Check that the user is already following user with id "2"
        assertThat(user.following, hasItem("2"))

        user.unfollow("2")

        // Check that the user is no more following user with id "2"
        assertThat(user.following, not(hasItem("2")))
    }

    @Test
    fun testUnfollowNonFollowedUserAndNonNullFollowings() {

        val user = User("1", 5, following = listOf("3"))

        // Check that the user is already following user with id "2"
        assertThat(user.following, not(hasItem("2")))

        user.unfollow("2")

        // Check that the user is no more following user with id "2"
        assertThat(user.following, not(hasItem("2")))
    }

    @Test
    fun testFollowWithNullFollowings() {

        val user = User("1", 5)

        // Check that the user followings are indeed null
        assertNull(user.following)

        user.follow("2")

        // Check that the user is now following user with id "2"
        assertThat(user.following, hasItem("2"))
    }

    @Test
    fun testUnfollowWithNullFollowings() {

        val user = User("1", 5)

        // Check that the user followings are indeed null
        assertNull(user.following)

        user.unfollow("2")

        // Check that the user followings are still null after unfollowing
        assertNull(user.following)
    }

    @Test
    fun testFollowBlankUserIdThrowsIllegalArgumentException() {

        val user = User("1", 5)

        val exception = assertThrows(IllegalArgumentException::class.java) {

            user.follow("")
        }

        assertThat(
            exception.message,
            `is`(equalTo("The userId cannot be blank"))
        )
    }

    @Test
    fun testUnfollowBlankUserIdThrowsIllegalArgumentException() {

        val user = User("1", 5)

        val exception = assertThrows(IllegalArgumentException::class.java) {

            user.unfollow("")
        }

        assertThat(
            exception.message,
            `is`(equalTo("The userId cannot be blank"))
        )
    }

    @Test
    fun testFollowThenUnfollowWorksCorrectly() {

        val user = User("1", 5, following = listOf("2"))

        val oldFollowings = user.following
        val newUserId = "3"

        user.follow(newUserId)
        user.unfollow(newUserId)

        assertThat(user.following, equalTo(oldFollowings))
    }

    @Test
    fun testUnfollowThenFollowWorksCorrectly() {

        val followingId = "2"
        val user = User("1", 5, following = listOf(followingId))

        val oldFollowings = user.following

        user.unfollow(followingId)
        user.follow(followingId)

        assertThat(user.following, equalTo(oldFollowings))
    }

    @Test
    fun testIsFollowingTrivialUserReturnsTrue() {

        val followingId = "2"
        val user = User("1", 5, following = listOf(followingId))

        assertTrue(user.isFollowing(followingId))
    }

    @Test
    fun testIsFollowingTrivialUserReturnsFalse() {

        val user = User("1", 5, following = listOf("2"))

        assertFalse(user.isFollowing("3"))
    }

    @Test
    fun testIsFollowingTrivialUserReturnsFalseIfFollowingsIsNull() {

        val user = User("1", 5)

        assertFalse(user.isFollowing("3"))
    }

    @Test
    fun testIsFollowingBlankUserThrowsIllegalArgumentException() {

        val user = User("1", 5, following = listOf("2"))

        val exception = assertThrows(IllegalArgumentException::class.java) {

            user.isFollowing("")
        }

        assertThat(
            exception.message,
            `is`(equalTo("The userId cannot be blank"))
        )
    }
}