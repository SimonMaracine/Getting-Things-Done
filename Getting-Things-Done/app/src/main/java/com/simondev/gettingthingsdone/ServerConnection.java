package com.simondev.gettingthingsdone;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
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

    ServerConnection(String host, int port) throws ServerConnectionException {
        Future<?> future = executor.submit(() -> {
            try {
                socket = new Socket(host, port);
            } catch (IOException e) {
                throw new ServerConnectionExceptionRT("Could not connect to " + host + ": " + e);
            }
        });

        try {
            future.get();
        } catch (ExecutionException e) {
            throw new ServerConnectionException(e);
        } catch (InterruptedException e) {
            throw new ServerConnectionException("Unexpected error occurred in connecting to server: " + e);
        }
    }

    void sendReceiveLoop() {
        executor.execute(() -> {
            while (true) {
                try {
                    receiveNextMessage(socket.getInputStream());
                } catch (ClientDisconnect e) {
                    break;
                } catch (ServerConnectionException | IOException e) {
                    throw new ServerConnectionExceptionRT(e);
                }

                try {
                    sendNextMessage(socket.getOutputStream());
                } catch (ServerConnectionException | IOException e) {
                    throw new ServerConnectionExceptionRT(e);
                }
            }
        });
    }

    void sendReceivePair() throws ServerConnectionException {
        Future<?> future = executor.submit(() -> {
            boolean received = false;
            boolean sent = false;

            do {
                try {
                    if (sendNextMessage(socket.getOutputStream())) {
                        sent = true;
                    }
                } catch (ServerConnectionException | IOException e) {
                    throw new ServerConnectionExceptionRT(e);
                }

                try {
                    if (receiveNextMessage(socket.getInputStream())) {
                        received = true;
                    }
                } catch (ClientDisconnect e) {
                    throw new ServerConnectionExceptionRT("Disconnected from server");
                } catch (ServerConnectionException | IOException e) {
                    throw new ServerConnectionExceptionRT(e);
                }
            } while (!received || !sent);
        });

        try {
            future.get();
        } catch (ExecutionException e) {
            throw new ServerConnectionException(e);
        } catch (InterruptedException e) {
            throw new ServerConnectionException("Unexpected error occurred in send-receive pair: " + e);
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

    private boolean receiveNextMessage(InputStream stream) throws ClientDisconnect, ServerConnectionException {
        byte[] buffer = new byte[HEADER_SIZE];
        int result;

        try {
            if (stream.available() == 0) {
                return false;
            }

            result = stream.read(buffer, 0, HEADER_SIZE);
        } catch (IOException e) {
            throw new ServerConnectionException(e);
        }

        if (result < 0) {
            throw new ClientDisconnect();
        }

        Header header;

        try {
            header = parseHeader(buffer, result);
        } catch (ServerConnectionException e) {
            throw new ServerConnectionException(e);
        }

        buffer = new byte[header.payloadSize];

        try {
            if (stream.available() == 0) {
                return false;
            }

            result = stream.read(buffer, 0, header.payloadSize);
        } catch (IOException e) {
            throw new ServerConnectionException(e);
        }

        if (result < 0) {
            throw new ClientDisconnect();
        }

        JSONObject payload;

        try {
            payload = parsePayload(buffer, result, header);
        } catch (ServerConnectionException e) {
            throw new ServerConnectionException(e);
        }

        Message msg = new Message();
        msg.header = header;
        msg.payload = payload;

        incomingMessages.add(msg);

        return true;
    }

    private boolean sendNextMessage(OutputStream stream) throws ServerConnectionException {
        Message msg = outgoingMessages.poll();

        if (msg == null) {
            return false;
        }

        byte[] payload = msg.payload.toString().getBytes();

        msg.header.payloadSize = (short) payload.length;

        byte[] header = new byte[HEADER_SIZE];

        shortToBytes(header, 0, msg.header.msgType);
        shortToBytes(header, 2, msg.header.payloadSize);

        try {
            stream.write(header);  // FIXME
            stream.write(payload);
            stream.flush();
        } catch (IOException ignored) {
            throw new ServerConnectionException("Could not write to socket");
        }

        return true;
    }

    private Header parseHeader(byte[] buffer, int read) throws ServerConnectionException {
        if (read < HEADER_SIZE) {
            throw new ServerConnectionException("Malformed header: not enough bytes");
        }

        short msgType = bytesToShort(buffer, 0);
        short payloadSize = bytesToShort(buffer, 2);

        if (!MsgType.inRangeServer(msgType)) {
            throw new ServerConnectionException("Malformed header: message type: " + msgType);
        }

        if (!(payloadSize >= 0 && payloadSize <= MAX_PAYLOAD_SIZE)) {
            throw new ServerConnectionException("Malformed header: payload size: " + payloadSize);
        }

        Header header = new Header();
        header.msgType = msgType;
        header.payloadSize = payloadSize;

        return header;
    }

    private JSONObject parsePayload(byte[] buffer, int read, Header header) throws ServerConnectionException {
        if (read != header.payloadSize) {
            throw new ServerConnectionException("Not enough bytes for payload: got " + read + ", expected " + header.payloadSize);
        }

        JSONObject obj;

        try {
            obj = (JSONObject) new JSONTokener(new String(buffer)).nextValue();
        } catch (JSONException e) {
            throw new ServerConnectionException("Parse error: " + e);
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

class ServerConnectionExceptionRT extends RuntimeException {
    ServerConnectionExceptionRT(String message) {
        super(message);
    }

    ServerConnectionExceptionRT(Exception exception) {
        super(exception);
    }
}

class ServerConnectionException extends Exception {
    ServerConnectionException(String message) {
        super(message);
    }

    ServerConnectionException(Exception exception) {
        super(exception);
    }
}

class ClientDisconnect extends Exception {}
