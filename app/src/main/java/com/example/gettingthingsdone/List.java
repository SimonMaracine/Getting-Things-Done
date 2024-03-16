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

        EditText inpName = view.findViewById(R.id.inpName);
        inpName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                list.name = ((EditText) v).getText().toString();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void on(View view) {

    }
}
