package snake.app.gameUpdateMessage;

import snake.app.Position;
import snake.app.utils.ByteUtil;

import java.util.*;

public class GameUpdateMessage {
    GameUpdateMessageType type;
    GameResult gameResult;
    GameStateInformation gameStateInfo;

    /**
     * Game Update Information sent by server to client. Class constructing using Builder.
     * Types of Game Update Information:
     *     GAME CREATED     (Player 1 created a game, waiting for 2nd player)
     *     GAME CREATED    (2 players joined, game playing)
     *     GAME OVER        (Game ended)
     *     GAME UPDATE      (Changes from the game: new apple position, a player changes direction, a player died, ect)
     * Components of each type:
     *      GAME CREATED    type
     *      GAME CREATED    type
     *      GAME OVER       type + GameResult
     *      GAME UPDATE     type + GameStateInformation
     */
    private GameUpdateMessage(Builder builder) {
        this.type = builder.type;
        this.gameResult = builder.gameResult;
        this.gameStateInfo = builder.gameStateInfo;
    }

    public GameUpdateMessageType getType() {
        return type;
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public GameStateInformation getGameStateInfo() {
        return gameStateInfo;
    }

    /**
     * Encode the class into byte array per homework requirement.
     *      GAME CREATED    type (1)
     *      GAME CREATED    type (1)
     *      GAME OVER       type (1) + GameResult (encode in GameResult class)
     *      GAME UPDATE     type (1) + GameStateInformation (encode in GameStateInformation class)
     * @return
     */
    public byte[] encode() {
        switch (this.type) {
            case GAME_CREATED: case GAME_STARTING:
                return encodeGamePending();
            case GAME_OVER:
                return encodeGameOver();
            case GAME_UPDATE:
                return encodeGameUpdate();
        }
        return new byte[0];
    }

    private byte[] encodeGamePending() {
        byte b = (byte) this.type.getValue();
        return new byte[] {b};
    }

    private byte[] encodeGameOver() {
        byte[] res = new byte[] {(byte) this.type.getValue()};
        byte[] gameResultB = this.gameResult.encode();
        res = ByteUtil.concatenateByteArr(res, gameResultB);
        return res;
    }

    private byte[] encodeGameUpdate() {
        byte[] res = new byte[] {(byte) this.type.getValue()};
        byte[] gameStateInfo = this.gameStateInfo.encode();
        res = ByteUtil.concatenateByteArr(res, gameStateInfo);
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameUpdateMessage that = (GameUpdateMessage) o;
        return type == that.type && Objects.equals(gameResult, that.gameResult) && Objects.equals(gameStateInfo, that.gameStateInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, gameResult, gameStateInfo);
    }

    @Override
    public String toString() {
        return "GameUpdateMessage{" +
                "type=" + type +
                ", gameResult=" + gameResult +
                ", gameStateInfo=" + gameStateInfo +
                '}';
    }

    /**
     * Convert encoded byte arrays back to GameUpdateMessage type
     * @param bytes
     */
    public static GameUpdateMessage decode(byte[] bytes) {
        int type = bytes[0];
        if (type == GameUpdateMessageType.GAME_CREATED.getValue()) {
            return new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_CREATED).build();
        } else if (type == GameUpdateMessageType.GAME_STARTING.getValue()) {
            return new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_STARTING).build();
        } else if (type == GameUpdateMessageType.GAME_OVER.getValue()) {
            byte[] gameResultBytes = Arrays.copyOfRange(bytes, 1, bytes.length);
            GameResult r = GameResult.decode(gameResultBytes);
            return new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_OVER)
                    .setGameResult(r)
                    .build();
        } else {
            byte[] gameStateInfoBytes = Arrays.copyOfRange(bytes, 1, bytes.length);
            GameStateInformation i = GameStateInformation.decode(gameStateInfoBytes);
            return new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_UPDATE)
                    .setGameStateInfo(i)
                    .build();
        }
    }

    public static class Builder {
        GameUpdateMessageType type;
        GameResult gameResult;
        GameStateInformation gameStateInfo;

        public Builder(GameUpdateMessageType type) {
            this.type = type;
        }

        public Builder setGameResult(GameResult gameResult) {
            this.gameResult = gameResult;
            return this;
        }

        public Builder setGameStateInfo(GameStateInformation gameStateInfo) {
            this.gameStateInfo = gameStateInfo;
            return this;
        }

        public GameUpdateMessage build() {
            GameUpdateMessage gameUpdateMessage = new GameUpdateMessage(this);
            return gameUpdateMessage;
        }
    }


    public static void main(String[] args) {
        Position s11 = new Position(3, 10);
        Position s12 = new Position(4, 10);
        Position s13 = new Position(5, 10);
        LinkedList<Position> snake1p = new LinkedList<>(Arrays.asList(s11, s12, s13));

        Position s21 = new Position(31, 24);
        Position s22 = new Position(30, 24);
        Position s23 = new Position(29, 24);
        LinkedList<Position> snake2p = new LinkedList<>(Arrays.asList(s21, s22, s23));

        Position a = new Position(10, 19);
        GameStateInformation state = new GameStateInformation(a, snake1p, snake2p);
        GameUpdateMessage g = new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_UPDATE).setGameStateInfo(state).build();
        System.out.println(g.getGameStateInfo().getSnake1());
        System.out.println(g.getGameStateInfo().getSnake2());
        byte[] expectedB = g.encode();
        GameUpdateMessage expectedD = GameUpdateMessage.decode(expectedB);
    }

}
