package com.example.gettingthingsdone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class Main extends AppCompatActivity {
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
                        Toast.makeText(getApplicationContext(), "Lists", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Motivational", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
