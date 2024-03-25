import pprint
import random

import message
import client
import data


class Context:
    def __init__(self):
        self._registered_users: list[data.User] = data.get_registered_users()
        self._lists: list[data.List] = data.get_tasks()
        self._motivational: list[tuple[str, str]] = data.get_motivational()

        print("Currently registered users:")
        pprint.pprint(self._registered_users)

        print("Stored tasks:")
        for todo_list in self._lists:
            print(f"Name: {todo_list.name}, count: {len(todo_list.tasks)}, user: {todo_list.user_email}")

        print(f"Motivationals: {len(self._motivational)}")

    def ping(self, msg: message.Message, cl: client.ClientConnection):
        print(msg.payload["msg"])

        self._ping(cl, msg.payload["msg"])

    def sign_up(self, msg: message.Message, cl: client.ClientConnection):
        new_email = msg.payload["email"]
        new_password = msg.payload["password"]

        if not new_email:
            self._sign_up_fail(cl, "Invalid email address")
            return

        for user in self._registered_users:
            if user.email == new_email:
                self._sign_up_fail(cl, "Email address already in use")
                return

        if not new_password:
            self._sign_up_fail(cl, "Invalid password")
            return

        if len(new_password) < 15:
            self._sign_up_fail(cl, "Password too short")
            return

        user = data.User(new_email, new_password)

        data.register_user(user)
        self._registered_users.append(user)

        self._sign_up_ok(cl)

    def log_in(self, msg: message.Message, cl: client.ClientConnection):
        user_email = msg.payload["email"]
        user_password = msg.payload["password"]

        if not user_email:
            self._log_in_fail(cl, "Invalid email address")
            return

        if not user_password:
            self._log_in_fail(cl, "Invalid password")
            return

        for user in self._registered_users:
            if user.email == user_email:
                if user.password == user_password:
                    self._log_in_ok(cl)
                    return
                else:
                    self._log_in_fail(cl, "Incorrect password")
                    return

        self._log_in_fail(cl, "No account with that email address")

    def get_tasks(self, msg: message.Message, cl: client.ClientConnection):
        user_email = msg.payload["email"]

        lists = self._prepare_tasks_for_client(user_email)

        for todo_list in lists:
            self._offer_tasks_l(cl, todo_list.name)

            for task in todo_list.tasks:
                self._offer_tasks_t(cl, todo_list.name, task)

        self._end_tasks(cl)

    def get_motivational(self, msg: message.Message, cl: client.ClientConnection):
        text, author = random.choice(self._motivational)

        self._offer_motivational(cl, text, author)

    def _ping(self, cl: client.ClientConnection, msg: str):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerPing, {"msg": msg})
        )

    def _sign_up_ok(self, cl: client.ClientConnection):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerSignUpOk, {})
        )

    def _sign_up_fail(self, cl: client.ClientConnection, err_msg: str):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerSignUpFail, {"msg": err_msg})
        )

    def _log_in_ok(self, cl: client.ClientConnection):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerLogInOk, {})
        )

    def _log_in_fail(self, cl: client.ClientConnection, err_msg: str):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerLogInFail, {"msg": err_msg})
        )

    def _offer_tasks_l(self, cl: client.ClientConnection, name: str):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerOfferTasksL, {"name": name})
        )

    def _offer_tasks_t(self, cl: client.ClientConnection, name: str, task: data.Task):
        cl.enqueue_message(
            self._construct_message(
                message.MsgType.ServerOfferTasksT,
                {"name": name, "contents": task.contents, "done": task.done}
            )
        )

    def _end_tasks(self, cl: client.ClientConnection):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerEndTasks, {})
        )

    def _offer_motivational(self, cl: client.ClientConnection, text: str, author: str):
        cl.enqueue_message(
            self._construct_message(message.MsgType.ServerOfferMotivational, {"text": text, "author": author})
        )

    def _construct_message(self, msg_type: int, payload: dict) -> message.Message:
        return message.Message(message.Header(msg_type, -1), payload)

    def _prepare_tasks_for_client(self, user_email: str) -> list[data.List]:
        result = []

        for todo_list in self._lists:
            if todo_list.user_email == user_email:
                result.append(todo_list)

        return result
