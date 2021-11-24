package snake.app;

import snake.app.gameUpdateMessage.*;
import snake.app.utils.NetUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static snake.app.Config.CLIENT_PORT;
import static snake.app.Config.SNAKE1_IP;

/**
 * Store a Map of GameState(s) with gameID as keys, GameStates as values.
 * This Map is implemented with ReentrantReadWriteLock be accessible safely by multiple threads.
 */

public class GameMap {
    boolean active;
    protected Map<Integer, GameState> games;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    public GameMap(boolean active) {
        this.active = active;
        this.games = new HashMap<>();
    }

    public int getSize() {
        readLock.lock();
        try {
            return games.size();
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsGame(int gameID) {
        readLock.lock();
        try {
            return this.games.containsKey(gameID);
        } finally {
            readLock.unlock();
        }
    }

    public GameState getGame(int gameID) {
        readLock.lock();
        try {
            return games.getOrDefault(gameID, GameState.get());
        } finally {
            readLock.unlock();
        }
    }

    public void addGame(int gameID, GameState game) {
        writeLock.lock();
        try {
            this.games.put(gameID, game);
        } finally {
            writeLock.unlock();
        }
    }

    public void removeGame(int gameID) {
        if (!this.containsGame(gameID)) return;
        writeLock.lock();
        try {
            this.games.remove(gameID);
        } finally {
            writeLock.unlock();
        }
    }

    public void moveSnakeInGame() {
        writeLock.lock();
        try {
            this.games.values().forEach(GameState::moveSnake);
        } finally {
            writeLock.unlock();
        }
    }

    public void updateGameDirection(int gameID, Direction dir, String playerNickName) {
        writeLock.lock();
        try {
            this.games.get(gameID).updateDirection(dir, playerNickName);
        } finally {
            writeLock.unlock();
        }

    }

    public void sendUpdate() {
        readLock.lock();
        try {
            if (this.games.isEmpty()) return;
            if (active) sendUpdateActiveGames();
            else sendUpdatePendingGames();
        } finally {
            readLock.unlock();
        }
    }

    private void sendUpdatePendingGames() {
        for (Map.Entry<Integer, GameState> entry: this.games.entrySet()) {
            GameUpdateMessage message = new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_CREATED).build();
            NetUtil.sendMessage(message.encode(), SNAKE1_IP, CLIENT_PORT);
        }
    }

    private void sendUpdateActiveGames() {
        for (Map.Entry<Integer, GameState> entry: this.games.entrySet()) {
            int gameID = entry.getKey();
            GameState game = entry.getValue();
            GameUpdateMessage message;
            if (game.isGameOver()) {
                GameResult gameResult;
                if (game.getWinner().isEmpty()) {
                    gameResult = new GameResult.Builder(GameFinalResult.DRAW).build();
                } else {
                    String winner = game.getWinner().get();
                    gameResult = new GameResult.Builder(GameFinalResult.DECIDED).setWinnerName(winner).build();
                }
                message = new GameUpdateMessage
                        .Builder(GameUpdateMessageType.GAME_OVER)
                        .setGameResult(gameResult)
                        .build();
                games.remove(gameID);
            } else {
                Position applePos = game.getApplePosition();
                LinkedList<Position> snake1Pos = game.getSnake1().getSnakeBody();
                LinkedList<Position> snake2Pos = game.getSnake2().getSnakeBody();
                GameStateInformation gameInfo = new GameStateInformation(applePos, snake1Pos, snake2Pos);
                message = new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_UPDATE)
                        .setGameStateInfo(gameInfo)
                        .build();
            }
            NetUtil.sendToBothPlayer(message.encode());
        }
    }


}
