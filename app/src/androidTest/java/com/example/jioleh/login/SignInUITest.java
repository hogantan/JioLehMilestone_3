package com.example.jioleh.login;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.jioleh.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

//To test whether all UI elements are present and are presenting the correct inputs

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignInUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void signInUITest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnSignIn), withText("Sign in"),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));


        onView(withId(R.id.tvLoginSubHeader)).check(matches(withText("Sign In")));

        onView(withId(R.id.tiletLoginEmail)).check(matches(withHint("Email address")));

        onView(withId(R.id.tiletLoginPassword)).check(matches(withHint("Password")));

        onView(withId(R.id.btnLoginSignIn)).check(matches(withText("Sign in")));

        onView(withId(R.id.tvForgotPasswordLogin)).check(matches(withText("Forgot password?")));

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.tvForgotPasswordLogin), withText("Forgot password?"),
                        isDisplayed()));
        appCompatTextView.perform(click());

        onView(withId(R.id.tvForgotPassword)).check(matches(withText("Forgot Password")));

        onView(withId(R.id.tvForgotPasswordMsg)).check(matches(withText("Please enter your registered email address.")));

        onView(withId(R.id.tiletForgotPasswordEmail)).check(matches(withHint("Email address")));

        onView(withId(R.id.btnResetPassword)).check(matches(withText("Reset password")));

        ViewInteraction imageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));

        ViewInteraction imageButton3 = onView(
                allOf(withContentDescription("Navigate up"),
                        isDisplayed()));
        imageButton3.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        isDisplayed()));
        appCompatImageButton2.perform(click());
    }
}
