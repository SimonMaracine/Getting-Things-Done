package com.example.gettingthingsdone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class Main extends AppCompatActivity {
    private final TodoLists lists = new TodoLists();

    TodoLists getLists() {
        return lists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabMain = findViewById(R.id.tabMain);
        tabMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragMain, Lists.class, null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                        break;
                    case 1:
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragMain, Motivational.class, null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                        break;
                    case 2:
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragMain, Settings.class, null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
