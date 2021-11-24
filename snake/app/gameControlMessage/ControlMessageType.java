package snake.app.gameControlMessage;

public enum ControlMessageType {
    CREATE_GAME(1),
    JOIN_GAME(2),
    CHANGE_DIRECTION(3);

    private int value;

    ControlMessageType(int v) {
        value = v;
    }

    public int getValue() {
        return this.value;
    }
}
