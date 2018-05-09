package com.alexandre.myquotes

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.alexandre.myquotes.view.login.LoginActivity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class QuoteInstrumentedTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.alexandre.myquotes", appContext.packageName)
    }

    @Test
    fun hintLogin(){
        onView(withId(R.id.ed_login))
                .check(matches(withHint("login or email")))
    }

    @Test
    fun testLogin() {
        onView(withId(R.id.ed_login))
                .perform(clearText(), typeText("HealsyTestUser"))

        onView(withId(R.id.ed_password))
                .perform(clearText(), typeText("HealsyTestUser")).perform(closeSoftKeyboard())

        Thread.sleep(500);

        onView(withId(R.id.submit))
                .perform(click())

        Thread.sleep(4000);

        onView(withId(R.id.txt_user_name)).check(matches(withText("healsytestuser")));
    }
}
