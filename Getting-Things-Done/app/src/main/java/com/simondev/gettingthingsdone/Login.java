package com.simondev.gettingthingsdone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    private ServerConnection serverConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btnLogin).setOnClickListener(this::onLoginButtonPressed);
        findViewById(R.id.btnSignUp).setOnClickListener(this::onSignUpButtonPressed);

        try {
            serverConnection = ((GettingThingsDone) getApplicationContext()).createServerConnection();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Could not connect to server: " + e, Toast.LENGTH_LONG).show();
        }
    }

    private void onLoginButtonPressed(View view) {
        // TODO send a log in request, then wait for validation and result

        startActivity(new Intent(Login.this, Main.class));
        finish();
    }

    private void onSignUpButtonPressed(View view) {
        // TODO send a create account request, then wait for validation and result

        Toast.makeText(getApplicationContext(), "Sign up pressed", Toast.LENGTH_SHORT).show();

        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "Hello world");
        } catch (JSONException ignored) {}

        serverConnection.sendMessage(MsgType.ClientPing, obj);
    }
}
