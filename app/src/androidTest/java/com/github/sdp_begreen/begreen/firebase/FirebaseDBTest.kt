package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.core.app.launchActivity
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.models.ProfilePhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import kotlin.test.junit.JUnitAsserter.fail

@RunWith(AndroidJUnit4::class)
@LargeTest
class FirebaseDBTest {

    private val profilePhotoMetaData = ProfilePhotoMetadata()

    @get:Rule
    val koinTestRule = KoinTestRule()

    companion object {
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()
    }

    @Test
    fun setWithBlankKeyThrowIllegalArgument() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.set(" ", "Trying blank value")
            }
        }
    }

    @Test
    fun addUserBlankUserIdThrowIllegalArgument() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.addUser(User("1", 1, "test"), " ")
            }
        }
    }

    @Test
    fun getUserBlankUserIdThrowIllegalArgument() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.getUser( " ")
            }
        }
    }

    @Test
    fun storeUserProfilePictureBlankUserIdThrowIllegalArgument() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.storeUserProfilePicture(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888), " ", profilePhotoMetaData)
            }
        }
    }

    @Test
    fun userExistBlankUserIdThrowIllegalArgument() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.userExists( " ")
            }
        }
    }

    @Test
    fun getUserProfilePictureBlankUserIdThrowIllegalArgument() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.getUserProfilePicture(profilePhotoMetaData," ")
            }
        }
    }

    @Test
    fun retrieveUserAfterSetShouldMatch() {
        val user = User("1",  100, "User Test", "description", "0076286372", "test@email.com", 1, null, null)

        runBlocking {
            FirebaseDB.addUser(user, user.id)
            assertThat(FirebaseDB.getUser(user.id), `is`(equalTo(user)))
        }
    }

    @Test
    fun retrieveUserProfilePictureAfterAddShouldMatch() {
        val user = User("2", 10, "User Test 2")
        runBlocking {
            FirebaseDB.addUser(user, user.id)
        }

        // to be able to access resources, need to be in an activity
        launchActivity<MainActivity>().onActivity { activity ->
            val img: Bitmap = BitmapFactory.decodeResource(activity.resources, R.drawable.marguerite_test_image)

            runBlocking {
                val pictureUID = FirebaseDB.storeUserProfilePicture(img, user.id, profilePhotoMetaData)

                assertThat(pictureUID, notNullValue())

                // TODO equalsBitmap not working yet need to find a way to compare bitmap
                /*val retImg: Bitmap? = FirebaseDB.getUserProfilePicture(pictureUID!!, user.id)
                retImg?.also {
                    assertThat(it, equalsBitmap(img))
                }*/
            }
        }
    }

    @Test
    fun userExistsReturnFalseForNonExistingUserId() {
        runBlocking {
            assertFalse(FirebaseDB.userExists("Not Existing"))
        }
    }

    @Test
    fun freshlyAddedUserExistsInDatabase() {
        val user = User("existingUser",  10, "Existing User")

        runBlocking {
            FirebaseDB.addUser(user, user.id)
            assertTrue(FirebaseDB.userExists(user.id))
        }
    }

    @Test
    fun getImageWithEmptyPhotoIdReturnNull() {
        runBlocking {
            assertThat(FirebaseDB.getImage(profilePhotoMetaData), nullValue())
        }
    }

    @Test
    fun getProfilePictureEmptyPhotoIdReturnNull() {
        runBlocking {
            assertThat(FirebaseDB.getImage(profilePhotoMetaData), nullValue())
        }
    }

    // TODO: update tests with new implementation

    @Test
    fun addBinThrowsIllegalArgumentExceptionWhenBinIdIsNotNull() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                val bin = Bin("Not null ID", TrashCategory.ELECTRONIC, 4.3, 2.1)
                assertTrue(FirebaseDB.addBin(bin))
            }
        }
    }
    @Test
    fun addBinReturnsTrueWhenStoreSucceeds() {
        runBlocking {
            val bin = Bin(TrashCategory.ELECTRONIC, LatLng(4.3, 2.1))
            assertTrue(FirebaseDB.addBin(bin))
        }
    }

    @Test
    fun addBinUpdatesBinIdWhenStoreSucceeds() {
        runBlocking {
            val bin = Bin(TrashCategory.ELECTRONIC, LatLng(4.3, 2.1))
            assertTrue(FirebaseDB.addBin(bin))
            assertNotNull(bin.id)
        }
    }

    @Test
    fun addBinCorrectlyUpdatesDatabase() {

        val bin = Bin(TrashCategory.PAPER, LatLng(10.2, -4.2))

        runBlocking {

            FirebaseDB.addBin(bin)

            val bins = FirebaseDB.getAllBins()
            // Checks that the bin got correctly added
            assertThat(bins, hasItem(bin))
        }
    }

    @Test
    fun removeBinCorrectlyUpdatesDatabase() {

        val bin = Bin(TrashCategory.ORGANIC, LatLng(0.1, 89.9))

        runBlocking {

            FirebaseDB.addBin(bin)

            var bins = FirebaseDB.getAllBins()
            // Checks that the bin got correctly added
            assertThat(bins, hasItem(bin))

            FirebaseDB.removeBin(bin.id!!)

            bins = FirebaseDB.getAllBins()
            // Checks that the bin got removed
            assertThat(bins, not(hasItem(bin)))

        }
    }

    @Test
    fun getAllBinsReturnsPreviouslyAddedLocations() {

        runBlocking {

            val binLocations = FirebaseDB.getAllBins()

            // Checks that the location got correctly added
            assertThat(binLocations, hasItems(
                Bin("123", TrashCategory.PAPER, 69.6969,420.42),
                Bin("456", TrashCategory.METAL,123.456,654.321)
            ))
        }
    }

    @Test
    fun getAllUsersReturnNotEmptyListUser() {
        runBlocking {
            val users = FirebaseDB.getAllUsers()
            assertNotNull(users)
            assertThat(users.size, `is`(not(0)))
        }
    }

    @Test
    fun followThrowsIllegalArgumentExceptionWhenGivenBlankUserIdAsFirstArg() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.follow("", "Non existent ID")
            }
        }
    }

    @Test
    fun followDoesNotThrowExceptionWhenGivenNonExistentUserId() {
        runBlocking {
            try{
                FirebaseDB.follow("Non existent ID", "Non existent ID")
            } catch (_: Exception){
                fail("follow should not throw an exception with non blank user IDs")
            }
        }
    }

    @Test
    fun followCorrectlyAddsFollowerIdToFollowedUserAndFollowedIdToFollowerUser() {
        val user1 = User("user1 id", 5)
        val user2 = User("user2 id", 3)

        // Add both users to the DB
        runBlocking {
            FirebaseDB.addUser(user1, user1.id)
            FirebaseDB.addUser(user2, user2.id)
        }

        // Check that both users have no followers and following users
        assertThat(user1.followers, nullValue())
        assertThat(user1.following, nullValue())
        assertThat(user2.followers, nullValue())
        assertThat(user2.following, nullValue())

        runBlocking {
            FirebaseDB.follow(user1.id, user2.id)

            val user1_modified = FirebaseDB.getUser(user1.id)!!
            val user2_modified = FirebaseDB.getUser(user2.id)!!

            // Check that the ids got added in the correct fields
            assertThat(user1_modified.followers, nullValue())
            assertThat(user1_modified.following, containsInAnyOrder(user2.id))
            assertThat(user2_modified.followers, containsInAnyOrder(user1.id))
            assertThat(user2_modified.following, nullValue())
        }
    }

    @Test
    fun unfollowThrowsIllegalArgumentExceptionWhenGivenBlankUserIdAsFirstArg() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.unfollow("", "Non existent ID")
            }
        }
    }

    @Test
    fun unfollowDoesNotThrowExceptionWhenGivenNonExistentUserId() {
        runBlocking {
            try{
                FirebaseDB.unfollow("Non existent ID", "Non existent ID")
            } catch (_: Exception){
                fail("unfollow should not throw an exception with non blank user IDs")
            }
        }
    }

    @Test
    fun unfollowCorrectlyRemovesFollowerIdFromFollowedUserAndFollowedIdFromFollowerUser() {
        // user1 and user3 follow each other, user2 follows user1 and user3
        var user1 = User("id1", 5,
            followers = listOf("id2", "id3"), following = listOf("id3"))
        var user2 = User("id2", 3,
            followers = null, following = listOf("id1", "id3"))
        var user3 = User("id3", 0,
            followers = listOf("id1", "id2"), following = listOf("id1"))

        // Add all users to the DB
        runBlocking {
            FirebaseDB.addUser(user1, user1.id)
            FirebaseDB.addUser(user2, user2.id)
            FirebaseDB.addUser(user3, user3.id)

            // user1 starts following user2
            FirebaseDB.follow(user1.id, user2.id)

            user1 = FirebaseDB.getUser(user1.id)!!
            user2 = FirebaseDB.getUser(user2.id)!!

            // Check that the ids got added in the correct fields
            assertThat(user1.followers, containsInAnyOrder("id2", "id3"))
            assertThat(user1.following, containsInAnyOrder("id2", "id3"))
            assertThat(user2.followers, containsInAnyOrder("id1"))
            assertThat(user2.following, containsInAnyOrder("id1", "id3"))

            // user3 unfollows user1
            FirebaseDB.unfollow(user3.id, user1.id)

            user1 = FirebaseDB.getUser(user1.id)!!
            user3 = FirebaseDB.getUser(user3.id)!!

            // Check that the ids got removed in the correct fields
            assertThat(user1.followers, containsInAnyOrder("id2"))
            assertThat(user1.following, containsInAnyOrder("id2", "id3"))
            assertThat(user3.followers, containsInAnyOrder("id1", "id2"))
            // user3 does not follow anyone anymore
            assertThat(user3.following, nullValue())
        }
    }

    @Test
    fun getFollowedIdsThrowsIllegalArgumentExceptionWhenGivenBlankUserId() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.getFollowedIds("")
            }
        }
    }

    @Test
    fun getFollowedIdsThrowsReturnsEmptyListForNonExistingUser() {
        runBlocking{
            assertThat(FirebaseDB.getFollowedIds("Non existing User"), empty())
        }
    }

    @Test
    fun getFollowedIdsReturnsExpectedListOfUserIds() {

        val user = User("valid ID", 5,
            followers = listOf("abcd", "efgh"), following = listOf("ijkl", "mnop", "qrst"))

        runBlocking{
            FirebaseDB.addUser(user, user.id)
            assertThat(FirebaseDB.getFollowedIds(user.id),
                containsInAnyOrder("ijkl", "mnop", "qrst"))
        }
    }

    @Test
    fun getFollowerIdsThrowsIllegalArgumentExceptionWhenGivenBlankUserId() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                FirebaseDB.getFollowerIds("")
            }
        }
    }

    @Test
    fun getFollowerIdsThrowsReturnsEmptyListForNonExistingUser() {
        runBlocking{
            assertThat(FirebaseDB.getFollowerIds("Non existing User"), empty())
        }
    }

    @Test
    fun getFollowerIdsReturnsExpectedListOfUserIds() {

        val user = User("valid ID 2", 5,
            followers = listOf("abcd", "efgh"), following = listOf("ijkl", "mnop", "qrst"))

        runBlocking{
            FirebaseDB.addUser(user, user.id)
            assertThat(FirebaseDB.getFollowerIds(user.id),
                containsInAnyOrder("abcd", "efgh"))
        }
    }

    @Test
    fun getFollowersReturnsExpectedListOfUsers() {

        // user1 and user3 follow each other, user2 follows user1 and user3
        val user1 = User("ID1", 5, "user1",
            followers = listOf("ID2", "ID3"), following = listOf("ID3"))
        val user2 = User("ID2", 3, "user2",
            followers = null, following = listOf("ID1", "ID3"))
        val user3 = User("ID3", 0, "user3",
            followers = listOf("ID2", "ID1"), following = listOf("ID1"))


        runBlocking{

            FirebaseDB.addUser(user1, user1.id)
            FirebaseDB.addUser(user2, user2.id)
            FirebaseDB.addUser(user3, user3.id)

            val user1Followers = FirebaseDB.getFollowers(user1.id)
            assertThat(user1.followers!!.size, `is`(2))
            assertThat(user1Followers, containsInAnyOrder(user2, user3))
        }
    }

    @Test
    fun addFeedbackDoesNotThrowException() {
        try {
            runBlocking {
                FirebaseDB.addFeedback("Feedback", "userId", "date")
            }
        } catch (_: Exception){
            fail("addFeeback should not throw exception")
        }
    }

}