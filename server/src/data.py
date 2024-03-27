import dataclasses
import json


_REGISTERED_USERS_FILE = "registered_users"
_TASKS_FILE = "tasks"
_MOTIVATIONAL = "MOTIVATIONAL"


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
    with open(_TASKS_FILE, "w") as file:
        file.write("[]")


def get_tasks() -> list[List]:
    try:
        with open(_TASKS_FILE, "r") as file:
            try:
                obj: list[dict] = json.load(file)
            except json.JSONDecodeError as err:
                print(err)
                return []
    except FileNotFoundError:
        create_tasks_file()
        return []

    result = []

    for todo_list in obj:
        user_email: str = todo_list["user_email"]
        name: str = todo_list["name"]
        tasks: list[dict] = todo_list["tasks"]

        result_tasks = [Task(task["contents"], task["done"]) for task in tasks]

        result.append(List(user_email, name, result_tasks))

    return result


def get_motivational() -> list[tuple[str, str]]:
    with open(_MOTIVATIONAL, "r") as file:
        lines = file.readlines()

    result = []

    for line in lines:
        text, author = line.split(" | ")

        result.append((text, author.rstrip()))

    return result
