package com.example.gettingthingsdone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        // TODO send a log in request, then wait for validation and result

        startActivity(new Intent(Login.this, Main.class));
        finish();
    }

    public void onSignUpButtonPressed(View view) {
        // TODO send a create account request, then wait for validation and result

        Toast.makeText(getApplicationContext(), "Sign up pressed", Toast.LENGTH_SHORT).show();
    }
}
