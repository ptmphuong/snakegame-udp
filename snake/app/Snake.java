package snake.app;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static snake.app.Config.BOARD_SIZE;

public class Snake {
    private LinkedList<Position> snakeBody;
    private Direction direction;
    private Player player;

    public Snake(Player player) {
        this.player = player;
        generateSnakeBody();
        generateDir();
    }

    public Snake(Player player, boolean generateSnake) {
        this.player = player;
    }

    public LinkedList<Position> getSnakeBody() {
        return snakeBody;
    }

    private void generateSnakeBody() {
//    Position head = Position.random(BOARD_SIZE/2);
        int leftSide = BOARD_SIZE/3;
        int rightSide = BOARD_SIZE*3/4;
        int firstPosition = this.getPlayer().getPlayerID() == 1 ? leftSide : rightSide;
        Position head = new Position(firstPosition, firstPosition);
        this.snakeBody = Stream.of(head,
                        Position.copy(head).move(Direction.DOWN),
                        Position.copy(head).move(Direction.DOWN).move(Direction.DOWN))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private void generateDir() {
        this.direction = Direction.random();
    }

    public Direction getDirection() {
        return direction;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean move(Snake opponentSnake, Position applePosition) {
        Position nextHead = this.getNextHead();
        if (this.moveOutOfBound(nextHead) ||
                this.hitItself(nextHead) ||
                this.hitSomeSnake(nextHead, opponentSnake.getSnakeBody())
            ) {
            return false;
        } else {
            this.snakeBody.addFirst(this.getNextHead());
            if (!this.meetApple(applePosition)) this.snakeBody.removeLast();
            return true;
        }
    }

    public boolean updateDirection(Direction direction) {
        Position next = this.getNextHead();
        if (next.outOfBound()) return false;
        if (!next.equals(snakeBody.get(1))) {
            this.direction = direction;
        }
        return true;
    }

    public boolean appleInSnakeBody(Position apple) {
        return this.getSnakeBody().stream().anyMatch(p -> p.equals(apple));
    }

    private boolean moveOutOfBound(Position nextHead) {
        System.out.println("out of bound: " + nextHead.outOfBound());
        return nextHead.outOfBound();
    }

    public boolean meetApple(Position applePosition) {
        return this.getNextHead().equals(applePosition);
    }

    public Position getNextHead() {
        return Position.copy(snakeBody.getFirst()).moveInBoard(direction);
    }

    private boolean hitItself(Position nextHead) {
        System.out.println("hit it self: " + this.hitSomeSnake(nextHead, this.getSnakeBody()));
        return this.hitSomeSnake(nextHead, this.getSnakeBody());
    }

    private boolean hitSomeSnake(Position nextHead, LinkedList<Position> snake) {
        for (Position p : snake) {
            if (p.equals(snake.getFirst()) || p.equals(snake.getLast())) {
                continue;
            }
            if (p.equals(nextHead)) {
                System.out.println("hit some snake, next head: " + nextHead);
                return true;
            }
        }
        return false;
    }


        @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snake snakeInfo = (Snake) o;
        return Objects.equals(snakeBody, snakeInfo.snakeBody) && direction == snakeInfo.direction && Objects.equals(player, snakeInfo.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snakeBody, direction, player);
    }

    @Override
    public String toString() {
        return "SnakeInfo{" +
                "snake=" + snakeBody +
                ", dir=" + direction +
                ", player=" + player +
                '}';
    }
}
