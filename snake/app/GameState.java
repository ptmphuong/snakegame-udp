package snake.app;

import static snake.app.Config.BOARD_SIZE;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class GameState {
  private static GameState INSTANCE;
  private Position apple;
  private Snake snake1;
  private Snake snake2;
  private boolean gameOver;
  private Optional<String> winner = Optional.empty();

  GameState(Player player1, boolean generateSnake) {
    this.snake1 = new Snake(player1, generateSnake);
  }

  GameState() {
    initGame();
  }

  GameState(Player player1, Player player2) {
    this.snake1 = new Snake(player1);
    this.snake2 = new Snake(player2);
    initGame();
  }

  private void initGame() {
    generateApple();
    gameOver = false;
  }

  public static synchronized GameState get() {
    if (INSTANCE == null) {
      INSTANCE = new GameState();
    }
    return INSTANCE;
  }

  public Position getApplePosition() {
    return apple;
  }

  public Optional<String> getWinner() {
    return this.winner;
  }

  public Snake getSnake1() {
    return snake1;
  }

  public Snake getSnake2() {
    return snake2;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  private boolean isPlayer1(String playerNickName) {
    return this.snake1.getPlayer().getNickName().equals(playerNickName);
  }

  public void updateDirection(Direction newDir, String playerNickName) {
    boolean update = this.isPlayer1(playerNickName) ? snake1.updateDirection(newDir) : snake2.updateDirection(newDir);
    if (!update) this.gameOver = true;
  }

  private void printSnakePositions() {
    System.out.println("1: " + snake1);
    System.out.println("2: " + snake2);
  }

  public void moveSnake() {
    if (gameOver) {
      return;
    }

    Position apple = this.getApplePosition();

    if (snake1.getNextHead().equals(snake2.getNextHead())) {
      this.gameOver = true;
    }

    if (!snake2.move(snake1, apple)) {
      this.gameOver = true;
      String winnerName = snake1.getPlayer().getNickName() + " (snake2/red hits somebody)";
      this.winner = Optional.of(winnerName);
      return;
    }

    if (!snake1.move(snake2, apple)) {
      this.gameOver = true;
      String winnerName = snake2.getPlayer().getNickName() + " (snake1/green hits somebody)";
      this.winner = Optional.of(winnerName);
      return;
    }

    if (snake1.meetApple(apple) || snake2.meetApple(apple)) {
      generateApple();
    }
  }

  private void generateApple() {
    do {
      apple = Position.random(BOARD_SIZE);
    } while (snake1.appleInSnakeBody(apple) || snake2.appleInSnakeBody(apple));
  }
}
