package com.simondev.gettingthingsdone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Future;

public class Login extends AppCompatActivity {
    private EditText inpEmail;
    private EditText inpPassword;

    private ServerConnection serverConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inpEmail = findViewById(R.id.inpEmail);
        inpPassword = findViewById(R.id.inpPassword);

        findViewById(R.id.btnLogIn).setOnClickListener(this::onLogInButtonPressed);
        findViewById(R.id.btnSignUp).setOnClickListener(this::onSignUpButtonPressed);

        try {
            serverConnection = new ServerConnection("192.168.1.250", 1922);
        } catch (ServerConnectionException e) {
            Toast.makeText(this, "Could not connect to server: " + e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            if (serverConnection != null) {
                serverConnection.close();
            }
        }
    }

    private void onLogInButtonPressed(View view) {
        String email = inpEmail.getText().toString();
        String password = inpPassword.getText().toString();

        if (!checkEmailAndPasswordInput(email, password)) {
            return;
        }

        if (serverConnection == null) {
            Toast.makeText(getApplicationContext(), "Not connected to the server", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("email", email);
            obj.put("password", password);
        } catch (JSONException ignored) {}

        serverConnection.sendMessage(MsgType.ClientLogIn, obj);

        Future<?> future = serverConnection.sendReceiveAsync();

        Handler handler = new Handler(getMainLooper());
        handler.post(() -> Communication.waitForMessage(serverConnection, handler, future,
            msg -> {
                switch (msg.header.msgType) {
                    case MsgType.ServerLogInOk -> {
                        startActivity(new Intent(Login.this, Main.class));
                        finish();
                    }
                    case MsgType.ServerLogInFail -> {
                        String message = "";
                        try {
                            message = msg.payload.getString("msg");
                        } catch (JSONException ignored) {}

                        Toast.makeText(this, "Failed to log in: " + message, Toast.LENGTH_LONG).show();
                    }
                    default -> {
                        assert false;
                    }
                }
            },
            errMsg -> Toast.makeText(this, "Unexpected error: " + errMsg, Toast.LENGTH_LONG).show()
        ));
    }

    private void onSignUpButtonPressed(View view) {
        String email = inpEmail.getText().toString();
        String password = inpPassword.getText().toString();

        if (!checkEmailAndPasswordInput(email, password)) {
            return;
        }

        if (serverConnection == null) {
            Toast.makeText(getApplicationContext(), "Not connected to the server", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("email", email);
            obj.put("password", password);
        } catch (JSONException ignored) {}

        serverConnection.sendMessage(MsgType.ClientSignUp, obj);

        Future<?> future = serverConnection.sendReceiveAsync();

        Handler handler = new Handler(getMainLooper());
        handler.post(() -> Communication.waitForMessage(serverConnection, handler, future,
            msg -> {
                switch (msg.header.msgType) {
                    case MsgType.ServerSignUpOk -> {
                        Toast.makeText(this, "Successfully signed up", Toast.LENGTH_LONG).show();
                    }
                    case MsgType.ServerSignUpFail -> {
                        String message = "";
                        try {
                            message = msg.payload.getString("msg");
                        } catch (JSONException ignored) {}

                        Toast.makeText(this, "Failed to sign up: " + message, Toast.LENGTH_LONG).show();
                    }
                    default -> {
                        assert false;
                    }
                }
            },
            errMsg -> Toast.makeText(this, "Unexpected error: " + errMsg, Toast.LENGTH_LONG).show()
        ));
    }

    private boolean checkEmailAndPasswordInput(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email field is empty", Toast.LENGTH_LONG).show();
            return false;
        }

        // TODO check email with regex

        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password field is empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (password.length() < 15) {
            Toast.makeText(getApplicationContext(), "Password is too short", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
