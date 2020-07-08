package com.example.jioleh;

import androidx.test.espresso.ViewInteraction;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.jioleh.R;
import com.example.jioleh.login.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

//This test goes through sign in --> postlogin --> home fragment --> profile fragment --> settings --> change password --> back press --> sign out
//All while ensuring the necessary UI elements are present and representing the correct text
//Important: Ensure that app is currently NOT logged in before running test

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChangePasswordUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void ChangePasswordUITest() {

        takeFive();

        //email and password to login (change accordingly)
        String email = "bossbibu@gmail.com";
        String password = "123456";

        //Sign In Page
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnSignIn), withText("Sign in"),
                        isDisplayed()));
        appCompatButton.perform(click());

        takeFive();

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.tiletLoginEmail),
                        isDisplayed()));
        textInputEditText.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.tiletLoginPassword),
                        isDisplayed()));
        textInputEditText2.perform(replaceText(password), closeSoftKeyboard());

        takeFive();

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnLoginSignIn), withText("Sign in"),
                        isDisplayed()));
        appCompatButton2.perform(click());

        takeFive();

        //Profile Fragment
        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.bab_profile), withContentDescription("Profile"),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        takeFive();

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.settings), withContentDescription("Profile"),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        takeFive();

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view)));
        recyclerView.perform(actionOnItemAtPosition(6, click()));

        takeFive();

        onView(withId(R.id.tbTempTopBar)).check(matches(isDisplayed()));

        onView(withId(R.id.tvChangePassword)).check(matches(withText("Change Password")));

        onView(withId(R.id.tiletChangeOldPassword)).check(matches(withHint("Old Password")));

        onView(withId(R.id.tiletChangeNewPassword)).check(matches(withHint("New Password")));

        onView(withId(R.id.tiletChangeConfirmNewPassword)).check(matches(withHint("Confirm New Password")));

        onView(withId(R.id.btnConfirmChangePassword)).check(matches(withText("Confirm change")));

        takeFive();

        pressBack();

        takeFive();

        ViewInteraction recyclerView1 = onView(
                allOf(withId(R.id.recycler_view)));
        recyclerView1.perform(actionOnItemAtPosition(8, click()));

        takeFive();

        ViewInteraction appCompatButton1 = onView(
                allOf(withId(android.R.id.button1), withText("yes")));
        appCompatButton1.perform(scrollTo(), click());

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
