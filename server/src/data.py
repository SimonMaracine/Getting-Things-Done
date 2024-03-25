import dataclasses


_REGISTERED_USERS_FILE = "registered_users"


@dataclasses.dataclass
class User:
    email: str
    password: str


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

    users = []

    for line in lines:
        email, password = line.split(",")

        users.append(User(email, password.rstrip()))

    return users


def register_user(user: User):
    with open(_REGISTERED_USERS_FILE, "a") as file:
        file.write(user.email + "," + user.password + "\n")
