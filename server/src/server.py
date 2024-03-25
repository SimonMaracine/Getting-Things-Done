import socket
import threading
import queue

import message
import client
import context


class Server:
    _PORT = 1922

    def __init__(self):
        self._listening = True
        self._exit_code = 0

        self._clients: dict[int, client.ClientConnection] = {}
        self._clients_counter = 0
        self._clients_mutex = threading.Lock()

        self._ctx = context.Context()

    def run(self) -> int:
        print("Starting server...")

        listening_thread = threading.Thread(target=self._listen_for_connections)
        listening_thread.start()

        try:
            try:
                while True:
                    self._process_clients()
                    self._process_finished_clients()
            except KeyboardInterrupt:
                self._interrupt()
        except Exception as err:
            print(f"Unexpected error occurred: {err}")
            self._listening = False
            self._exit_code = 1

        listening_thread.join()

        with self._clients_mutex:
            cl: client.ClientConnection
            for cl in self._clients.values():
                if not cl.is_finished():
                    cl.close_connection()

            for cl in self._clients.values():
                cl.join_thread()

        print("Stopped server")

        return self._exit_code

    def _process_clients(self):
        with self._clients_mutex:
            cl: client.ClientConnection
            for cl in self._clients.values():
                try:
                    msg = cl.dequeue_message()
                except queue.Empty:
                    continue

                match msg.header.msg_type:
                    case message.MsgType.ClientPing:
                        self._ctx.ping(msg, cl)
                    case message.MsgType.ClientSignUp:
                        self._ctx.sign_up(msg, cl)
                    case message.MsgType.ClientLogIn:
                        self._ctx.log_in(msg, cl)
                    case message.MsgType.ClientGetTasks:
                        self._ctx.get_tasks(msg, cl)
                    case message.MsgType.ClientGetMotivational:
                        self._ctx.get_motivational(msg, cl)

    def _interrupt(self):
        print()
        self._listening = False

    def _process_finished_clients(self):
        with self._clients_mutex:
            finished_clients: list[int] = []

            cl: client.ClientConnection
            for index, cl in self._clients.items():
                if cl.is_finished():
                    cl.join_thread()
                    finished_clients.append(index)

            for index in finished_clients:
                del self._clients[index]

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

                cl = client.ClientConnection(connection, self._clients_counter)

                thread = threading.Thread(target=self._handle_connection, args=(cl, address))

                cl.set_thread(thread)

                with self._clients_mutex:
                    self._clients[self._clients_counter] = cl

                self._clients_counter += 1

                thread.start()

    def _handle_connection(self, cl: client.ClientConnection, address):
        with cl:
            print(f"Client connected: {address}, <{cl.get_index()}>")

            while True:
                try:
                    cl.receive_next_message()
                except client.ClientDisconnect:
                    break

                cl.send_next_message()

        print(f"Disconnected: {address}, <{cl.get_index()}>")
