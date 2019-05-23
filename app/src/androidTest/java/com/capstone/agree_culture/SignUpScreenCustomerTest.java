package com.capstone.agree_culture;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SignUpScreenCustomerTest {

    @Rule
    public ActivityTestRule<SignUpActivity> mSignUpActivityCustomer = new ActivityTestRule<SignUpActivity>(SignUpActivity.class);

    @Test
    public void signUpCustomer() throws Exception{

        onView(withId(R.id.sign_up_full_name))
                .perform(typeText("Test Test Test"));
        onView(withId(R.id.sign_up_email))
                .perform(typeText("tesstt@gmail.com"));
        onView(withId(R.id.sign_up_password))
                .perform(typeText("123123"));
        onView(withId(R.id.sign_up_confirm_password))
                .perform(typeText("123123"));
        onView(withId(R.id.sign_up_address))
                .perform(typeText("South Bus Terminal"));
        onView(withId(R.id.sign_up_city))
                .perform(typeText("Cebu"));
        onView(withId(R.id.sign_up_phone_number))
                .perform(typeText("9290356732"));
//        onView(withId(R.id.sign_up_button))
//                .perform(click());


    }
}
