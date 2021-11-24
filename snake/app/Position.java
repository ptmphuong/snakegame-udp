package snake.app;

import java.util.concurrent.ThreadLocalRandom;
import static snake.app.Config.BOARD_SIZE;

/**
 * Position - row, column on the Board.
 */
public class Position implements Comparable<Position> {
  private int row;
  private int col;
  private int size = BOARD_SIZE;
  private static final int ENCODED_ARRAY_BYTES_LENGTH = 2;

  public Position(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public byte[] encode() {
    byte[] bytes = new byte[] {
            (byte) this.row,
            (byte) this.col,
    };
    return bytes;
  }

  public static Position decode(byte[] bytes) {
    if (bytes.length !=  ENCODED_ARRAY_BYTES_LENGTH) {
      throw new IllegalArgumentException("Invalid byte array size for Position. Should be " + ENCODED_ARRAY_BYTES_LENGTH);
    }
    int r = bytes[0];
    int c = bytes[1];
    return new Position(r, c);
  }

  public static Position random(int size) {
    return new Position(ThreadLocalRandom.current().nextInt(size),
                        ThreadLocalRandom.current().nextInt(size));
  }

  public static Position copy(Position o) {
    return new Position(o.getRow(), o.getCol());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Position)) {
      return false;
    }
    Position op = (Position) o;
    return op.getRow() == row && op.getCol() == col && op.getSize() == size;
  }

  @Override
  public int hashCode() {
    return (row * 31 + col) * 31 + size;
  }

  @Override
  public String toString() {
    return String.format("([%d,%d],%d)", row, col, size);
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public int getSize() {
    return size;
  }

  /**
   * Move position of snake head, snake can collide with the edge of the window.
   * @param d current moving direction of the snake
   * @return new position after moved
   */
  public Position move(Direction d) {
    switch (d) {
      case UP:
        row = (size + row - 1) % size;
        break;
      case RIGHT:
        col = (col + 1) % size;
        break;
      case DOWN:
        row = (row + 1) % size;
        break;
      case LEFT:
        col = (size + col - 1) % size;
        break;
    }
    return this;
  }

  /**
   * Move position of snake head, snake can NOT collide with the edge of the window.
   * @param d current moving direction of the snake
   * @return new position after moved
   */
  public Position moveInBoard(Direction d) {
    switch (d) {
      case UP:
        row = row - 1;
        break;
      case RIGHT:
        col = col + 1;
        break;
      case DOWN:
        row = row + 1;
        break;
      case LEFT:
        col = col - 1;
        break;
    }
    return this;
  }

  /**
   * Check if the position is outside the board.
   * @return
   */
  public boolean outOfBound() {
    return this.row < 0 || this.row >= this.size || this.col < 0 || this.col >= this.size;
  }


  @Override
  public int compareTo(Position o) {
    if (this.row == o.getRow()) {
      return (Integer.compare(this.col, o.getCol()));
    }
    return Integer.compare(this.row, o.getRow());
  }
}
