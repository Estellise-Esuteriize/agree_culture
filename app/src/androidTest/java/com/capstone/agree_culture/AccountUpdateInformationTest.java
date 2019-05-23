package com.capstone.agree_culture;


import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AccountUpdateInformationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void distributorUpdateInformationTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try{

            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ViewInteraction actionMenuItemView = onView(
                    allOf(withId(R.id.home_login), withContentDescription("Login"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(R.id.toolbar),
                                            1),
                                    0),
                            isDisplayed()));
            actionMenuItemView.perform(click());

            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ViewInteraction appCompatAutoCompleteTextView = onView(
                    allOf(withId(R.id.email),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.design.widget.TextInputLayout")),
                                            0),
                                    0)));
            appCompatAutoCompleteTextView.perform(scrollTo(), replaceText("distributor@gmail.com"), closeSoftKeyboard());

            ViewInteraction appCompatEditText = onView(
                    allOf(withId(R.id.password),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.design.widget.TextInputLayout")),
                                            0),
                                    0)));
            appCompatEditText.perform(scrollTo(), replaceText("123123"), closeSoftKeyboard());

            ViewInteraction appCompatButton = onView(
                    allOf(withId(R.id.sign_in_button), withText("Sign in"),
                            childAtPosition(
                                    allOf(withId(R.id.email_login_form),
                                            childAtPosition(
                                                    withId(R.id.login_form),
                                                    0)),
                                    2)));
            appCompatButton.perform(scrollTo(), click());

        }
        catch (NoMatchingViewException ex){
            ex.printStackTrace();
        }

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        ViewInteraction appCompatImageButton = onView(
//                allOf(withContentDescription("Open navigation drawer"),
//                        childAtPosition(
//                                allOf(withId(R.id.toolbar),
//                                        childAtPosition(
//                                                withClassName(is("android.support.design.widget.AppBarLayout")),
//                                                0)),
//                                0),
//                        isDisplayed()));
//        appCompatImageButton.perform(click());


        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_account_edit));

//        ViewInteraction navigationMenuItemView = onView(withId(R.id.nav_account_edit));
//        navigationMenuItemView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.sign_up_full_name),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        textInputEditText.perform(scrollTo(), click());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.sign_up_full_name),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        textInputEditText2.perform(scrollTo(), replaceText("DistributorKo"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.sign_up_button), withText("Update Information"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                8)));
        appCompatButton2.perform(scrollTo(), click());

        try{
            Thread.sleep(5000);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
