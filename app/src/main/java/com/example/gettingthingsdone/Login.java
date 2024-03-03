package com.example.gettingthingsdone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonPressed(View view) {
        Toast.makeText(getApplicationContext(), "Input pressed", Toast.LENGTH_SHORT).show();
    }
}
