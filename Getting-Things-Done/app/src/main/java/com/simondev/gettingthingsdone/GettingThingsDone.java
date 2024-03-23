package com.simondev.gettingthingsdone;

import android.app.Application;

class GettingThingsDone extends Application {
    private ServerConnection serverConnection;

    ServerConnection createServerConnection() {
        try {
            serverConnection = new ServerConnection("192.168.1.250", 1922);
        } catch (RuntimeException e) {
            throw e;
        }

        return serverConnection;
    }

    ServerConnection getServerConnection() {
        return serverConnection;
    }
}
