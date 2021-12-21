package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.command.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class WishListServer {

    private static final int BUFFER_SIZE = 512;

    private final int port;
    private final ByteBuffer buffer;
    private final CommandExecutor commandExecutor;

    private Selector selector;
    private boolean serverIsWorking;

    public WishListServer(int port) {
        this.port = port;
        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        this.commandExecutor = new CommandExecutor();
    }

    public void start() {
        serverIsWorking = true;
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel);

            while (serverIsWorking) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        String clientMessage = readClientMessage(clientChannel);
                        if (clientMessage == null) {
                            continue;
                        }

                        processMessage(key, clientMessage);
                    } else if (key.isAcceptable()) {
                        accept(key);
                    }
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred with the server", e);
        }
    }

    public void stop() {
        serverIsWorking = false;
        if (selector.isOpen()) {
            try {
                selector.close();
            } catch (IOException e) {
                throw new IllegalStateException("An error occurred while closing selector!", e);
            }
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel serverSocketChannel) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String readClientMessage(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void sendResponse(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = serverSocketChannel.accept();

        accept.configureBlocking(false);
        SelectionKey selectionKey = accept.register(selector, SelectionKey.OP_READ);
        selectionKey.attach("Guest");
    }

    private void processMessage(SelectionKey key, String clientMessage) throws IOException {
        Command clientCommand = CommandCreator.create(clientMessage, (String) key.attachment());
        Response response = commandExecutor.execute(clientCommand);
        addNickname(response, key);
        sendResponse((SocketChannel) key.channel(), response.message());
    }

    private void addNickname(Response response, SelectionKey key) {
        if (response.command() == CommandType.REGISTER) {
            String[] responseParts = response.message().split(" ");
            String commandResult = responseParts[4];
            String username = responseParts[3];
            if ("successfully".equals(commandResult)) {
                key.attach(username);
            }
        }
    }

    public static void main(String[] args) {
        WishListServer server = new WishListServer(8888);
        server.start();
    }

}
