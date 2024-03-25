package com.simondev.gettingthingsdone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class Main extends AppCompatActivity {
    private final TodoLists lists = new TodoLists();
    private final TodoMotivational motivational = new TodoMotivational();

    private ServerConnection serverConnection;

    TodoLists getLists() {
        return lists;
    }

    TodoMotivational getMotivational() {
        return motivational;
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
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragMain, Lists.class, null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                        break;
                    case 1:
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragMain, Motivational.class, null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                        break;
                    case 2:
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

        try {
            serverConnection = new ServerConnection("192.168.1.250", 1922);
        } catch (ConnectionException e) {
            Toast.makeText(this, "Could not connect to server: " + e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            serverConnection.close();
        }
    }
}
