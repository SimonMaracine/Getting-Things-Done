package com.example.gettingthingsdone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Motivational extends Fragment {
    private TodoMotivational motivational;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_motivational, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        motivational = ((Main) requireActivity()).getMotivational();

        TextView txtMotivational = view.findViewById(R.id.txtMotivational);

        setMotivational(txtMotivational);
    }

    private void setMotivational(TextView txtMotivational) {
        // TODO check timestamp and fetch paragraph from the server

        txtMotivational.setText(motivational.paragraph);
    }
}
