package snake.app;

import snake.app.gameControlMessage.ControlMessage;
import snake.app.gameControlMessage.ControlMessageType;
import snake.app.gameUpdateMessage.GameUpdateMessage;
import snake.app.utils.NetUtil;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static snake.app.Config.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JFrame;

/**
 * Build the body of a Client.
 * Client1 and Client2 use Client to execute
 */
public class Client extends JFrame {
    private final ScheduledExecutorService scheduler;
    private final Board board;

    Client(Player player) {
        setTitle("Snake Game client");
        board = new Board();
        add(board);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        addKeyListener(new DirectionChangeHandler(player));

        // Render game board / run game
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                () -> {
                    board.repaint();
                },
                /* initial delay */ 0,
                GAME_SPEED_MS,
                MILLISECONDS);

        NetUtil.startMessageHandler(this::handleMessage, CLIENT_PORT);

        ControlMessage message = makeStartGameMessage(player);
        System.out.println("Send first request message: " + message);
        System.out.println("Player: " + player);
        NetUtil.sendMessage(message.encode(), SERVER_IP, SERVER_PORT);
    }

    private ControlMessage makeStartGameMessage(Player player) {
        ControlMessageType type = player.getPlayerID() == 1 ? ControlMessageType.CREATE_GAME : ControlMessageType.JOIN_GAME;
        ControlMessage m = new ControlMessage.Builder(type)
                .setGameID(GAME_ID)
                .setPlayerNickName(player.getNickName())
                .setPlayerIP(player.getAddress())
                .setPlayerPort(player.getPort())
                .build();
        return m;
    }


    private void handleMessage(byte[] bytes) {
        System.out.println("Received message from server: ");
//        System.out.println(Arrays.toString(bytes));
        GameUpdateMessage g = GameUpdateMessage.decode(bytes);
        System.out.println(g.toString());
        board.updateGame(g);
    }
}

