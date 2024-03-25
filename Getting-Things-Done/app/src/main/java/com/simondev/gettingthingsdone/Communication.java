package com.simondev.gettingthingsdone;

import android.os.Handler;

import java.util.concurrent.Future;

class Communication {
    static void waitForMessage(ServerConnection serverConnection, Handler handler, Future<?> future, OnMessageArrived onMessageArrived, OnError onError) {
        if (!future.isDone()) {
            handler.postDelayed(() -> waitForMessage(serverConnection, handler, future, onMessageArrived, onError), 4);

            return;
        }

        try {
            ServerConnection.awaitFuture(future);
        } catch (ServerConnectionException e) {
            onError.call(e.getMessage());

            return;
        }

        Message msg = serverConnection.receiveMessage();
        onMessageArrived.call(msg);
    }
}

interface OnMessageArrived {
    void call(Message msg);
}

interface OnError {
    void call(String errMsg);
}
