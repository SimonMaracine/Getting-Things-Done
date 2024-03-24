import server


def main() -> int:
    try:
        getting_things_done_server = server.Server()
        getting_things_done_server.run()
    except Exception as err:
        print(f"Unexpected error occurred: {err}")
        return 1

    return 0
