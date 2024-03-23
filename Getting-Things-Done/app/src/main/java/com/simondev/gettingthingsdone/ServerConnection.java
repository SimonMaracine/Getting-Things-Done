package com.simondev.gettingthingsdone;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;

class ServerConnection {
    private Socket socket;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ConcurrentLinkedQueue<Message> incomingMessages = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> outgoingMessages = new ConcurrentLinkedQueue<>();

    private static final int HEADER_SIZE = 4;
    private static final int MAX_PAYLOAD_SIZE = 512 - HEADER_SIZE;

    ServerConnection(String host, int port) {
        executor.execute(() -> {
            try {
                socket = new Socket(host, port);
            } catch (IOException e) {
                throw new RuntimeException("Could not connect to " + host + ": " + e);
            }

            InputStreamReader reader;
            OutputStreamWriter writer;

            try {
                reader = new InputStreamReader(socket.getInputStream());
                writer = new OutputStreamWriter(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException("Could not create IO stream: " + e);
            }

            try {
                while (true) {
                    if (receiveNextMessage(reader)) {
                        break;
                    }

                    sendNextMessage(writer);
                }
            } finally {
                try {
                    reader.close();
                    writer.close();
                } catch (IOException ignored) {}
            }
        });
    }

    void sendMessage(short msgType, JSONObject payload) {
        Message msg = new Message();
        msg.header = new Header();
        msg.header.msgType = msgType;
        msg.payload = payload;

        outgoingMessages.add(msg);
    }

    Message receiveMessage() {
        return incomingMessages.poll();
    }

    void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    private boolean receiveNextMessage(InputStreamReader reader) {
        char[] buffer = new char[HEADER_SIZE];
        int result;

        try {
            if (!reader.ready()) {
                return false;
            }

            result = reader.read(buffer, 0, HEADER_SIZE);
        } catch (IOException e) {
            return false;
        }

        if (result < 0) {
            return true;
        }

        Header header;

        try {
            header = parseHeader(buffer, result);
        } catch (RuntimeException e) {
            return false;
        }

        buffer = new char[header.payloadSize];

        try {
            if (!reader.ready()) {
                return false;
            }

            result = reader.read(buffer, 0, header.payloadSize);
        } catch (IOException e) {
            return false;
        }

        if (result < 0) {
            return true;
        }

        JSONObject payload;

        try {
            payload = parsePayload(buffer, result, header);
        } catch (RuntimeException e) {
            return false;
        }

        Message msg = new Message();
        msg.header = header;
        msg.payload = payload;

        incomingMessages.add(msg);

        return false;
    }

    private void sendNextMessage(OutputStreamWriter writer) {
        Message msg = outgoingMessages.poll();

        if (msg == null) {
            return;
        }

        byte[] payload = msg.payload.toString().getBytes();

        msg.header.payloadSize = (short) payload.length;

        byte[] header = new byte[HEADER_SIZE];

        shortToBytes(header, 0, msg.header.msgType);
        shortToBytes(header, 2, msg.header.payloadSize);

        try {
            writer.write(new String(header) + new String(payload));
            writer.flush();
        } catch (IOException ignored) {}
    }

    private Header parseHeader(char[] buffer, int read) {
        if (read < HEADER_SIZE) {
            throw new RuntimeException("Malformed header: not enough bytes");
        }

        short msgType;
        short payloadSize;

        try {
            msgType = (short) Integer.parseInt(new String(buffer), 0, 2, 10);
            payloadSize = (short) Integer.parseInt(new String(buffer), 2, 4, 10);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Malformed header: " + e);
        }

        if (!MsgType.inRangeServer(msgType)) {
            throw new RuntimeException("Malformed header: message type");
        }

        if (!(payloadSize >= 0 && payloadSize <= MAX_PAYLOAD_SIZE)) {
            throw new RuntimeException("Malformed header: payload size");
        }

        Header header = new Header();
        header.msgType = msgType;
        header.payloadSize = payloadSize;

        return header;
    }

    private JSONObject parsePayload(char[] buffer, int read, Header header) {
        if (read != header.payloadSize) {
            throw new RuntimeException("Not enough bytes for payload: got " + read + ", expected " + header.payloadSize);
        }

        JSONObject obj;

        try {
            obj = (JSONObject) new JSONTokener(new String(buffer)).nextValue();
        } catch (JSONException e) {
            throw new RuntimeException("Parse error: " + e);
        }

        return obj;
    }

    private void shortToBytes(byte[] buffer, int offset, short s) {
        buffer[offset] = (byte) (s >> 8);
        buffer[offset + 1] = (byte) (s);
    }
}
