package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.DatabaseActivity
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FirebaseDBTest {

    // For some reason to perform the write in the database, an activity has to be started
    @get:Rule
    val activityRule = ActivityScenarioRule(DatabaseActivity::class.java)

    companion object {
        @BeforeClass @JvmStatic fun setup() {
            try {
                Firebase.database.useEmulator("10.0.2.2", 9000)
                Firebase.storage.useEmulator("10.0.2.2", 9199)
                Firebase.auth.useEmulator("10.0.2.2", 9099)
            } catch (_:java.lang.IllegalStateException){}
        }
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
                FirebaseDB.storeUserProfilePicture(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888), " ", PhotoMetadata())
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
                FirebaseDB.getUserProfilePicture(PhotoMetadata(), " ")
            }
        }
    }

    @Test
    fun retrieveUserAfterSetShouldMatch() {
        val user = User("1",  100, "User Test", 10, null, "description", "0076286372", "test@email.com", 1, null, null)

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
        activityRule.scenario.onActivity { activity ->
            val img: Bitmap = BitmapFactory.decodeResource(activity.resources, R.drawable.marguerite_test_image)
            val photoMetadata = PhotoMetadata()

            runBlocking {
                val pictureUID = FirebaseDB.storeUserProfilePicture(img, user.id, photoMetadata)

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
            assertThat(FirebaseDB.getImage(PhotoMetadata(), 1), nullValue())
        }
    }

    @Test
    fun getProfilePictureEmptyPhotoIdReturnNull() {
        runBlocking {
            assertThat(FirebaseDB.getImage(PhotoMetadata(), 1), nullValue())
        }
    }

    // TODO: update tests with new implementation
    /*@Test
    fun storeBinLocationReturnsTrue() {
        runBlocking {
            assertTrue(FirebaseDB.storeBinLocation(LatLng(1.1, -2.2)))
        }
    }

    @Test
    fun storeBinLocationCorrectlyUpdatesDatabase() {

        val binLocation = LatLng(12.3, -43.0)

        runBlocking {

            assertTrue(FirebaseDB.storeBinLocation(binLocation))

            val binLocations = FirebaseDB.getAllBinLocations()
            assertNotNull(binLocations)
            // Checks that the location got correctly added
            assertThat(binLocations, hasItem(binLocation))
        }

    }

    @Test
    fun getAllBinLocationsReturnsPreviouslyAddedLocations() {

        runBlocking {

            val binLocations = FirebaseDB.getAllBinLocations()
            assertNotNull(binLocations)
            // Checks that the location got correctly added
            assertThat(binLocations, hasItems(
                LatLng(69.6969, 420.42),
                LatLng(123.456, 654.321)
            ))
        }
    }*/

    @Test
    fun getAllUsersReturnNotEmptyListUser() {
        runBlocking {
            val users = FirebaseDB.getAllUsers()
            assertNotNull(users)
            assertThat(users.size, `is`(not(0)))
        }
    }

}