import socket
import threading


class Server:
    HOST = "localhost"
    PORT = 1922

    def __init__(self):
        pass

    def run(self):
        print("Starting server...")

        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as listening_socket:
            listening_socket.bind((self.HOST, self.PORT))  # TODO error handling
            listening_socket.listen()

            print(f"Listening on {(self.HOST, self.PORT)}")

            try:
                connection, address = listening_socket.accept()
            except KeyboardInterrupt:
                print()
                return

            threading.Thread(target=self._handle_connection, args=(connection, address)).start()

    def _handle_connection(connection: socket.socket, address):
        print(f"Client connection: {connection}")

        connection.close()
