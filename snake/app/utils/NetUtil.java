package snake.app.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.function.Consumer;

import static snake.app.Config.*;

public class NetUtil {

    /**
     * Send message from one address to another.
     * @param data
     * @param destPort
     */
    public static void sendMessage(byte[] data, String destAdd, int destPort) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
//            InetAddress addr = InetAddress.getByName("localhost");
            InetAddress addr = InetAddress.getByName(destAdd);
            DatagramPacket pkt = new DatagramPacket(data, data.length, addr, destPort);
            clientSocket.send(pkt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startMessageHandler(Consumer<byte[]> f, int port) {
        Thread msgHandler = new Thread(() -> {
            try (DatagramSocket serverSocket = new DatagramSocket(port)) {
                while (true) {
                    byte[] buf = new byte[BUF_SIZE];
                    DatagramPacket pkt = new DatagramPacket(buf, BUF_SIZE);
                    serverSocket.receive(pkt);
                    byte[] data = Arrays.copyOfRange(pkt.getData(), pkt.getOffset(), pkt.getOffset() + pkt.getLength());
                    f.accept(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        msgHandler.setDaemon(true);
        msgHandler.start();
    }

    public static void sendToBothPlayer(byte[] data) {
        NetUtil.sendMessage(data, SNAKE1_IP, CLIENT_PORT);
        NetUtil.sendMessage(data, SNAKE2_IP, CLIENT_PORT);
    }
}
