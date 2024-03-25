package com.simondev.gettingthingsdone;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

class ServerConnection {
    private Socket socket;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ConcurrentLinkedQueue<Message> incomingMessages = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> outgoingMessages = new ConcurrentLinkedQueue<>();

    private static final int HEADER_SIZE = 4;
    private static final int MAX_PAYLOAD_SIZE = 512 - HEADER_SIZE;

    ServerConnection(String host, int port) throws ConnectionException {
        Future<?> future = executor.submit(() -> {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(3000);
            } catch (IOException e) {
                throw new ConnectionExceptionRT("Could not connect to " + host + ": " + e);
            }
        });

        awaitFuture(future);
    }

    void sendReceive() throws ConnectionException {
        Future<?> future = sendReceiveAsync();

        awaitFuture(future);
    }

    Future<?> sendReceiveAsync() {
        return executor.submit(() -> {
            boolean received = false;
            boolean sent = false;

            do {
                try {
                    if (sendNextMessage(socket.getOutputStream())) {
                        sent = true;
                    }
                } catch (ConnectionException | IOException e1) {
                    throw new ConnectionExceptionRT(e1);
                }

                try {
                    if (receiveNextMessage(socket.getInputStream())) {
                        received = true;
                    }
                } catch (ClientDisconnect e) {
                    try {
                        socket.close();
                    } catch (IOException ignored) {}

                    throw new ConnectionExceptionRT("Disconnected from server");
                } catch (ConnectionException | IOException e) {
                    throw new ConnectionExceptionRT(e);
                }
            } while (!received || !sent);
        });
    }

    static void awaitFuture(Future<?> future) throws ConnectionException {
        try {
            future.get();
        } catch (ExecutionException e) {
            throw new ConnectionException(e);
        } catch (InterruptedException e) {
            throw new ConnectionException("Unexpected error occurred: " + e);
        }
    }

    void sendMessage(short msgType, JSONObject payload) {
        Message msg = new Message();
        msg.header = new Header();
        msg.header.msgType = msgType;
        msg.payload = payload;

        outgoingMessages.add(msg);
    }

    Message receiveMessage() {
        Message msg;

        do {
            msg = incomingMessages.poll();
        } while (msg == null);

        return msg;
    }

    void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    private boolean sendNextMessage(OutputStream stream) throws ConnectionException {
        Message msg = outgoingMessages.poll();

        if (msg == null) {
            return false;
        }

        byte[] payload = msg.payload.toString().getBytes();

        msg.header.payloadSize = (short) payload.length;

        byte[] header = new byte[HEADER_SIZE];

        shortToBytes(header, 0, msg.header.msgType);
        shortToBytes(header, 2, msg.header.payloadSize);

        byte[] all = new byte[header.length + payload.length];
        System.arraycopy(header, 0, all, 0, header.length);
        System.arraycopy(payload, 0, all, header.length, payload.length);

        try {
            stream.write(all);
            stream.flush();
        } catch (IOException ignored) {
            throw new ConnectionException("Could not write to socket");
        }

        return true;
    }

    private boolean receiveNextMessage(InputStream stream) throws ClientDisconnect, ConnectionException {
        byte[] buffer = new byte[HEADER_SIZE];
        int result;

        try {
            result = stream.read(buffer, 0, HEADER_SIZE);
        } catch (SocketTimeoutException e) {
            throw new ClientDisconnect();
        } catch (IOException e) {
            throw new ConnectionException(e);
        }

        if (result < 0) {
            throw new ClientDisconnect();
        }

        Header header;

        try {
            header = parseHeader(buffer, result);
        } catch (ConnectionException e) {
            throw new ConnectionException(e);
        }

        buffer = new byte[header.payloadSize];

        try {
            result = stream.read(buffer, 0, header.payloadSize);
        } catch (SocketTimeoutException e) {
            throw new ClientDisconnect();
        } catch (IOException e) {
            throw new ConnectionException(e);
        }

        if (result < 0) {
            throw new ClientDisconnect();
        }

        JSONObject payload;

        try {
            payload = parsePayload(buffer, result, header);
        } catch (ConnectionException e) {
            throw new ConnectionException(e);
        }

        Message msg = new Message();
        msg.header = header;
        msg.payload = payload;

        incomingMessages.add(msg);

        return true;
    }

    private Header parseHeader(byte[] buffer, int read) throws ConnectionException {
        if (read < HEADER_SIZE) {
            throw new ConnectionException("Malformed header: not enough bytes");
        }

        short msgType = bytesToShort(buffer, 0);
        short payloadSize = bytesToShort(buffer, 2);

        if (!MsgType.inRangeServer(msgType)) {
            throw new ConnectionException("Malformed header: message type: " + msgType);
        }

        if (!(payloadSize >= 0 && payloadSize <= MAX_PAYLOAD_SIZE)) {
            throw new ConnectionException("Malformed header: payload size: " + payloadSize);
        }

        Header header = new Header();
        header.msgType = msgType;
        header.payloadSize = payloadSize;

        return header;
    }

    private JSONObject parsePayload(byte[] buffer, int read, Header header) throws ConnectionException {
        if (read != header.payloadSize) {
            throw new ConnectionException("Not enough bytes for payload: got " + read + ", expected " + header.payloadSize);
        }

        JSONObject obj;

        try {
            obj = (JSONObject) new JSONTokener(new String(buffer)).nextValue();
        } catch (JSONException e) {
            throw new ConnectionException("Parse error: " + e);
        }

        return obj;
    }

    private void shortToBytes(byte[] buffer, int offset, short s) {
        buffer[offset] = (byte) (s >> 8);
        buffer[offset + 1] = (byte) (s);
    }

    private short bytesToShort(byte[] buffer, int offset) {
        short result = 0;

        result |= (short) ((buffer[offset] << 8) & 0xff00);
        result |= (short) ((buffer[offset + 1]) & 0x00ff);

        return result;
    }
}

class ConnectionExceptionRT extends RuntimeException {
    ConnectionExceptionRT(String message) {
        super(message);
    }

    ConnectionExceptionRT(Exception exception) {
        super(exception);
    }
}

class ConnectionException extends Exception {
    ConnectionException(String message) {
        super(message);
    }

    ConnectionException(Exception exception) {
        super(exception);
    }
}

class ClientDisconnect extends Exception {}
