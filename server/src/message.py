import enum
import dataclasses


class MessageType(enum.Enum):
    Hello = enum.auto()


@dataclasses.dataclass(frozen=True)
class Header:
    msg_type: MessageType


@dataclasses.dataclass(frozen=True)
class Message:
    header: Header
    payload: bytes


def parse_header(data: bytes) -> Header:
    pass


def parse_message(data: bytes) -> Message:
    pass
