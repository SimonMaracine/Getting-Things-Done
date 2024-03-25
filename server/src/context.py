import message
import client


class Context:
    def __init__(self):
        pass

    def process_ping(self, msg: message.Message, cl: client.Client):
        print(msg.payload["msg"])

        msg = self._construct_message(message.MsgType.ServerPing, {"msg": msg.payload["msg"]})
        cl.enqueue_message(msg)

    def process_sign_up(self, msg: message.Message, cl: client.Client):
        pass

    def process_log_in(self, msg: message.Message, cl: client.Client):
        pass

    def _construct_message(self, msg_type: int, payload: dict) -> message.Message:
        return message.Message(message.Header(msg_type, -1), payload)
