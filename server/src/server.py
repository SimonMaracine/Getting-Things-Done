import socket
import threading
import queue
import dataclasses

import message


class ClientDisconnect(RuntimeError):
    pass


@dataclasses.dataclass
class Client:
    connection: socket.socket
    thread: threading.Thread
    finished_serving: bool


class Server:
    _PORT = 1922

    def __init__(self):
        self._listening = True
        self._incoming_messages: queue.Queue[message.Message] = queue.Queue()
        self._outgoing_messages: queue.Queue[message.Message] = queue.Queue()

        self._clients: dict[int, Client] = {}
        self._clients_counter = 0
        self._clients_mutex = threading.Lock()

    def run(self):
        print("Starting server...")

        listening_thread = threading.Thread(target=self._listen_for_connections)
        listening_thread.start()

        while True:
            try:
                self._process_incoming_messages()
                self._process_finished_clients()
            except KeyboardInterrupt:
                self._interrupt()
                break

        listening_thread.join()

        # Don't acquire the mutex here
        for client in self._clients.values():
            if not client.finished_serving:
                client.connection.close()

        for client in self._clients.values():
            client.thread.join()

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

    def _interrupt(self):
        print()
        self._listening = False

    def _process_finished_clients(self):
        with self._clients_mutex:
            finished_clients = []

            for index, client in self._clients.items():
                if client.finished_serving:
                    client.thread.join()
                    finished_clients.append(index)

            for index in finished_clients:
                del self._clients[index]

    def _send_message(self, msg_type: int, payload: dict):
        self._outgoing_messages.put(message.Message(message.Header(msg_type, -1), payload))

    def _listen_for_connections(self):
        host = socket.gethostbyname(socket.gethostname())

        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as listening_socket:
            listening_socket.settimeout(3.0)
            listening_socket.bind((host, self._PORT))  # TODO error handling
            listening_socket.listen()

            print(f"Listening on {(host, self._PORT)}")

            while self._listening:
                try:
                    connection, address = listening_socket.accept()
                except BlockingIOError:
                    continue
                except TimeoutError:
                    continue

                connection.setblocking(False)

                thread = threading.Thread(target=self._handle_connection, args=(connection, address, self._clients_counter))

                with self._clients_mutex:
                    self._clients[self._clients_counter] = Client(connection, thread, False)
                self._clients_counter += 1

                thread.start()

    def _handle_connection(self, connection: socket.socket, address, index: int):
        with connection:
            print(f"Client connected: {address}")

            while True:
                try:
                    self._receive_next_message(connection)
                except ClientDisconnect:
                    break

                self._send_next_message(connection)

        with self._clients_mutex:
            self._clients[index].finished_serving = True

        print(f"Disconnected: {address}")

    def _receive_next_message(self, connection: socket.socket):
        try:
            data = connection.recv(message.HEADER_SIZE)
        except TimeoutError:
            return
        except BlockingIOError:
            return
        except OSError:
            raise ClientDisconnect()

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
        except OSError:
            raise ClientDisconnect()

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
