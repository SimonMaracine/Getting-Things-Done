package com.simondev.gettingthingsdone;

import android.annotation.SuppressLint;

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

        view.findViewById(R.id.btnCreateList).setOnClickListener(this::onCreateListButtonPressed);

        lists = ((Main) requireActivity()).getLists();

        createPresentLists();
    }

    public void onCreateListButtonPressed(View view) {
        String name = "<List " + lists.getCounter() + ">";
        createListViews(lists.add(name), name);
    }

    private void createListViews(int index, String name) {
        if (lytLists.getChildAt(0).getId() == R.id.txtEmpy) {
            lytLists.removeViewAt(0);
        }

        if (lytLists.getChildCount() == 0) {
            lytLists.addView(createSpacingView());
        }

        lytLists.addView(createListView(index, name));
        lytLists.addView(createSpacingView());
    }

    @SuppressLint("SetTextI18n")
    private View createListView(int index, String name) {
        LinearLayout lytList = new LinearLayout(getContext());
        lytList.setOrientation(LinearLayout.HORIZONTAL);
        lytList.setGravity(Gravity.END);

        int verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        TextView txtList = new TextView(getContext());
        txtList.setText(name);
        txtList.setTextAppearance(com.google.android.material.R.style.TextAppearance_AppCompat_Medium);
        txtList.setPadding(horizontalPadding, verticalPadding, 0, verticalPadding);
        txtList.setLayoutParams(new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            0.75f
        ));

        lytList.addView(txtList);

        Button btnList = new Button(getContext());
        btnList.setText("View");
        btnList.setLayoutParams(new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            0.25f
        ));
        btnList.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("listIndex", index);

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

    private View createSpacingView() {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());

        View spacing = new View(getContext());
        spacing.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

        return spacing;
    }

    private void createPresentLists() {
        for (TodoList list : lists) {
            createListViews(list.index, list.getName());
        }
    }
}
