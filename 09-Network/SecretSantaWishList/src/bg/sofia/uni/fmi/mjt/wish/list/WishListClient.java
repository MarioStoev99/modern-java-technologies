package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class WishListClient {

    private InetSocketAddress inetSocketAddress;
    private ByteBuffer buffer;

    public WishListClient() {
        inetSocketAddress = new InetSocketAddress("localhost",7777);
        buffer = ByteBuffer.allocate(512);
    }

    public void start() {
        try(SocketChannel socketChannel = SocketChannel.open();
            Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(inetSocketAddress);
            while(true) {
                String command = scanner.nextLine();
                buffer.clear();
                buffer.put(command.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();
                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String message = new String(byteArray, "UTF-8");
                System.out.println(message);
                if(command.equals("disconnect")) {
                    break;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WishListClient wishListClient = new WishListClient();
        wishListClient.start();
    }
}
