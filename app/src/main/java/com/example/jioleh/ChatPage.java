package com.example.jioleh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class ChatPage extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabs;
    private TabItem chat;
    private TabItem friends;
    private TabItem friend_requests;
    private ViewPager pager;
    private ChatPageFragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        initialise();
        initialiseToolbar();
    }

    private void initialise() {

        tabs = findViewById(R.id.tlChatPage);
        chat = findViewById(R.id.tlChat);
        friends = findViewById(R.id.tlFriends);
        friend_requests = findViewById(R.id.tlRequests);
        pager = findViewById(R.id.vpChatPage);

        fragmentManager = new ChatPageFragmentManager(getSupportFragmentManager(), tabs.getTabCount());
        pager.setAdapter(fragmentManager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbChatPage);
        toolbar.setTitle("JioLeh");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}
