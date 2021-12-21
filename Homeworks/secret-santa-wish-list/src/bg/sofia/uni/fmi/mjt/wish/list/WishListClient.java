package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.command.CommandType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class WishListClient {

    private static final int BUFFER_SIZE = 512;

    private final ByteBuffer buffer;
    private final String host;
    private final int port;

    public WishListClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open(); Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(host,port));

            new Thread(new ResponseReader(socketChannel)).start();
            String command = "";
            while (true) {
                command = scanner.nextLine();
                if("disconnect".equals(command)) {
                    break;
                }
                buffer.clear();
                buffer.put(command.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
            }
        } catch (IOException e) {
            System.err.println("There is a problem with the network communication! " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WishListClient client = new WishListClient("localhost",8888);
        client.start();
    }
}
