package com.simondev.gettingthingsdone;

import android.app.Application;

public class GettingThingsDone extends Application {
    private ServerConnection serverConnection;

    ServerConnection createServerConnection() throws ServerConnectionException {
        serverConnection = new ServerConnection("192.168.1.250", 1922);

        return serverConnection;
    }

    ServerConnection getServerConnection() {
        return serverConnection;
    }
}
