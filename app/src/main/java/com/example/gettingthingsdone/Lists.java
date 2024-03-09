package com.example.gettingthingsdone;

import android.annotation.SuppressLint;
import android.graphics.Color;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

// https://www.flaticon.com/free-icons/tick

public class Lists extends Fragment {
    private LinearLayout lytLists;

    private HashMap<Integer, Object> lists;

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
    }

    public void onCreateListButtonPressed(View view) {
        // Remove the placeholder text, if it's there
        if (lytLists.getChildAt(0).getId() == R.id.txtEmpy) {
            lytLists.removeViewAt(0);
        }

        // Add space, if it's the first one
        if (lytLists.getChildCount() == 0) {
            lytLists.addView(createSpacing());
        }

        // Add the new list
        lytLists.addView(createSeparator());
        lytLists.addView(createNewList());
        lytLists.addView(createSeparator());
        lytLists.addView(createSpacing());

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
    private View createNewList() {
        int verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        int horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());

        TextView txtList = new TextView(getContext());
        txtList.setText("<New List>");
        txtList.setTextAppearance(com.google.android.material.R.style.TextAppearance_AppCompat_Medium);
        txtList.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        txtList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        txtList.setBackgroundColor(Color.parseColor("#FAF5FF"));

        // TODO maybe remove
//        txtList.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN: {
//                    TextView view = (TextView) v;
//                    view.getBackground().setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_200), PorterDuff.Mode.SRC_ATOP);
//                    view.invalidate();
//                    break;
//                }
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL: {
//                    TextView view = (TextView) v;
//                    view.getBackground().clearColorFilter();
//                    view.invalidate();
//                    break;
//                }
//            }
//
//            return false;
//        });

        txtList.setOnClickListener(v -> {

        });

        return txtList;
    }

    private View createSeparator() {
        View lineSeparator = new View(getContext());
        lineSeparator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2));
        lineSeparator.setBackgroundColor(Color.parseColor("#CCCCCC"));

        return lineSeparator;
    }

    private View createSpacing() {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());

        View spacing = new View(getContext());
        spacing.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));

        return spacing;
    }
}
