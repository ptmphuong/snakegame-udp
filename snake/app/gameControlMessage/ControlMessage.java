package snake.app.gameControlMessage;

import snake.app.Direction;
import snake.app.utils.ByteUtil;
import java.util.Arrays;

/**
 * Control Information sent by client to serer
 * Types of control message: create game, join game, change direction.
 */
public class ControlMessage {
    private ControlMessageType type;
    private int gameID;
    private String nickName;
    private String IP;
    private int port;
    private Direction direction;

    /**
     * Constructor of ControlMessage. Construct by builder types.
     * CREATE GAME and JOIN GAME: set type, set id, set player nick name, set ip, set port.
     * CHANGE DIRECTION: set type, set id, set player nick name, set direction.
     * @param builder
     */
    private ControlMessage(Builder builder) {
        this.type = builder.type;
        this.gameID = builder.gameID;
        this.nickName = builder.nickName;
        this.IP = builder.IP;
        this.port = builder.port;
        this.direction = builder.direction;
    }

    public ControlMessageType getType() {
        return type;
    }

    public int getGameID() {
        return gameID;
    }

    public String getNickName() {
        return nickName;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public Direction getDirection() {
        return direction;
    }

    private String getDirectionStr() {
        if (this.getDirection().equals(null)) return "null";
        switch (this.getDirection()) {
            case UP:
                return "UP";
            case DOWN:
                return "DOWN";
            case LEFT:
                return "LEFT";
            case RIGHT:
                return "RIGHT";
        }
        return "idk";
    }

    private byte getDirectionByte() {
        return (byte) this.getDirection().getValue();
    }

    /**
     * Encode the class into byte array per homework requirement.
     * CREATE GAME / JOIN GAME:
     *      message type (1) + game_id length (1) + nickname length (1) + game_id (in bytes) + nickname (in bytes) + ip (4) + port (2)
     * CHANGE DIRECTION:
     *  message type (1) + game_id length (1) + nickname length (1) + game_id (in bytes) + nickname (in bytes) + direction (1)
     *
     * @return
     */
    public byte[] encode() {
        switch (this.type) {
            case CREATE_GAME: case JOIN_GAME:
                return encodeRegisterGame();
            case CHANGE_DIRECTION:
                return encodeChangeDirection();
        }
        return new byte[0];
    }

    private byte[] encodeChangeDirection() {
        byte[] res = encodeMessageHead();
        res = ByteUtil.concatenateByteArr(res, this.getDirectionByte());
        return res;
    }

    private byte[] encodeRegisterGame() {
        byte[] head = encodeMessageHead();
        byte[] ipB = ByteUtil.ipTo4Bytes(this.IP);
        byte[] portB = ByteUtil.intToByteArr2(this.port);
        byte[] res = ByteUtil.concatenateByteArr(head, ipB);
        res = ByteUtil.concatenateByteArr(res, portB);
        return res;
    }

    private byte[] encodeMessageHead() {
        byte typeB = (byte) this.type.getValue();
        byte[] gameIDB = ByteUtil.intToByteArr(this.gameID);
        byte gameIDBLenB = (byte) gameIDB.length;
        byte[] nickNameB = this.nickName.getBytes();
        byte nickNameLenB = (byte) nickNameB.length;

        byte[] res = new byte[3];
        res[0] = typeB;
        res[1] = gameIDBLenB;
        res[2] = nickNameLenB;
        res = ByteUtil.concatenateByteArr(res, gameIDB);
        res = ByteUtil.concatenateByteArr(res, nickNameB);
        return res;
    }

    @Override
    public String toString() {
        return "ControlMessage{" +
                "type=" + type +
                ", gameID=" + gameID +
                ", nickName='" + nickName + '\'' +
                ", IP='" + IP + '\'' +
                ", port=" + port +
                ", direction=" + direction +
                '}';
    }

    public static class Builder {
        private ControlMessageType type;
        private int gameID;
        private String nickName;
        private String IP;
        private int port;
        private Direction direction;

        public Builder(ControlMessageType type) {
            this.type = type;
        }

        public Builder setGameID(int gameID) {
            this.gameID = gameID;
            return this;
        }

        public Builder setPlayerNickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder setPlayerIP(String ip) {
            this.IP = ip;
            return this;
        }

        public Builder setPlayerPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setDirection(Direction d) {
            this.direction = d;
            return this;
        }

        public ControlMessage build() {
            ControlMessage controlMessage = new ControlMessage(this);
            return controlMessage;
        }
    }

    /**
     * Convert encoded byte arrays back to ControlMessage type
     * @param bytes
     */
    public static ControlMessage decode(byte[] bytes) {
        int type = (int) bytes[0];
        int gameIDLength = (int) bytes[1];
        int nickNameLength = (int) bytes[2];
        int offset = 3;
        byte[] gameIDB = Arrays.copyOfRange(bytes, offset, offset + gameIDLength);
        byte[] nickNameB = Arrays.copyOfRange(bytes, offset + gameIDLength, offset + gameIDLength + nickNameLength);
        int decodedGameID = ByteUtil.byteArrToInt(gameIDB);
        String decodedNickName = new String(nickNameB);

        if (type == 1 || type == 2) {
            return ControlMessage.decodeRegisterGameMessage(bytes, type, decodedGameID, decodedNickName);
        } else {
            return ControlMessage.decodeChangeDirectionMessage(bytes, type, decodedGameID, decodedNickName);
        }

    }

    private static ControlMessageType getControlMessageType(int typeValue) {
        if (typeValue == ControlMessageType.CREATE_GAME.getValue()) return ControlMessageType.CREATE_GAME;
        if (typeValue == ControlMessageType.JOIN_GAME.getValue()) return ControlMessageType.JOIN_GAME;
        else return ControlMessageType.CHANGE_DIRECTION;
    }

    private static ControlMessage decodeRegisterGameMessage(byte[] bytes, int type, int gameID, String nickName) {
        ControlMessageType controlMessageType = getControlMessageType(type);
        int len = bytes.length;
        byte[] ipB = Arrays.copyOfRange(bytes, len - 6, len - 2);
        byte[] portB = Arrays.copyOfRange(bytes, len - 2, len);
        String ip = ByteUtil.ipBytesToString(ipB);
        int port = ByteUtil.byteArrToInt(portB);

        ControlMessage controlMessage = new ControlMessage.Builder(controlMessageType)
                .setGameID(gameID)
                .setPlayerNickName(nickName)
                .setPlayerIP(ip)
                .setPlayerPort(port)
                .build();
        return controlMessage;
    }

    private static ControlMessage decodeChangeDirectionMessage(byte[] bytes, int type, int gameID, String nickName) {
        ControlMessageType controlMessageType = getControlMessageType(type);
        int dirValue = (int) bytes[bytes.length - 1];
        Direction direction = getDirection(dirValue);
        ControlMessage controlMessage = new ControlMessage.Builder(controlMessageType)
                .setGameID(gameID)
                .setPlayerNickName(nickName)
                .setDirection(direction)
                .build();
        return controlMessage;
    }

    private static Direction getDirection(int directionValue) {
        if (directionValue == 0) return Direction.UP;
        if (directionValue == 1) return Direction.RIGHT;
        if (directionValue == 2) return Direction.DOWN;
        else return Direction.LEFT;
    }

}
