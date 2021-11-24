package snake.app;

import java.net.InetAddress;
import java.util.Objects;

public class Player {
    int playerID;
    private String nickName;
    private String address;
    private int port;

    private static final String LOCAL_HOST = "localhost";
    private static final String LOCAL_HOST_ADDRESS = "127.0.0.1";

    public Player(Integer playerID, String nickName, String address, int port) {
        this.playerID = playerID;
        this.nickName = nickName;
        this.setAddress(address);
        this.port = port;
    }

    private void setAddress(String address) {
        if (address.equals(LOCAL_HOST)) this.address = LOCAL_HOST_ADDRESS;
        else this.address = address;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getNickName() {
        return nickName;
    }

    public byte[] getNickNameInBytes() {
        return this.nickName.getBytes();
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }


    @Override
    public String toString() {
        return "Player{" +
                "nickName='" + nickName + '\'' +
                ", address=" + address +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return port == player.port && Objects.equals(nickName, player.nickName) && Objects.equals(address, player.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickName, address, port);
    }
}
