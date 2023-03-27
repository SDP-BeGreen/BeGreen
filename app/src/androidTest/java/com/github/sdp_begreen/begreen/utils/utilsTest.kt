package com.github.sdp_begreen.begreen.utils

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class utilsTest {
    //This methode check textview is empty or not
    fun checkTextViewIsEmpty(value: String){
        if(value.equals("")){
            assertThat("textview is empty",false)
        }
        else{
            assertThat("textview is not empty",true)
        }
    }

    //This Methode check string length more then 25 then its give error
    fun checkStringLength(value: String){
        val maxLength = 25
        if(value.length > maxLength){
            assertThat("Not allow more then $maxLength sentence length. Right now your sentence length "+value.length , false)
        }
        else{
            assertThat("this test case pass" , true)
        }
    }

    //this methode use for get text from textview
    fun getText(matcher: org.hamcrest.Matcher<View?>?): String? {
        val stringHolder = arrayOf<String?>(null)
        onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): org.hamcrest.Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController?, view: View) {
                val tv = view as TextView //Save, because of check in getConstraints()
                stringHolder[0] = tv.text.toString()
            }
        })
        return stringHolder[0]
    }


}
