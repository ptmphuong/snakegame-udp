package snake.app;

import snake.app.gameControlMessage.ControlMessage;
import snake.app.utils.NetUtil;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static snake.app.Config.*;

public class Server {
    private final GameMap activeGames;
    private final PendingGameMap pendingGames;
    private final ScheduledExecutorService scheduler;

    Server() {
        activeGames = new GameMap(true);
        pendingGames = new PendingGameMap();
        scheduler = Executors.newScheduledThreadPool(6);

        // Thread to update games.
        scheduler.scheduleAtFixedRate(() -> {
                    activeGames.moveSnakeInGame();
                },
                /* initialDelay */ 0,
                GAME_SPEED_MS,
                MILLISECONDS);

        // Thread to PENDING game updates.
        scheduler.scheduleAtFixedRate(() -> {
                    pendingGames.sendPendingUpdate();
                },
                /* initialDelay */ 0,
                GAME_UPDATE_MS,
                MILLISECONDS);

        // Thread to send game updates.
        scheduler.scheduleAtFixedRate(() -> {
                    activeGames.sendUpdate();
                },
                /* initialDelay */ 0,
                GAME_UPDATE_MS,
                MILLISECONDS);

        // Thread to show debug info.
        scheduler.scheduleAtFixedRate(() -> {
                    System.out.println(String.format("There are %d active games", activeGames.getSize()));
                    System.out.println(String.format("There are %d pending games", pendingGames.getSize()));
                },
                /* initialDelay */ 0,
                5,
                SECONDS);

        // Thread handles incoming control messages.
        NetUtil.startMessageHandler(this::handleMessage, SERVER_PORT);

        System.out.println("SnakeServer start.");
    }

    private void handleMessage(byte[] d) {
        System.out.println("received control message: ");

        ControlMessage controlMessage = ControlMessage.decode(d);
        System.out.println(controlMessage);

        int gameID = controlMessage.getGameID();

        switch (controlMessage.getType()) {
            case CREATE_GAME:
                Player player1 = new Player(1, controlMessage.getNickName(), controlMessage.getIP(), controlMessage.getPort());
                System.out.println(String.format("Create game [%s]", controlMessage.getNickName()));
                pendingGames.addPendingGame(gameID, player1); // TODO: pending game
                break;

            case JOIN_GAME:
                System.out.println(String.format("Join game [%s]", controlMessage.getNickName()));
                Player player2 = new Player(2, controlMessage.getNickName(), controlMessage.getIP(), controlMessage.getPort());
                if (pendingGames.containsGame(gameID)) {
//                    GameState game = pendingGames.getGame(gameID);
                    Player pendingPlayer = pendingGames.getPlayer(gameID);
                    pendingGames.removePendingGame(gameID);
                    activeGames.addGame(gameID, new GameState(pendingPlayer, player2));
                }
                // TODO handle when no game in map
                break;

            case CHANGE_DIRECTION:
                System.out.println("Case change dir");
                if (activeGames.containsGame(gameID)) {
                    Direction dir = controlMessage.getDirection();
                    System.out.println(String.format("Direction update [%d:%s]",
                            gameID, dir)
                    );
                    activeGames.updateGameDirection(gameID, dir, controlMessage.getNickName());

                }
                break;
        }
    }

    public static void main(String... args) {
        new Server();
    }

}
