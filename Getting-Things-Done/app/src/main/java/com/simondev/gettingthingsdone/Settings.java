package com.simondev.gettingthingsdone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;

public class Settings extends Fragment {
    private LinearLayout lytAllTasks;

    private TodoLists lists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lists = ((Main) requireActivity()).getLists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnLogOut).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Login.class));
            requireActivity().finish();
        });

        lytAllTasks = view.findViewById(R.id.lytAllTasks);

        createPresentTasks();
    }

    private void createTaskViews(int index, String name) {
        if (lytAllTasks.getChildAt(0).getId() == R.id.txtEmptyA) {
            lytAllTasks.removeViewAt(0);
        }

        if (lytAllTasks.getChildCount() == 0) {
            lytAllTasks.addView(createSpacingView());
        }

        lytAllTasks.addView(createTaskView(index, name));
        lytAllTasks.addView(createSpacingView());
    }

    @SuppressLint("SetTextI18n")
    private View createTaskView(int index, String name) {
        LinearLayout lytTask = new LinearLayout(this.getContext());
        lytTask.setOrientation(LinearLayout.HORIZONTAL);
        lytTask.setGravity(Gravity.END);

        int verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        TextView txtTask = new TextView(this.getContext());
        txtTask.setText(name);
        txtTask.setTextAppearance(com.google.android.material.R.style.TextAppearance_AppCompat_Medium);
        txtTask.setPadding(0, verticalPadding, 0, verticalPadding);
        txtTask.setLayoutParams(new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            0.75f
        ));

        lytTask.addView(txtTask);

        Button btnTask = new Button(this.getContext());
        btnTask.setText("Delete");
        btnTask.setLayoutParams(new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            0.25f
        ));
        btnTask.setOnClickListener(v -> {

        });

        lytTask.addView(btnTask);

        return lytTask;
    }

    private View createSpacingView() {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());

        View spacing = new View(getContext());
        spacing.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

        return spacing;
    }

    private View createSeparatorView() {
        View lineSeparator = new View(getContext());
        lineSeparator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2));
        lineSeparator.setBackgroundColor(Color.parseColor("#CCCCCC"));

        return lineSeparator;
    }

    private void createPresentTasks() {
        Iterator<TodoList> iter = lists.iterator();

        while (iter.hasNext()) {
            TodoList list = iter.next();

            for (TodoTask task : list) {
                String content = task.content;

                if (content.length() > 12) {
                    content = content.substring(0, 12) + "...";
                }

                createTaskViews(task.index, content);
            }

            if (iter.hasNext()) {
                lytAllTasks.addView(createSeparatorView());
                lytAllTasks.addView(createSpacingView());
            }
        }
    }
}
