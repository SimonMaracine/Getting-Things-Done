package com.simondev.gettingthingsdone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

// https://www.flaticon.com/free-icons/tick
// https://www.flaticon.com/free-icons/cross

public class List extends Fragment {
    private LinearLayout lytTasks;
    private final ArrayList<EditText> inpTasks = new ArrayList<>();

    private TodoList list;

    private static final int DRAWABLE_CANCEL = 1;
    private static final int DRAWABLE_CHECKED = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int index = getArguments().getInt("listIndex");

            list = ((Main) requireActivity()).getLists().get(index);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lytTasks = view.findViewById(R.id.lytTasks);

        ((EditText) view.findViewById(R.id.inpName)).setText(list.getName());

        view.findViewById(R.id.btnAddTask).setOnClickListener(this::onAddTaskButtonPressed);

        createPresentTasks();
    }

    @Override
    public void onStop() {
        super.onStop();

        list.setName(((EditText) requireActivity().findViewById(R.id.inpName)).getText().toString());

        for (int i = 0; i < list.getCount(); i++) {
            list.getTask(i).content = inpTasks.get(i).getText().toString();
        }
    }

    public void onAddTaskButtonPressed(View view) {
        String name = "<Task " + list.getCount() + ">";
        createTaskViews(list.addTask(name), name, false);
    }

    private void createTaskViews(int index, String name, boolean done) {
        // Remove the placeholder text, if it's there
        if (lytTasks.getChildAt(0).getId() == R.id.txtEmptyT) {
            lytTasks.removeViewAt(0);
        }

        // Add space, if it's the first one
        if (lytTasks.getChildCount() == 0) {
            lytTasks.addView(createSpacingView());
        }

        // Add the new list
        lytTasks.addView(createTaskView(index, name, done));
        lytTasks.addView(createSpacingView());
    }

    private View createTaskView(int index, String name, boolean done) {
        LinearLayout lytTask = new LinearLayout(this.getContext());
        lytTask.setOrientation(LinearLayout.HORIZONTAL);
        lytTask.setGravity(Gravity.END);

        int verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        EditText inpTask = new EditText(this.getContext());
        inpTask.setText(name);
        inpTask.setTextAppearance(com.google.android.material.R.style.TextAppearance_AppCompat_Medium);
        inpTask.setPadding(0, verticalPadding, 0, verticalPadding);
        inpTask.setLayoutParams(new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            0.75f
        ));

        lytTask.addView(inpTask);

        inpTasks.add(inpTask);

        ImageButton btnTask = new ImageButton(this.getContext());
        btnTask.setImageDrawable(ResourcesCompat.getDrawable(getResources(), done ? R.drawable.checked : R.drawable.cancel, requireContext().getTheme()));
        btnTask.setTag(done ? DRAWABLE_CHECKED : DRAWABLE_CANCEL);
        btnTask.setLayoutParams(new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            0.25f
        ));
        btnTask.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnTask.setOnClickListener(v -> {
            ImageButton btn = (ImageButton) v;

            switch ((Integer) btn.getTag()) {
                case DRAWABLE_CANCEL:
                    btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.checked, requireContext().getTheme()));
                    btnTask.setTag(DRAWABLE_CHECKED);
                    list.getTask(index).done = true;
                    break;
                case DRAWABLE_CHECKED:
                    btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cancel, requireContext().getTheme()));
                    btnTask.setTag(DRAWABLE_CANCEL);
                    list.getTask(index).done = false;
                    break;
                default:
                    assert false;
            }
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

    private void createPresentTasks() {
        for (int i = 0; i < list.getCount(); i++) {
            createTaskViews(i, list.getTask(i).content, list.getTask(i).done);
        }
    }
}
