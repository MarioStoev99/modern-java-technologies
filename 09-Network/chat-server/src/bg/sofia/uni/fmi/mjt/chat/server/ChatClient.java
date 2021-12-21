package bg.sofia.uni.fmi.mjt.chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ChatClient {

    private static final int BUFFER_SIZE = 512;

    private final InetSocketAddress address;

    public ChatClient(String host, int port) {
        this.address = new InetSocketAddress(host, port);
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(address);
            Scanner scanner = new Scanner(System.in);
            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

            Thread response = new ResponseThread(socketChannel);
            response.start();

            String command = null;
            while (!"disconnect".equals(command)) {
                command = scanner.nextLine();
                buffer.clear();
                buffer.put(command.getBytes());
                buffer.flip();
                try {
                    socketChannel.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred with I/O logic!", e);
        }
    }
}
