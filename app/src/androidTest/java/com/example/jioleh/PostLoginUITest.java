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

//This test goes through sign in --> postlogin --> home fragment --> favourite fragment --> chat fragment --> profile fragment --> settings --> sign out
//All while ensuring the necessary UI elements are present and representing the correct text
//Important: Ensure that app is currently NOT logged in before running test

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PostLoginUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void postLoginUITest() {

        takeFive();

        //email and password to login (change accordingly)
        String email = "bossbibu@gmail.com";
        String password = "123456";

        //Sign In Page
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnSignIn), withText("Sign in"),
                        isDisplayed()));
        appCompatButton.perform(click());

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

        //Home Fragment
        onView(withId(R.id.tbTitle)).check(matches(withText("JioLeh")));

        onView(withId(R.id.ivSearchActivity)).check(matches(isDisplayed()));

        onView(withId(R.id.ivFindNearby)).check(matches(isDisplayed()));

        onView(withId(R.id.tvLatestActivities)).check(matches(withText("Latest Activities")));

        takeFive();

        ViewInteraction bottomNavigationItemView1 = onView(
                allOf(withId(R.id.bab_favourite), withContentDescription("Favourite"),
                        isDisplayed()));
        bottomNavigationItemView1.perform(click());

        takeFive();

        //Favourite Fragment
        onView(withId(R.id.tbTitle)).check(matches(withText("Favourites")));

        ViewInteraction textView5 = onView(
                allOf(withText("Join"),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withText("Like"),
                        isDisplayed()));
        textView6.check(matches(isDisplayed()));

        ViewInteraction tabView = onView(
                allOf(withContentDescription("Like"),
                        isDisplayed()));
        tabView.perform(click());

        takeFive();

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.bab_chat), withContentDescription("Messages"),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        takeFive();

        //Chat Fragment
        onView(withId(R.id.tbTitle)).check(matches(withText("Messages")));

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.bab_profile), withContentDescription("Profile"),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        takeFive();

        //Profile Fragment
        onView(withId(R.id.tbTitle)).check(matches(withText("My Profile")));

        onView(withId(R.id.settings)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_profilePageUsername)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_profilePageAge)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_profilePageGender)).check(matches(isDisplayed()));

        onView(withId(R.id.iv_userProfilePageImage)).check(matches(isDisplayed()));


        ViewInteraction textView15 = onView(
                allOf(withText("Listings"),
                        isDisplayed()));
        textView15.check(matches(isDisplayed()));

        ViewInteraction textView16 = onView(
                allOf(withText("Reviews"),
                        isDisplayed()));
        textView16.check(matches(isDisplayed()));

        textView16.perform(click());

        takeFive();

        ViewInteraction textView17 = onView(
                allOf(withText("About Me"),
                        isDisplayed()));
        textView17.check(matches(isDisplayed()));

        textView17.perform(click());

        takeFive();

        onView(withId(R.id.ivAboutMeLocation)).check(matches(isDisplayed()));

        onView(withId(R.id.ivAboutMeContact)).check(matches(isDisplayed()));

        onView(withId(R.id.tvAboutMeLocation)).check(matches(isDisplayed()));

        onView(withId(R.id.tvAboutMeContact)).check(matches(isDisplayed()));

        onView(withId(R.id.bio)).check(matches(withText("Bio")));

        onView(withId(R.id.Interests)).check(matches(withText("Interests")));

        onView(withId(R.id.bio_fill)).check(matches(isDisplayed()));

        onView(withId(R.id.Interest_box)).check(matches(isDisplayed()));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.settings), withContentDescription("Profile"),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        takeFive();

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view)));
        recyclerView.perform(actionOnItemAtPosition(8, click()));

        takeFive();

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("yes")));
        appCompatButton3.perform(scrollTo(), click());

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
