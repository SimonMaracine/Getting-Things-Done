package com.example.gettingthingsdone;

import android.annotation.SuppressLint;
import android.graphics.Color;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

// https://www.flaticon.com/free-icons/tick

public class Lists extends Fragment {
    private LinearLayout lytLists;

    private TodoLists lists;

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

        lytLists = view.findViewById(R.id.lytLists);

        Button btnCreateList = view.findViewById(R.id.btnCreateList);
        btnCreateList.setOnClickListener(this::onCreateListButtonPressed);

        lists = ((Main) requireActivity()).getLists();

        createTaskLists();
    }

    public void onCreateListButtonPressed(View view) {
        createListViews(lists.add("<New List " + lists.getCounter() + ">"));
    }

    private void createListViews(int index) {
        // Remove the placeholder text, if it's there
        if (lytLists.getChildAt(0).getId() == R.id.txtEmpy) {
            lytLists.removeViewAt(0);
        }

        // Add space, if it's the first one
        if (lytLists.getChildCount() == 0) {
            lytLists.addView(createSpacingView());
        }

        // Add the new list
        lytLists.addView(createSeparatorView());
        lytLists.addView(createListView(index, "<New List " + index + ">"));
        lytLists.addView(createSeparatorView());
        lytLists.addView(createSpacingView());

//        LinearLayout lytList = new LinearLayout(this.getContext());
//        lytList.setOrientation(LinearLayout.HORIZONTAL);
//
//        TextView txtList = new TextView(this.getContext());
//        txtList.setText("<New List>");
//
//        lytList.addView(txtList);
//
//        ImageButton btnList = new ImageButton(this.getContext());
//        btnList.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.checked, requireContext().getTheme()));
//        btnList.setOnClickListener(null);

//        lytLists.addView(lytList);
    }

    @SuppressLint("SetTextI18n")
    private View createListView(int id, String name) {
        LinearLayout lytList = new LinearLayout(getContext());
        lytList.setOrientation(LinearLayout.HORIZONTAL);
        lytList.setGravity(Gravity.END);

        int verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        int horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        TextView txtList = new TextView(getContext());
        txtList.setText(name);
        txtList.setTextAppearance(com.google.android.material.R.style.TextAppearance_AppCompat_Medium);
        txtList.setPadding(horizontalPadding, verticalPadding, 0, verticalPadding);
        txtList.setLayoutParams(new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        ));

        lytList.addView(txtList);

        Button btnList = new Button(getContext());
        btnList.setText("Open");
        btnList.setLayoutParams(new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            0.25f
        ));
        btnList.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("listIndex", id);

            requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragMain, List.class, args)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
        });

        lytList.addView(btnList);

        return lytList;
    }

    private View createSeparatorView() {
        View lineSeparator = new View(getContext());
        lineSeparator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2));
        lineSeparator.setBackgroundColor(Color.parseColor("#CCCCCC"));

        return lineSeparator;
    }

    private View createSpacingView() {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());

        View spacing = new View(getContext());
        spacing.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));

        return spacing;
    }

    private void createTaskLists() {
        for (int i = 0; i < lists.getCounter(); i++) {
            createListViews(i);
        }
    }
}
