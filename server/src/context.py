import message
import client
import data


class Context:
    def __init__(self):
        self._registered_users = data.get_registered_users()

    def ping(self, msg: message.Message, cl: client.Client):
        print(msg.payload["msg"])

        self._ping(msg, cl)

    def sign_up(self, msg: message.Message, cl: client.Client):
        new_email = msg.payload["email"]
        new_password = msg.payload["password"]

        if not new_email:
            self._sign_up_fail(msg, cl, "Invalid email address")
            return

        for user in self._registered_users:
            if user.email == new_email:
                self._sign_up_fail(msg, cl, "Email address already in use")
                return

        if not new_password:
            self._sign_up_fail(msg, cl, "Invalid password")
            return

        if len(new_password) < 15:
            self._sign_up_fail(msg, cl, "Password too short")
            return

        user = data.User(new_email, new_password)

        data.register_user(user)
        self._registered_users.append(user)

        self._sign_up_ok(msg, cl)

    def log_in(self, msg: message.Message, cl: client.Client):
        pass

    def _ping(self, msg: message.Message, cl: client.Client):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerPing, {"msg": msg.payload["msg"]})
        )

    def _sign_up_ok(self, msg: message.Message, cl: client.Client):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerSignUpOk, {})
        )

    def _sign_up_fail(self, msg: message.Message, cl: client.Client, err_msg: str):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerSignUpFail, {"msg": err_msg})
        )

    def _log_in_ok(self, msg: message.Message, cl: client.Client):
        pass

    def _log_in_fail(self, msg: message.Message, cl: client.Client):
        pass

    def _construct_message(self, msg_type: int, payload: dict) -> message.Message:
        return message.Message(message.Header(msg_type, -1), payload)
