package bg.sofia.uni.fmi.mjt.chat.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ResponseThread extends Thread {

    private static final int BUFFER_SIZE = 512;

    private final SocketChannel socketChannel;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public ResponseThread(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        String response = null;
        while (!"Disconnected from server!".equals(response)) {
            buffer.clear();
            try {
                socketChannel.read(buffer);
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while reading: ", e);
            }
            buffer.flip();
            byte[] byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);
            response = new String(byteArray, StandardCharsets.UTF_8);
            System.out.println(response);
        }
    }
}
