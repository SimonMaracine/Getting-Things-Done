import dataclasses
import json


class MsgType:
    ClientPing = 1

    ServerPing = 10

    _ClientFirst = ClientPing
    _ClientLast = ClientPing

    def in_range_client(msg_type: int) -> bool:
        return MsgType._ClientFirst <= msg_type <= MsgType._ClientLast


@dataclasses.dataclass(frozen=True)
class Header:
    msg_type: int
    payload_size: int


@dataclasses.dataclass(frozen=True)
class Message:
    header: Header
    payload: dict


class MessageError(RuntimeError):
    pass


def parse_header(data: bytes) -> Header:
    if len(data) != 4:
        raise MessageError(f"Not enough bytes for header: {len(data)}")

    msg_type = int.from_bytes(data[:2])
    payload_size = int.from_bytes(data[2:])

    if not MsgType.in_range_client(msg_type):
        raise MessageError("Malformed header: message type")

    if not (0 <= payload_size < 512):
        raise MessageError("Malformed header: payload size")

    return Header(msg_type, payload_size)


def parse_payload(data: bytes, header: Header) -> dict:
    if len(data) != header.payload_size:
        raise MessageError(f"Not enough bytes for payload: got {len(data)}, expected {header.payload_size}")

    try:
        obj = json.loads(data)
    except json.JSONDecodeError as err:
        raise MessageError(f"Parse error: {err}")

    if type(obj) != dict:
        raise MessageError("Parse error: invalid object")

    obj: dict = obj

    match header.msg_type:
        case MsgType.ClientPing:
            keys = (
                ("msg", str),
            )

    for key in keys:
        if (key[0], key[1]) not in map(lambda item: (item[0], type(item[1])), obj.items()):
            raise MessageError(f"Parse error: missing or invalid key {key}")

    return obj


def dump_header(header: Header) -> bytes:
    return header.msg_type.to_bytes() + header.payload_size.to_bytes()


def dump_payload(payload: dict) -> bytes:
    return bytes(json.dumps(payload))
