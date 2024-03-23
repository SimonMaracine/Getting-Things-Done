import socket
import threading
import queue

import message


class ClientDisconnect(RuntimeError):
    pass


class Server:
    HOST = "localhost"
    PORT = 1922

    def __init__(self):
        self._listening = True
        self._incoming_messages: queue.Queue[message.Message] = queue.Queue()
        self._outgoing_messages: queue.Queue[message.Message] = queue.Queue()

    def run(self):
        print("Starting server...")

        listening_thread = threading.Thread(target=self._listen_for_connections)
        listening_thread.start()

        while True:
            try:
                self._process_incoming_messages()
            except KeyboardInterrupt:
                print()
                self._listening = False
                break

        listening_thread.join()

        print("Stopped server")

    def _process_incoming_messages(self):
        try:
            msg = self._incoming_messages.get(True, 2.0)
        except queue.Empty:
            return

        match msg.header.msg_type:
            case message.MsgType.ClientPing:
                print(msg.payload["msg"])
                self._send_message(message.MsgType.ServerPing, {"msg": msg.payload["msg"]})

    def _send_message(self, msg_type: int, payload: dict):
        self._outgoing_messages.put(message.Message(message.Header(msg_type, -1), payload))

    def _listen_for_connections(self):
        host = socket.gethostbyname(socket.gethostname())

        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as listening_socket:
            listening_socket.settimeout(3.0)
            listening_socket.bind((host, self.PORT))  # TODO error handling
            listening_socket.listen()

            print(f"Listening on {(host, self.PORT)}")

            while self._listening:
                try:
                    connection, address = listening_socket.accept()
                except BlockingIOError:
                    continue
                except TimeoutError:
                    continue

                connection.setblocking(False)

                threading.Thread(target=self._handle_connection, args=(connection, address)).start()

    def _handle_connection(self, connection: socket.socket, address):
        with connection:
            print(f"Client connected: {(connection, address)}")

            while True:
                try:
                    self._receive_next_message(connection)
                except ClientDisconnect:
                    break

                self._send_next_message(connection)

        print(f"Disconnected: {address}")

    def _receive_next_message(self, connection: socket.socket):
        try:
            data = connection.recv(message.HEADER_SIZE)
        except TimeoutError:
            return
        except BlockingIOError:
            return

        if not data:
            raise ClientDisconnect()

        try:
            header = message.parse_header(data)
        except message.MessageError as err:
            print(err)
            return

        try:
            data = connection.recv(header.payload_size)
        except TimeoutError:
            return
        except BlockingIOError:
            return

        if not data:
            raise ClientDisconnect()

        try:
            payload = message.parse_payload(data, header)
        except message.MessageError as err:
            print(err)
            return

        self._incoming_messages.put(message.Message(header, payload))

    def _send_next_message(self, connection: socket.socket):
        try:
            msg = self._outgoing_messages.get(False)
        except queue.Empty:
            return

        payload = message.dump_payload(msg.payload)

        msg.header.payload_size = len(payload)
        header = message.dump_header(msg.header)

        connection.sendall(header + payload)

# TODO keep track of connections to close them when Ctrl+C
