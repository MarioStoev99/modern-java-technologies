package bg.sofia.uni.fmi.mjt.chat.server;

import bg.sofia.uni.fmi.mjt.chat.command.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatServer {

    private static final int BUFFER_SIZE = 512;

    private final CommandExecutor commandExecutor;
    private final ByteBuffer buffer;
    private final Map<String, SocketChannel> keyStorage;
    private final int port;

    private boolean serverIsWorking;
    private Selector selector;

    public ChatServer(int port) {
        this.port = port;
        this.keyStorage = new HashMap<>();
        this.commandExecutor = new CommandExecutor();
        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public void start() {
        serverIsWorking = true;
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            InetSocketAddress socketAddress = new InetSocketAddress(port);
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, socketAddress);

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
                        String clientMessage = readClientMessage((SocketChannel) key.channel());
                        if (clientMessage == null) {
                            continue;
                        }

                        Command clientCommand = CommandCreator.create(clientMessage, (String) key.attachment());
                        Response response = commandExecutor.execute(clientCommand);
                        sendResponse(response, key);
                    } else if (key.isAcceptable()) {
                        accept(selector, key);
                    }

                    iterator.remove();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred with the server: ", e);
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

    private void configureServerSocketChannel(ServerSocketChannel serverSocketChannel, InetSocketAddress socketAddress) throws IOException {
        serverSocketChannel.bind(socketAddress);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void accept(Selector selector, SelectionKey selectionKey) throws IOException {
        ServerSocketChannel socketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel accept = socketChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private String readClientMessage(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);

        return new String(byteArray, StandardCharsets.UTF_8);
    }

    private void sendResponse(Response response, SelectionKey currentSelectionKey) throws IOException {
        switch (response.type()) {
            case NICK -> {
                String nickname = response.recipient();
                currentSelectionKey.attach(nickname);
                keyStorage.put(nickname, (SocketChannel) currentSelectionKey.channel());
                writeMessageToChannel((SocketChannel) currentSelectionKey.channel(), response.message());
            }
            case SEND -> sendResponseToUser(response,currentSelectionKey);
            case SEND_ALL -> sendResponseToAllUsers(response, currentSelectionKey);
            default -> writeMessageToChannel((SocketChannel) currentSelectionKey.channel(),response.message());
        }
    }

    private void sendResponseToUser(Response response, SelectionKey currentSelectionKey) throws IOException {
        SocketChannel clientChannel;
        if (keyStorage.containsKey(currentSelectionKey.attachment())) {
            clientChannel = keyStorage.get(response.recipient());
        } else {
            clientChannel = (SocketChannel) currentSelectionKey.channel();
        }
        writeMessageToChannel(clientChannel, response.message());
    }

    private void sendResponseToAllUsers(Response response, SelectionKey currentSelectionKey) throws IOException {
        if (!keyStorage.containsKey(currentSelectionKey.attachment())) {
            sendResponseToUser(response, currentSelectionKey);
            return;
        }
        SocketChannel currentSocketChannel = (SocketChannel) currentSelectionKey.channel();
        for (Map.Entry<String, SocketChannel> channel : keyStorage.entrySet()) {
            if (currentSocketChannel.equals(channel.getValue())) {
                continue;
            }
            SocketChannel clientChannel = channel.getValue();
            writeMessageToChannel(clientChannel, response.message());
        }
    }

    private void writeMessageToChannel(SocketChannel channel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        channel.write(buffer);
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(8888);
        server.start();
    }
}
