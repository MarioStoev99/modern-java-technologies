import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class PasswordVaultClient {

    private static final int BUFFER_SIZE = 512;

    private final String host;
    private final int port;

    public PasswordVaultClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open(); Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(host, port));
            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

            new Thread(new ResponseReader(socketChannel)).start();

            String command;
            while (true) {
                command = scanner.nextLine();
                if ("disconnect".equals(command)) {
                    break;
                }
                buffer.clear();
                buffer.put(command.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
            }
        } catch (IOException e) {
            throw new IllegalStateException("The client have not managed to connect to the server!", e);
        }
    }

}
