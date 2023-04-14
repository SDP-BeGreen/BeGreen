package com.github.sdp_begreen.begreen.viewModels

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.mockito.Mockito.*

/**
 * I started by putting this test in the unit test package, but I needed support from android
 * to be able to test bitmap, as they are not implemented for simple unit test
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class ConnectedUserViewModelTest: KoinTest {

    /**
     * Initialize some constant to use in every tests
     */
    companion object {
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)
        private val userPhotoMetadata = PhotoMetadata("user1_profile_picture")
        private val userPhotoMetadata2 = PhotoMetadata("user3_profile_picture")
        private const val userId1 = "1234"
        private const val userId2 = "1235"
        private const val userId3 = "1236"
        private val user1 = User(userId1, 12, "User 1", profilePictureMetadata = userPhotoMetadata)
        private val user2 = User(userId2, 12, "User 2")
        private val user3 = User(userId3, 12, "User 3", profilePictureMetadata = userPhotoMetadata2)
        private val fakePicture1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        private val fakePicture2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        @BeforeClass
        @JvmStatic
        fun setUpAuth() {
            // The implementation need to be provided before the rule is executed,
            // that's why we do it in the beforeClass method
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                `when`(db.getUser(userId1)).thenReturn(user1)
                `when`(db.getUser(userId2)).thenReturn(user2)
                `when`(db.getUser(userId3)).thenReturn(user3)
                `when`(db.getUserProfilePicture(userPhotoMetadata, userId1))
                    .thenReturn(fakePicture1)
                // add a small delay, just to be sure that it is triggered after initialization
                // and arrive second, after the initial null value
                `when`(auth.getConnectedUserIds())
                    .thenReturn(flowOf(userId1).onEach { delay(10) })
            }
        }
    }

    private lateinit var vm: ConnectedUserViewModel

    /**
     * Needed to be able to test coroutine
     * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/
     */
    @get:Rule
    val coroutineRules = CoroutineTestRule()

    /**
     * As I only test the viewModel without actually starting any activity or fragment,
     * I don't need to use the [com.github.sdp_begreen.begreen.rules.KoinTestRule] that launch an
     * actual instrumented test
     */
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single { db }
            single { auth }
        })
    }

    @Before
    fun setUpVM() {
        vm = ConnectedUserViewModel()
    }

    @Test
    fun currentUserContainCorrectSignInUser() {
        runTest {
            assertThat(vm.currentUser.drop(1).first(), `is`(equalTo(user1)))
        }
    }

    @Test
    fun currentUserContainsCorrectUserSetManually() {
        runTest {
            vm.setCurrentUser(user1)

            assertThat(vm.currentUser.first(), `is`(equalTo(user1)))
        }
    }

    @Test
    fun currentUserContainCorrectSignInUserChangingUser() {
        runTest {
            `when`(auth.getConnectedUserIds())
                .thenReturn(flowOf(userId1, userId2, userId3).onEach { delay(10) })

            // need new vm, as we modify the "getConnectedUserIds" value
            val vm = ConnectedUserViewModel()
            val users = vm.currentUser.drop(1).take(3).toList()
            assertThat(users, contains(user1, user2, user3))
        }
    }

    @Test
    fun currentUserCorrectValueAuthAndUpdateUser() {
        runTest {
            `when`(auth.getConnectedUserIds())
                .thenReturn(flowOf(userId1, userId3).onEach { delay(10) })

            val vm = ConnectedUserViewModel()
            // need to explicitly launch it for it to go into the dispatcher
            launch {
                delay(15)
                vm.setCurrentUser(user2)
            }

            val users = vm.currentUser.drop(1).take(3).toList()
            assertThat(users, contains(user1, user2, user3))
        }
    }

    @Test
    fun currentUserProfilePictureCorrectlySetSignedInUser() {
        runTest {
            assertThat(
                vm.currentUserProfilePicture.drop(1).first(),
                `is`(equalTo(fakePicture1)))
        }
    }

    @Test
    fun currentUserProfilePictureCorrectlyResetUponNewAuth() {
        runTest {
            `when`(auth.getConnectedUserIds())
                .thenReturn(flowOf(userId1, userId2).onEach { delay(10) })

            val vm = ConnectedUserViewModel()
            assertThat(
                vm.currentUserProfilePicture.drop(1).take(2).toList(),
                contains(fakePicture1, null)
            )
        }
    }

    @Test
    fun currentUserProfilePictureCorrectlyResetManuallyModifyingUser() {
        runTest {
            launch {
                delay(15)
                vm.setCurrentUser(user2)
            }

            assertThat(
                vm.currentUserProfilePicture.drop(1).take(2).toList(),
                contains(fakePicture1, null)
            )
        }
    }

    @Test
    fun currentUserProfilePictureCorrectlyUpdatedUponNewAuth() {
        runTest {
            `when`(db.getUserProfilePicture(userPhotoMetadata2, userId3))
                .thenReturn(fakePicture2)
            `when`(auth.getConnectedUserIds())
                .thenReturn(flowOf(userId1, userId3).onEach { delay(10) })

            val vm = ConnectedUserViewModel()
            assertThat(
                vm.currentUserProfilePicture.drop(1).take(2).toList(),
                contains(fakePicture1, fakePicture2)
            )
        }
    }


    @Test
    fun currentUserProfilePictureCorrectManuallyModified() {
        runTest {
            launch {
                delay(15)
                vm.setCurrentUserProfilePicture(fakePicture2, userId1)
            }

            assertThat(
                vm.currentUserProfilePicture.drop(1).take(2).toList(),
                contains(fakePicture1, fakePicture2)
            )
        }
    }

    @Test
    fun setCurrentUserProfilePictureThrowsWrongUserId() {
        runTest {
            launch {
                delay(15)
                val exception = assertThrows(IllegalArgumentException::class.java) {
                    vm.setCurrentUserProfilePicture(fakePicture2, "1111")
                }
                assertThat(exception.message, `is`(equalTo("Trying to modify profile picture of another user")))
            }
        }
    }


    @Test
    fun setCurrentUserProfilePictureThrowsNullUserId() {
        runTest {
            launch {
                delay(15)
                val exception = assertThrows(IllegalArgumentException::class.java) {
                    vm.setCurrentUserProfilePicture(fakePicture2, null)
                }
                assertThat(exception.message, `is`(equalTo("Trying to modify profile picture of another user")))
            }
        }
    }

}