import pprint

import message
import client
import data


class Context:
    def __init__(self):
        self._registered_users = data.get_registered_users()

        print("Currently registered users:")
        pprint.pprint(self._registered_users)

    def ping(self, msg: message.Message, cl: client.ClientConnection):
        print(msg.payload["msg"])

        self._ping(msg, cl)

    def sign_up(self, msg: message.Message, cl: client.ClientConnection):
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

    def log_in(self, msg: message.Message, cl: client.ClientConnection):
        user_email = msg.payload["email"]
        user_password = msg.payload["password"]

        if not user_email:
            self._log_in_fail(msg, cl, "Invalid email address")
            return

        if not user_password:
            self._log_in_fail(msg, cl, "Invalid password")
            return

        for user in self._registered_users:
            if user.email == user_email:
                if user.password == user_password:
                    self._log_in_ok(msg, cl)
                    return
                else:
                    self._log_in_fail(msg, cl, "Incorrect password")
                    return

        self._log_in_fail(msg, cl, "No account with that email address")

    def _ping(self, msg: message.Message, cl: client.ClientConnection):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerPing, {"msg": msg.payload["msg"]})
        )

    def _sign_up_ok(self, msg: message.Message, cl: client.ClientConnection):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerSignUpOk, {})
        )

    def _sign_up_fail(self, msg: message.Message, cl: client.ClientConnection, err_msg: str):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerSignUpFail, {"msg": err_msg})
        )

    def _log_in_ok(self, msg: message.Message, cl: client.ClientConnection):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerLogInOk, {})
        )

    def _log_in_fail(self, msg: message.Message, cl: client.ClientConnection, err_msg: str):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerLogInFail, {"msg": err_msg})
        )

    def _construct_message(self, msg_type: int, payload: dict) -> message.Message:
        return message.Message(message.Header(msg_type, -1), payload)
