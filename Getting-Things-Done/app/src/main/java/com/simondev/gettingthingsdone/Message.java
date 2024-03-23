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

    static final short ServerPing = 10;

    private static final short ServerFirst = ServerPing;
    private static final short ServerLast = ServerPing;

    static boolean inRangeServer(short msgType) {
        return msgType >= ServerFirst && msgType <= ServerLast;
    }
}
