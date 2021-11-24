package snake.app.gameUpdateMessage;

public enum GameUpdateMessageType {
    GAME_CREATED(4),
    GAME_STARTING(5),
    GAME_OVER(6),
    GAME_UPDATE(7);

    private int value;

    GameUpdateMessageType(int v) {
        value = v;
    }

    public int getValue() {
        return this.value;
    }
}
