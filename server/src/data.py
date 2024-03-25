import dataclasses
import json


_REGISTERED_USERS_FILE = "registered_users"
_TASKS_FILE = "tasks"


@dataclasses.dataclass(slots=True)
class User:
    email: str
    password: str


@dataclasses.dataclass(slots=True)
class Task:
    contents: str
    done: bool


@dataclasses.dataclass(slots=True)
class List:
    user_email: str
    name: str
    tasks: list[Task]


def create_registered_users_file():
    with open(_REGISTERED_USERS_FILE, "w"):
        pass


def get_registered_users() -> list[User]:
    try:
        with open(_REGISTERED_USERS_FILE, "r") as file:
            lines = file.readlines()
    except FileNotFoundError:
        create_registered_users_file()
        return []

    result = []

    for line in lines:
        email, password = line.split(",")

        result.append(User(email, password.rstrip()))

    return result


def register_user(user: User):
    with open(_REGISTERED_USERS_FILE, "a") as file:
        file.write(user.email + "," + user.password + "\n")


def create_tasks_file():
    with open(_TASKS_FILE, "w"):
        pass


def get_tasks() -> list[List]:
    try:
        obj: list[dict] = json.load(_TASKS_FILE)
    except FileNotFoundError:
        create_tasks_file()
        return []
    except json.JSONDecodeError as err:
        print(err)
        return []

    result = []

    for todo_list in obj:
        user_email: str = todo_list["user_email"]
        name: str = todo_list["name"]
        tasks: list[dict] = todo_list["tasks"]

        result_tasks = [Task(task["contents"], task["done"]) for task in tasks]

        result.append(List(user_email, name, result_tasks))

    return result
