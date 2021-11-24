package snake.app;

import snake.app.gameUpdateMessage.GameUpdateMessage;
import snake.app.gameUpdateMessage.GameUpdateMessageType;

import static snake.app.Config.BOARD_SIZE;
import static snake.app.Config.UNIT_SIZE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Board used by client to render UI based on GameUpdateMessage
 */
public class Board extends JPanel {
  private static final Image GREEN_DOT = new ImageIcon("snake/app/images/dot.png").getImage();
  private static final Image APPLE = new ImageIcon("snake/app/images/apple.png").getImage();
  private static final Image RED_DOT = new ImageIcon("snake/app/images/head.png").getImage();

  private GameUpdateMessage gameUpdateMessage = new GameUpdateMessage.Builder(GameUpdateMessageType.GAME_CREATED).build();

  public Board() {
    int size = UNIT_SIZE * BOARD_SIZE;
    setPreferredSize(new Dimension(size, size));
    setBorder(BorderFactory.createLineBorder(Color.BLUE));
    setBackground(Color.BLACK);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    switch (this.gameUpdateMessage.getType()) {
      case GAME_CREATED:
        renderGameCreated(g);
        return;
      case GAME_STARTING:
        renderGameStarting(g);
        return;
      case GAME_UPDATE:
        renderApple(g);
        renderSnake(g);
        return;
      case GAME_OVER:
        renderGameOver(g);
        return;
    }
  }

  public void updateGame(GameUpdateMessage gameUpdateMessage) {
    this.gameUpdateMessage = gameUpdateMessage;
  }

  private void renderApple(Graphics g) {
    Position applePosition = gameUpdateMessage.getGameStateInfo().getApplePosition();
    render(g, APPLE, applePosition);
  }

  private void renderSnake(Graphics g) {
    gameUpdateMessage.getGameStateInfo().getSnake1().forEach(p -> render(g, GREEN_DOT, p));
    gameUpdateMessage.getGameStateInfo().getSnake2().forEach(p -> render(g, RED_DOT, p));
  }

  private void renderGameOver(Graphics g) {
    String gameOver = "Game over";
    String result;
    if (isDraw()) result = "DRAW";
    else {
      String winner = this.gameUpdateMessage.getGameResult().getWinnerName().get();
      result = "winner: " + winner;
    }
    renderGameDisplay(g, gameOver);
    renderGameDisplay2ndLine(g, result);
  }

  private boolean isDraw() {
    return this.gameUpdateMessage.getGameResult().getWinnerName().isEmpty();
  }

  private void renderGameCreated(Graphics g) {
    String text = "Game Created. Waiting for 2nd player";
    renderGameDisplay(g, text);
  }

  private void renderGameStarting(Graphics g) {
    String text = "2 players join. Game starting";
    renderGameDisplay(g, text);
  }

  private void renderGameDisplay(Graphics g, String displayText) {
    g.setColor(Color.RED);
    g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
    g.drawString(displayText, 50, 50);
  }

  private void renderGameDisplay2ndLine(Graphics g, String displayText) {
    g.setColor(Color.RED);
    g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
    g.drawString(displayText, 50, 80);
  }

  private void render(Graphics g, Image image, Position p) {
    g.drawImage(image, p.getCol() * UNIT_SIZE, p.getRow() * UNIT_SIZE, this);
  }
}
