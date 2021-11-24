package snake.app.gameUpdateMessage;

import snake.app.utils.ByteUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class GameResult {

    private GameFinalResult finalResult;
    private Optional<String> winnerName = Optional.empty();

    /**
     * Result of the game as a part of GameUpdateMessage
     * Use builder type to construct
     *
     * 2 types of GameResult:
     *      DRAW    - winnerName field will be empty
     *      DECIDED - winnerName field will have a winner's name
     * @param builder
     */
    private GameResult(Builder builder) {
        this.finalResult = builder.finalResult;
        this.winnerName = builder.winnerName;
    }

    public GameFinalResult getFinalResult() {
        return finalResult;
    }

    public Optional<String> getWinnerName() {
        return winnerName;
    }

    public byte[] encode() {
        switch (this.finalResult) {
            case DRAW: return encodeDraw();
            case DECIDED: return encodeDecided();
        }
        return new byte[0];
    }

    private byte[] encodeDraw() {
        byte b = (byte) this.finalResult.getValue();
        return new byte[] {b};
    }

    private byte[] encodeDecided() {
        byte b = (byte) this.finalResult.getValue();
        byte[] winnerNameB = this.winnerName.get().getBytes();
        byte winnerNameLB = (byte) winnerNameB.length;
        byte[] res = new byte[] {b, winnerNameLB};
        res = ByteUtil.concatenateByteArr(res, winnerNameB);
        return res;
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "finalResult=" + finalResult +
                ", winnerName='" + winnerName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameResult that = (GameResult) o;
        return finalResult == that.finalResult && Objects.equals(winnerName, that.winnerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(finalResult, winnerName);
    }

    public static class Builder {
        private GameFinalResult finalResult;
        private Optional<String> winnerName = Optional.empty();

        public Builder(GameFinalResult finalResult) {
            this.finalResult = finalResult;
        }

        public Builder setWinnerName(String winnerName) {
            this.winnerName = Optional.of(winnerName);
            return this;
        }

        public GameResult build() {
            return new GameResult(this);
        }
    }

    public static GameResult decode(byte[] bytes) {
        int type = (int) bytes[0];
        if (type == GameFinalResult.DRAW.getValue()) {
            return new GameResult.Builder(GameFinalResult.DRAW).build();
        } else {
            byte[] nameB = Arrays.copyOfRange(bytes, 2, bytes.length);
            String winnerName = new String(nameB);
            return new GameResult.Builder(GameFinalResult.DECIDED)
                    .setWinnerName(winnerName)
                    .build();
        }
    }

}
