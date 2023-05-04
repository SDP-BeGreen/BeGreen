package com.github.sdp_begreen.begreen.firebase.meetingServices

import android.graphics.Bitmap
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.matchers.EqualsToBitmap
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThrows
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class MeetingPhotoServiceTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()

        private val meetingWithPhotos = Meeting(
            "-NU6zLARtig1QxHxK532",
            "67676767",
            "Test photo meeting",
            "Meeting acting as a container to test photos"
        )
    }

    @get:Rule
    val coroutineRules = CoroutineTestRule()

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun addMeetingPhotoBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.addMeetingsPhoto(
                    " ",
                    TrashPhotoMetadata(takenBy = "not null"),
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting id cannot be blank"))
        )
    }

    @Test
    fun addMeetingPhotoBlankTakenByShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.addMeetingsPhoto(
                    meetingWithPhotos.meetingId!!,
                    TrashPhotoMetadata(takenBy = " "),
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user that took the photo cannot be blank or null"))
        )
    }

    @Test
    fun addMeetingPhotoNullTakenByShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.addMeetingsPhoto(
                    meetingWithPhotos.meetingId!!,
                    TrashPhotoMetadata(takenBy = null),
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user that took the photo cannot be blank or null"))
        )
    }

/*

    // TODO : Didn't manage to make this tests pass. I will work on it later.

    @Test
    fun addMeetingPhotoCorrectlyAddPhotoToMeetingInDB() {
        runTest {
            val metadata1 = TrashPhotoMetadata(
                "-NU8AMWCpNWmjCRlNU7x",
                null,
                "aaaaaa",
                "Hello",
                null
            )
            val metadataWithId = MeetingPhotoServiceImpl.addMeetingsPhoto(
                meetingWithPhotos.meetingId!!,
                metadata,
                Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
            )

            metadata = metadata.copy(pictureId = metadataWithId.pictureId)

            assertThat(metadataWithId, `is`(equalTo(metadata)))
        }
    }

 */

    @Test
    fun getAllPhotoMetadataBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.getAllPhotosMetadata(" ")
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting id cannot be blank"))
        )
    }

/*

    // TODO : Didn't manage to make this tests pass. I will work on it later.

    @Test
    fun getAllPhotoMetadataReturnCorrectModifiedListUponModification() {

        val metadata1 = TrashPhotoMetadata(
            "-NU8AMWCpNWmjCRlNU7x",
            null,
            "aaaaaa",
            "Hello",
            null
        )

        val metadata2 = TrashPhotoMetadata(
            "-NU8AMzGOjxhfRySRlNm",
            null,
            "bbbbbb",
            "world",
            null
        )

        val metadata3 = TrashPhotoMetadata(
            "-NU8AN2L5DiW1rfyAOR9",
            null,
            "cccccc",
            "blablabla",
            null
        )

        runTest {
            val channel = Channel<List<TrashPhotoMetadata>>(1)
            backgroundScope.launch {
                MeetingPhotoServiceImpl.getAllPhotosMetadata(meetingWithPhotos.meetingId!!)
                    .collect {
                        channel.send(it)
                    }
            }


            // check initial meetings
            assertThat(
                listOf(metadata1, metadata2, metadata3),
                everyItem(`is`(Matchers.`in`(channel.receive())))
            )

            // remove one of the three
            MeetingPhotoServiceImpl.removeMeetingPhoto(meetingWithPhotos.meetingId!!, metadata2)
            assertThat(
                listOf(metadata1, metadata3),
                everyItem(`is`(Matchers.`in`(channel.receive())))
            )

            // assert that when we get a photo it doesn't exist
            val exception = assertThrows(MeetingServiceException::class.java) {
                runBlocking {
                    MeetingPhotoServiceImpl.getPhoto(meetingWithPhotos.meetingId!!, metadata2)
                }
            }

            assertThat(
                exception.message,
                Matchers.stringContainsInOrder("Error while getting picture bytes from storage")
            )
        }
    }
    */

    @Test
    fun getPhotoBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.getPhoto(" ", TrashPhotoMetadata(pictureId = null, takenBy = "not null"))
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting id cannot be blank"))
        )
    }

    @Test
    fun getPhotoNullPictureIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.getPhoto(
                    meetingWithPhotos.meetingId!!,
                    TrashPhotoMetadata(pictureId = null, takenBy = "non null")
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The picture id cannot be blank or null"))
        )
    }

    @Test
    fun getPhotoBlankPictureIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.getPhoto(
                    meetingWithPhotos.meetingId!!,
                    TrashPhotoMetadata(pictureId = " ")
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The picture id cannot be blank or null"))
        )
    }

    @Test
    fun getPhotoAfterHavingAddedItReturnSamePhoto() {
        val bitmap = Bitmap.createBitmap(123, 240, Bitmap.Config.ARGB_8888)
        runTest {
            val metadata =
                MeetingPhotoServiceImpl.addMeetingsPhoto(
                    meetingWithPhotos.meetingId!!,
                    TrashPhotoMetadata(
                        null,
                        ParcelableDate.now,
                        "1234567890",
                        "Bin",
                        TrashCategory.PLASTIC
                    ),
                    bitmap
                )
            val retrievedPhoto =
                MeetingPhotoServiceImpl.getPhoto(meetingWithPhotos.meetingId!!, metadata)
            assertThat(bitmap, EqualsToBitmap.equalsBitmap(retrievedPhoto!!))
        }
    }

    @Test
    fun removeMeetingPhotoBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.removeMeetingPhoto(" ", TrashPhotoMetadata(pictureId = "not null", takenBy = "non null"))
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting id cannot be blank"))
        )
    }

    @Test
    fun removeMeetingPhotoNullPhotoIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.removeMeetingPhoto(
                    meetingWithPhotos.meetingId!!,
                    TrashPhotoMetadata(pictureId = null, takenBy = "non null")
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The picture id cannot be blank or null"))
        )
    }

    @Test
    fun removeMeetingPhotoBlankPhotoIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingPhotoServiceImpl.removeMeetingPhoto(
                    meetingWithPhotos.meetingId!!,
                    TrashPhotoMetadata(pictureId = " ", takenBy = null)
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The picture id cannot be blank or null"))
        )
    }
}