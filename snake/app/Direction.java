package snake.app;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Directions for snake to move
 */
public enum Direction {
  UP(0),
  RIGHT(1),
  DOWN(2),
  LEFT(3);

  private int value;

  Direction(int v) {
    value = v;
  }

  public int getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    switch (this) {
      case RIGHT: return "RIGHT";
      case UP: return "UP";
      case LEFT: return "LEFT";
      case DOWN: return "DOWN";
    }
    return "Direction{}";
  }

  public static Direction random() {
    return Direction.values()[ThreadLocalRandom.current().nextInt(4)];
  }

}
