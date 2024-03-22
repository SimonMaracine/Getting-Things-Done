import socket
import threading
import queue

import message


class Server:
    HOST = "localhost"
    PORT = 1922

    def __init__(self):
        self._listening = True
        self._message_queue: queue.Queue[message.Message] = queue.Queue()

    def run(self):
        print("Starting server...")

        listening_thread = threading.Thread(target=self._listen_for_connections)
        listening_thread.start()

        while True:
            try:
                message = self._message_queue.get(True, 2.0)
            except queue.Empty:
                continue
            except KeyboardInterrupt:
                break

        listening_thread.join()

        print("Stopped server...")

    def _listen_for_connections(self):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as listening_socket:
            listening_socket.bind((self.HOST, self.PORT))  # TODO error handling
            listening_socket.listen()

            print(f"Listening on {(self.HOST, self.PORT)}")

            while self._listening:
                connection, address = listening_socket.accept()  # FIXME

                threading.Thread(target=self._handle_connection, args=(connection, address)).start()

    def _handle_connection(self, connection: socket.socket, address):
        with connection:
            print(f"Client connected: {(connection, address)}")

            while True:
                message = self._receive_message(connection)

                if message is None:
                    break

                self._message_queue.put(message)

        print(f"Disconnected: {address}")

    def _receive_message(self, connection: socket.socket):
        data = connection.recv(2)

        if not data:
            # Client disconnected
            return None

        # TODO
