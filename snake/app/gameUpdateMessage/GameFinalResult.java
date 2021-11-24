package snake.app.gameUpdateMessage;

public enum GameFinalResult {
    DRAW(0), DECIDED(1);
    private int value;

    GameFinalResult(int v) {
        value = v;
    }

    public int getValue() {
        return this.value;
    }
}



