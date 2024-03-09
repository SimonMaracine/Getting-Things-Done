package com.example.gettingthingsdone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Lists extends Fragment {
    private ScrollView viewLists;  // TODO
    private LinearLayout lytLists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewLists = view.findViewById(R.id.viewLists);
        lytLists = view.findViewById(R.id.lytLists);

        Button btnCreateList = view.findViewById(R.id.btnCreateList);
        btnCreateList.setOnClickListener(this::onCreateListButtonPressed);
    }

    public void onCreateListButtonPressed(View view) {
        // Remove the placeholder text, if it's there
        View first = lytLists.getChildAt(0);

        if (first instanceof TextView && first.getId() == R.id.txtEmpy) {
            lytLists.removeViewAt(0);
        }

        // Add the new list
        Button btnFodder = new Button(this.getContext());
        btnFodder.setText("Fodder");

        lytLists.addView(btnFodder);
    }
}
