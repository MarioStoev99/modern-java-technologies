package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.command.CommandType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ResponseReader implements Runnable {

    private static final int BUFFER_SIZE = 512;

    private SocketChannel socketChannel;
    private ByteBuffer buffer;

    public ResponseReader(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    @Override
    public void run() {
        String response = "";
        while (true) {
            if("[ Disconnected from server ]".equals(response)) {
                break;
            }
            buffer.clear();
            try {
                socketChannel.read(buffer);
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while reading!", e);
            }
            buffer.flip();

            byte[] byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);
            String serverMessage = new String(byteArray, StandardCharsets.UTF_8);

            System.out.println(serverMessage);
        }
    }
}
