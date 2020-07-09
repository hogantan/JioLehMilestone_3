package com.example.jioleh;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.jioleh.login.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

//This test goes through sign up --> back press
//To test whether all UI elements are present and are presenting the correct inputs
//Important: Ensure that app is currently NOT logged in before running test

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignUpUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void signUpUITest() {

        takeFive();

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnNeedAAccount), withText("Sign up"),
                        isDisplayed()));
        appCompatButton.perform(click());

        takeFive();

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        onView(withId(R.id.tvRegisterNewAccount)).check(matches(withText("Create a New Account")));

        onView(withId(R.id.tiletRegisterEmail)).check(matches(withHint("Email address")));

        onView(withId(R.id.tiletRegisterPassword)).check(matches(withHint("Password")));

        onView(withId(R.id.btnRegister)).check(matches(withText("Create account")));

        ViewInteraction imageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        takeFive();
    }

    //Take a break for UI to load
    private void takeFive() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
