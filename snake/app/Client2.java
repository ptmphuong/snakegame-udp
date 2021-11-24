package snake.app;

import static snake.app.Config.*;
import static snake.app.Config.CLIENT_PORT;

/**
 * Run Player 2 to Join game
 * To change address and port for testing, please change that in the Config class.
 */
public class Client2 {
    public static void main(String[] args) {
        Player p2 = new Player(2, PLAYER2_NICKNAME, SNAKE2_IP, CLIENT_PORT);
        Client client = new Client(p2);
        client.setVisible(true);
    }
}
