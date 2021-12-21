import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ResponseReader implements Runnable {

    private static final int BUFFER_SIZE = 512;

    private final SocketChannel socketChannel;

    public ResponseReader(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        String response;
        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        while (true) {
            buffer.clear();
            try {
                socketChannel.read(buffer);
            } catch (IOException e) {
                System.out.println("Disconnected!");
                break;
            }
            buffer.flip();
            byte[] byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);
            response = new String(byteArray, StandardCharsets.UTF_8);
            System.out.println(response);
        }
    }
}
