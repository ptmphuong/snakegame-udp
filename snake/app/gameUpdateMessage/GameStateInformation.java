package snake.app.gameUpdateMessage;

import snake.app.Position;
import snake.app.utils.ByteUtil;

import java.util.*;

public class GameStateInformation {
    private static final int ENCODED_ARRAY_BYTES_LENGTH = 1 + 2 + 128 + 128;
    private static final int ENCODED_SNAKE_ARRAY_BYTES_LENGTH = 128;
    private int sequenceNum = 0;
    private Position applePosition;
    private LinkedList<Position> snake1;
    private LinkedList<Position> snake2;

    /**
     * Store information of a game state
     * @param applePosition
     * @param snake1
     * @param snake2
     */
    public GameStateInformation(Position applePosition, LinkedList<Position> snake1, LinkedList<Position> snake2) {
        this.applePosition = applePosition;
        this.snake1 = snake1;
        this.snake2 = snake2;
    }

    /**
     * Encode the class into byte array per homework requirement.
     *      Sequence num (1) +
     *      Apple row (1) + Apple col (1)
     *      Bit map of Snake1 position (128) + Bit map of Snake2 position (128)
     *
     * Each snake position is encoded as follows:
     *      Board size is 32. So each row can be mapped into 4 bytes.
     *      Use bits to represent if a snake is in that position of the board.
     * @return
     */
    public byte[] encode() {
        byte[] seqN = encodeSequenceNumber();
        byte[] applePos = this.applePosition.encode();
        byte[] snake1Pos = encodeSnakePosition(this.snake1);
        byte[] snake2Pos = encodeSnakePosition(this.snake2);
        byte[] res = ByteUtil.concatenateByteArr(seqN, applePos);
        res = ByteUtil.concatenateByteArr(res, snake1Pos);
        res = ByteUtil.concatenateByteArr(res, snake2Pos);
        return res;
    }

    private byte[] encodeSequenceNumber() {
        return new byte[] {(byte) sequenceNum};
    }

    private static byte[] encodeSnakePosition(LinkedList<Position> snakePosition) {
        byte[] snakePos = new byte[128];
//        System.out.println("ENCODING");
        for (Position p: snakePosition) {
//            System.out.println("pos: " + p);
            int byteIndex = getPositionIndex(p);
            int shiftValue = getShiftValue(p.getCol());
            int mask = (1 << shiftValue);
            byte newVal = (byte) (mask | snakePos[byteIndex]);
//            System.out.println("original: " + ByteUtil.bitsInByteStr(snakePos[byteIndex]) + " mask: " + ByteUtil.bitsInByteStr(mask) + " newVal: " + ByteUtil.bitsInByteStr(newVal));
            snakePos[byteIndex] = newVal;
//            System.out.println("val put in: " + ByteUtil.bitsInByteStr(snakePos[byteIndex]));
        }
        return snakePos;
    }

    public Position getApplePosition() {
        return applePosition;
    }

    public LinkedList<Position> getSnake1() {
        return snake1;
    }

    public LinkedList<Position> getSnake2() {
        return snake2;
    }

    public static int getPositionIndex(Position p) {
        int rowOffSet = p.getRow() * 4;
        int colOffSet = p.getCol() / 8;
        int index = rowOffSet + colOffSet;
//        System.out.println("row: " + rowOffSet + " colOffset: " + colOffSet + " indexPos: " + index);
        return index;
    }
    public static int getShiftValue(int col) {
        int val = 7 - (col % 8);
//        System.out.println("shift val: " + val);
        return val;
    }

    @Override
    public String toString() {
        return "GameStateInformation{" +
                "sequenceNum=" + sequenceNum +
                ", applePosition=" + applePosition +
                ", snake1=" + snake1 +
                ", snake2=" + snake2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStateInformation that = (GameStateInformation) o;
        return sequenceNum == that.sequenceNum && Objects.equals(applePosition, that.applePosition) && Objects.equals(snake1, that.snake1) && Objects.equals(snake2, that.snake2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceNum, applePosition, snake1, snake2);
    }

    public static GameStateInformation decode(byte[] bytes) {
        if (bytes.length != ENCODED_ARRAY_BYTES_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid byte array size for GameStateInformation. Should be " + ENCODED_ARRAY_BYTES_LENGTH
            );
        }
        int appleBytesStart = 1;
        int snake1BytesStart = 1 + 2;
        int snake2BytesStart = 3 + 128;
        byte[] applePositionBytes = Arrays.copyOfRange(bytes, appleBytesStart, snake1BytesStart);
        byte[] snake1Bytes = Arrays.copyOfRange(bytes, snake1BytesStart, snake2BytesStart);
        byte[] snake2Bytes = Arrays.copyOfRange(bytes, snake2BytesStart, bytes.length);

        Position applePosition = Position.decode(applePositionBytes);
        LinkedList<Position> snake1Position = decodeSnakePosition(snake1Bytes);
        LinkedList<Position> snake2Position = decodeSnakePosition(snake2Bytes);
        GameStateInformation g = new GameStateInformation(applePosition, snake1Position, snake2Position);
        return g;
    }

    /**
     * Convert encoded byte arrays back to ControlMessage type
     * @param bytes
     * @return
     */
    //  00 01 00 11     00 01 00 10
    //  11 11 11 10     11 11 11 10
    //  OR
    //=>11 11 11 11     11 11 11 10
    public static LinkedList<Position> decodeSnakePosition(byte[] bytes) {
        if (bytes.length != ENCODED_SNAKE_ARRAY_BYTES_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid byte array size for snake position. Should be " + ENCODED_SNAKE_ARRAY_BYTES_LENGTH
            );
        }

        List<Position> result = new ArrayList<>();
        int mask = -2; // 11 11 11 10

        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            if (b != 0) {
//                System.out.println("Position at index: " + i);
                int row =  i / 4;
                int colOffset = (i % 4) * 8;
//                System.out.println("row: " + row);
//                System.out.println("colOffSet: " + colOffset);
                int[] bitValue = ByteUtil.byteToIntArr(bytes[i]);
                for (int index = 0; index < bitValue.length; index++) {
                    if (bitValue[index] == 1) {
                        int col = colOffset + index;
//                        System.out.println("col: " + col);
                        result.add(new Position(row, col));
                    }
                }
            }
        }
        Collections.sort(result);
        LinkedList<Position> r = new LinkedList<>(result);

        return r;
    }

    public static void testShiftValPrint() {
        byte original = 1;
        System.out.println("original: " + ByteUtil.bitsInByteStr(original));
        for (byte col = 0; col < 8; col++) {
            int shiftVal = getShiftValue(col);
            int s = 1 << shiftVal;
            byte masked = (byte) (original | s);
            System.out.println("col: " + col + " | "
                    + "shiftVal: " + shiftVal + " | " + ByteUtil.bitsInByteStr((byte) s)
                    + " | " + ByteUtil.bitsInByteStr(masked));
        }
    }

    private static void printBoard(byte[] snakePositionBytes) {
        String[][] strings = new String[32][4];
        for (int i = 0; i < 128; i++) {
            int row = i / 4;
            int col = i % 4;
            strings[row][col] = ByteUtil.bitsInByteStr(snakePositionBytes[i]);
        }

        for (String[] s: strings) {
            System.out.println(Arrays.toString(s));
        }
    }

    public static void main(String args[])
    {
        Position apple = new Position(10, 19);

        Position p1 = new Position(3, 10);
        Position p2 = new Position(4, 10);
        Position p3 = new Position(5, 10);


        Position p8 = new Position(31, 24);
        Position p9 = new Position(30, 24);
        Position p10 = new Position(29, 24);

        LinkedList<Position> snake1 = new LinkedList<>(Arrays.asList(p1, p2, p3));
        LinkedList<Position> snake2 = new LinkedList<>(Arrays.asList(p8, p9, p10));

        Collections.sort(snake1);
        Collections.sort(snake2);

        GameStateInformation gE = new GameStateInformation(apple, snake1, snake2);
        byte[] bytes = gE.encode();
        byte[] snake1bytes = encodeSnakePosition(snake1);
        byte[] snake2bytes = encodeSnakePosition(snake2);

        printBoard(snake2bytes);
        System.out.println(Arrays.toString(snake2bytes));

        System.out.println("Decoding snake 2");
        LinkedList<Position> snake2decoded = decodeSnakePosition(snake2bytes);
        System.out.println(snake2decoded);
    }
}
