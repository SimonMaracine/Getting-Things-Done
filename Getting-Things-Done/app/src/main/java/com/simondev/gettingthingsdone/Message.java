package com.simondev.gettingthingsdone;

import org.json.JSONObject;

class Message {
    Header header;
    JSONObject payload;
}

class Header {
    short msgType;
    short payloadSize;
}

class MsgType {
    static final short ClientPing = 1;
    static final short ClientSignUp = 2;
    static final short ClientLogIn = 3;

    static final short ServerPing = 10;
    static final short ServerSignUpOk = 11;
    static final short ServerSignUpFail = 12;
    static final short ServerLogInOk = 13;
    static final short ServerLogInFail = 14;

    private static final short ServerFirst = ServerPing;
    private static final short ServerLast = ServerLogInFail;

    static boolean inRangeServer(short msgType) {
        return msgType >= ServerFirst && msgType <= ServerLast;
    }
}
