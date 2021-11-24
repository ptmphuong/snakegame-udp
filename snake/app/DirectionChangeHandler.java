package snake.app;

import snake.app.gameControlMessage.ControlMessage;
import snake.app.gameControlMessage.ControlMessageType;
import snake.app.utils.NetUtil;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static snake.app.Config.*;


class DirectionChangeHandler extends KeyAdapter {
  private Player player;

  public DirectionChangeHandler(Player player) {
    this.player = player;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    System.out.println("key pressed");
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        sendDirectionUpdate(Direction.LEFT);
        break;
      case KeyEvent.VK_RIGHT:
        sendDirectionUpdate(Direction.RIGHT);
        break;
      case KeyEvent.VK_UP:
        sendDirectionUpdate(Direction.UP);
        break;
      case KeyEvent.VK_DOWN:
        sendDirectionUpdate(Direction.DOWN);
        break;
    }
  }

  private void sendDirectionUpdate(Direction d) {
    ControlMessage controlMessage = new ControlMessage
            .Builder(ControlMessageType.CHANGE_DIRECTION)
            .setGameID(GAME_ID)
            .setPlayerNickName(player.getNickName())
            .setDirection(d)
            .build();
    System.out.println("Send update message due to key pressed: " + controlMessage);
    NetUtil.sendMessage(controlMessage.encode(), SERVER_IP, SERVER_PORT);
  }
}
