package com.simondev.gettingthingsdone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btnLogin).setOnClickListener(this::onLoginButtonPressed);
        findViewById(R.id.btnSignUp).setOnClickListener(this::onSignUpButtonPressed);
    }

    private void onLoginButtonPressed(View view) {
        // TODO send a log in request, then wait for validation and result

        startActivity(new Intent(Login.this, Main.class));
        finish();
    }

    private void onSignUpButtonPressed(View view) {
        // TODO send a create account request, then wait for validation and result

        Toast.makeText(getApplicationContext(), "Sign up pressed", Toast.LENGTH_SHORT).show();
    }
}
