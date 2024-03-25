import socket
import threading
import queue

import message


class ClientDisconnect(RuntimeError):
    pass


class ClientConnection:
    """Class that represents a client; it handles communication between a client and the server and it stores a reference to the thread"""

    def __init__(self, connection: socket.socket, index: int):
        self._connection = connection
        self._index = index
        self._thread: threading.Thread = None
        self._finished_serving = False

        self._incoming_messages: queue.Queue[message.Message] = queue.Queue()
        self._outgoing_messages: queue.Queue[message.Message] = queue.Queue()

    def __enter__(self):
        return self._connection.__enter__()

    def __exit__(self, exc_type, exc_value, exc_traceback):
        self._connection.__exit__(exc_type, exc_value, exc_traceback)

        self._finished_serving = True

    def close_connection(self):
        self._connection.close()

    def join_thread(self):
        self._thread.join()

    def is_finished(self) -> bool:
        return self._finished_serving

    def set_thread(self, thread: threading.Thread):
        self._thread = thread

    def get_index(self) -> int:
        return self._index

    def enqueue_message(self, msg: message.Message):
        self._outgoing_messages.put(msg)

    def dequeue_message(self) -> message.Message:
        return self._incoming_messages.get(False)

    def receive_next_message(self):
        """Try to read the socket and put the message into the queue"""

        try:
            data = self._connection.recv(message.HEADER_SIZE)
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
            data = self._connection.recv(header.payload_size)
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

    def send_next_message(self):
        """Extract the next message from the queue and try to write it to the socket"""

        try:
            msg = self._outgoing_messages.get(False)
        except queue.Empty:
            return

        payload = message.dump_payload(msg.payload)

        msg.header.payload_size = len(payload)
        header = message.dump_header(msg.header)

        self._connection.sendall(header + payload)
