package com.example.gettingthingsdone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class List extends Fragment {
    private TodoList list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int index = getArguments().getInt("listIndex");

            list = ((Main) requireActivity()).getLists().get(index);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((EditText) view.findViewById(R.id.inpName)).setText(list.name);

        view.findViewById(R.id.btnAddTask).setOnClickListener(this::onAddTaskButtonPressed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        list.name = ((EditText) requireActivity().findViewById(R.id.inpName)).getText().toString();
    }

    public void onAddTaskButtonPressed(View view) {

    }
}
