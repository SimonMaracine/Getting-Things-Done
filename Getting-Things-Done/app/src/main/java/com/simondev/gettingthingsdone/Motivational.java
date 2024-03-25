package com.simondev.gettingthingsdone;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Future;

// https://www.keepinspiring.me/motivational-quotes/

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

    @SuppressLint("SetTextI18n")
    private void setMotivational(TextView txtMotivational) {
        txtMotivational.setText("Motivational paragraph...");

        ServerConnection serverConnection = ((Main) requireActivity()).getServerConnection();

        serverConnection.sendMessage(MsgType.ClientGetMotivational, new JSONObject());

        Future<?> future = serverConnection.sendReceiveAsync();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Communication.waitForMessage(serverConnection, handler, future,
            msg -> {
                if (msg.header.msgType == MsgType.ServerOfferMotivational) {
                    String text = null;
                    String author = null;
                    try {
                        text = msg.payload.getString("text");
                        author = msg.payload.getString("author");
                    } catch (JSONException ignored) {}

                    motivational.text = text;
                    motivational.author = author;

                    if (motivational.text != null && motivational.author != null) {
                        txtMotivational.setText(motivational.text + "\n\n" + motivational.author);
                    }
                }
            },
            errMsg -> Toast.makeText(getContext(), "Unexpected error: " + errMsg, Toast.LENGTH_LONG).show()
        ));
    }
}
