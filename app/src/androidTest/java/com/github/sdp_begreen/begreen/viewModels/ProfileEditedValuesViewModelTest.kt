package com.github.sdp_begreen.begreen.viewModels

import android.graphics.Bitmap
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@SmallTest
class ProfileEditedValuesViewModelTest {

    private lateinit var vm: ProfileEditedValuesViewModel

    @Before
    fun setUpVM() {
        vm = ProfileEditedValuesViewModel()
    }

    @Test
    fun isCurrentlyEditingInitialyFalse() {
        assertFalse(vm.isCurrentlyEditing())
    }

    @Test
    fun startEditingCorrectlyReflectedInIsCurrentlyEditing() {
        vm.startEditing()
        assertTrue(vm.isCurrentlyEditing())
    }

    @Test
    fun finishEditingCorrectlyReflectedInIsCurrentlyEditing() {
        vm.finishEditing()
        assertFalse(vm.isCurrentlyEditing())
    }

    @Test
    fun nullValueSetWhenSettingDisplayNameInNonEditingMode() {
        vm.displayName = "New display name"

        assertThat(vm.displayName, `is`(nullValue()))
    }

    @Test
    fun nullValueSetWhenSettingEmailInNonEditingMode() {
        vm.email = "New email"

        assertThat(vm.email, `is`(nullValue()))
    }

    @Test
    fun nullValueSetWhenSettingPhoneInNonEditingMode() {
        vm.phone = "New phone number"

        assertThat(vm.phone, `is`(nullValue()))
    }

    @Test
    fun nullValueSetWhenSettingDescriptionInNonEditingMode() {
        vm.description = "New description"

        assertThat(vm.description, `is`(nullValue()))
    }

    @Test
    fun nullValueSetWhenSettingProfilePictureNonEditingMode() {
        vm.profilePicture = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        assertThat(vm.profilePicture, `is`(nullValue()))
    }

    @Test
    fun displayNameCorrectlySetWhenInEditingMode() {
        vm.startEditing()
        val name = "New display name"
        vm.displayName = name

        assertThat(vm.displayName, `is`(name))
    }

    @Test
    fun emailCorrectlySetWhenInEditingMode() {
        vm.startEditing()
        val email = "New email"
        vm.email = email

        assertThat(vm.email, `is`(email))
    }

    @Test
    fun phoneCorrectlySetWhenInEditingMode() {
        vm.startEditing()
        val phone = "12345"
        vm.phone = phone

        assertThat(vm.phone, `is`(phone))
    }

    @Test
    fun descriptionCorrectlySetWhenInEditingMode() {
        vm.startEditing()
        val description = "New description"
        vm.description = description

        assertThat(vm.description, `is`(description))
    }

    @Test
    fun profilePictureCorrectlySetWhenInEditingMode() {
        vm.startEditing()
        val picture = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        vm.profilePicture = picture

        assertThat(vm.profilePicture, `is`(picture))
    }

    @Test
    fun finishEditingCorrectlyResetAllValueToNull() {
        vm.startEditing()
        val name = "New display name"
        vm.displayName = name
        val email = "New email"
        vm.email = email
        val phone = "12345"
        vm.phone = phone
        val description = "New description"
        vm.description = description
        val picture = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        vm.profilePicture = picture

        // assert values non null
        assertThat(vm.displayName, `is`(name))
        assertThat(vm.email, `is`(email))
        assertThat(vm.phone, `is`(phone))
        assertThat(vm.description, `is`(description))
        assertThat(vm.profilePicture, `is`(picture))

        // finish editing
        vm.finishEditing()

        // assert values null
        assertThat(vm.displayName, `is`(nullValue()))
        assertThat(vm.email, `is`(nullValue()))
        assertThat(vm.phone, `is`(nullValue()))
        assertThat(vm.description, `is`(nullValue()))
        assertThat(vm.profilePicture, `is`(nullValue()))
    }
}