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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

//This test goes through sign in --> postlogin --> home fragment --> posting page --> back press --> profile fragment --> settings --> sign out
//All while ensuring the necessary UI elements are present and representing the correct text
//Important: Ensure that app is currently NOT logged in before running test

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PostingUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void postingUITest() {

        //email and password to login (change accordingly)
        String email = "bossbibu@gmail.com";
        String password = "123456";

        takeFive();

        //Sign In Page
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnSignIn), withText("Sign in"), isDisplayed()));
        appCompatButton.perform(click());

        takeFive();

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.tiletLoginEmail), isDisplayed()));
        textInputEditText.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.tiletLoginPassword), isDisplayed()));
        textInputEditText2.perform(replaceText(password), closeSoftKeyboard());

        takeFive();

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnLoginSignIn), withText("Sign in"), isDisplayed()));
        appCompatButton2.perform(click());

        takeFive();

        //Posting Page
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.btnJio), isDisplayed()));
        floatingActionButton.perform(click());

        onView(withId(R.id.tbTitle)).check(matches(withText("Post an Activity")));

        onView(withId(R.id.btnConfirmPost)).check(matches(isDisplayed()));

        onView(withId(R.id.tvPostFirstHeader)).check(matches(withText("Display Information")));

        onView(withId(R.id.tilDisplayTitle)).check(matches(isDisplayed()));

        onView(withId(R.id.ivPostDisplayImage)).check(matches(isDisplayed()));

        onView(withId(R.id.btnRemoveImage)).check(matches(isDisplayed()));

        onView(withId(R.id.tvPostSecondHeader)).check(matches(withText("Activity Information")));

        onView(withId(R.id.tvTypeActivity)).check(matches(withText("Type of Activity:")));

        onView(withId(R.id.spTypeActivity)).check(matches(isDisplayed()));

        onView(withId(R.id.tvLocation)).check(matches(withText("Location:")));

        onView(withId(R.id.etLocation)).check(matches(isDisplayed()));

        onView(withId(R.id.post_activity_open_maps)).check(matches(isDisplayed()));

        onView(withId(R.id.tvDateTime)).check(matches(withText("Time and Date:")));

        onView(withId(R.id.tvActivityTimeDate)).check(matches(withText("Actual activity:")));

        onView(withId(R.id.tvTime)).check(matches(isDisplayed()));

        onView(withId(R.id.btnSetTime)).check(matches(isDisplayed()));

        onView(withId(R.id.tvDate)).check(matches(isDisplayed()));

        onView(withId(R.id.btnSetDate)).check(matches(isDisplayed()));

        onView(withId(R.id.tvConfirmationDeadline)).check(matches(withText("Confirmation deadline:")));

        onView(withId(R.id.tvDeadlineTime)).check(matches(isDisplayed()));

        onView(withId(R.id.btnSetTimeDeadline)).check(matches(isDisplayed()));

        onView(withId(R.id.tvDeadlineDate)).check(matches(isDisplayed()));

        onView(withId(R.id.btnSetDateDeadline)).check(matches(isDisplayed()));

        onView(withId(R.id.tvNumberParticipants)).check(matches(withText("Number of Participants:")));

        onView(withId(R.id.tvMinParticipants)).check(matches(withText("Minimum:")));

        onView(withId(R.id.etMinParticipants)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.tvMaxParticipants)).perform(scrollTo()).check(matches(withText("Maximum:")));

        onView(withId(R.id.etMaxParticipants)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.tvDetails)).perform(scrollTo()).check(matches(withText("Additional Details:")));

        onView(withId(R.id.etAdditionalDetails)).perform(scrollTo()).check(matches(isDisplayed()));

        takeFive();

        pressBack();

        takeFive();

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("yes")));
        appCompatButton3.perform(scrollTo(), click());

        takeFive();

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.bab_profile), withContentDescription("Profile"),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        takeFive();

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.settings), withContentDescription("Profile"),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        takeFive();

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view)));
        recyclerView.perform(actionOnItemAtPosition(8, click()));

        takeFive();

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("yes")));
        appCompatButton4.perform(scrollTo(), click());

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
