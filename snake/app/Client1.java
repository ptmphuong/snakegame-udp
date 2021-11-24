package snake.app;

import static snake.app.Config.*;

/**
 * Run Player 1 to Create game
 * To change address and port for testing, please change that in the Config class.
 */
public class Client1 {
    public static void main(String[] args) {
        Player p1 = new Player(1, PLAYER1_NICKNAME, SNAKE1_IP, CLIENT_PORT);
        Client client = new Client(p1);
        client.setVisible(true);
    }
}
