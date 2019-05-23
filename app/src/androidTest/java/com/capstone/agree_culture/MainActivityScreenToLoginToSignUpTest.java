package com.capstone.agree_culture;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityScreenToLoginToSignUpTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void clickLoginButton_openLoginUi_openSignUpUi() throws Exception{
        onView(withId(R.id.home_login))
                .perform(click());
        onView(withId(R.id.sign_up_link))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.toolbar))
                .check(matches(isDisplayed()));

    }

}
