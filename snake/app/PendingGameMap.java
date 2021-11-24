package snake.app;

import snake.app.utils.NetUtil;
import snake.app.gameUpdateMessage.GameUpdateMessage;
import snake.app.gameUpdateMessage.GameUpdateMessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static snake.app.Config.CLIENT_PORT;
import static snake.app.Config.SNAKE1_IP;
/**
 * Store a Map of awaiting Player(s) with gameID as keys, Players as values.
 * This Map is implemented with ReentrantReadWriteLock be accessible safely by multiple threads.
 */
public class PendingGameMap {
    private Map<Integer, Player> pendingGames;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    public PendingGameMap() {
        this.pendingGames = new HashMap<>();
    }

    public int getSize() {
        readLock.lock();
        try {
            return pendingGames.size();
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsGame(int gameID) {
        readLock.lock();
        try {
            return this.pendingGames.containsKey(gameID);
        } finally {
            readLock.unlock();
        }
    }

    public Player getPlayer(int gameID) {
        readLock.lock();
        try {
            return pendingGames.get(gameID);
        } finally {
            readLock.unlock();
        }
    }

    public void addPendingGame(int gameID, Player player) {
        writeLock.lock();
        try {
            this.pendingGames.put(gameID, player);
        } finally {
            writeLock.unlock();
        }
    }

    public void removePendingGame(int gameID) {
        if (!this.containsGame(gameID)) return;
        writeLock.lock();
        try {
            this.pendingGames.remove(gameID);
        } finally {
            writeLock.unlock();
        }
    }

    public void sendPendingUpdate() {
        for (Map.Entry<Integer, Player> entry: this.pendingGames.entrySet()) {
            Player player = entry.getValue();
            GameUpdateMessage message = new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_CREATED).build();
            NetUtil.sendMessage(message.encode(), SNAKE1_IP, CLIENT_PORT);
        }
    }
}
