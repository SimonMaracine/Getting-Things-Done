import server


def main() -> int:
    getting_things_done = server.Server()
    return getting_things_done.run()
