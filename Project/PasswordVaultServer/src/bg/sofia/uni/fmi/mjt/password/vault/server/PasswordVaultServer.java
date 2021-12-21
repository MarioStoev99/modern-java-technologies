package bg.sofia.uni.fmi.mjt.password.vault.server;

import bg.sofia.uni.fmi.mjt.password.vault.command.Command;
import bg.sofia.uni.fmi.mjt.password.vault.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.password.vault.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.password.vault.command.CommandType;
import bg.sofia.uni.fmi.mjt.password.vault.command.Response;
import bg.sofia.uni.fmi.mjt.password.vault.exception.InvalidDecryptionException;
import bg.sofia.uni.fmi.mjt.password.vault.exception.InvalidEncryptionException;
import bg.sofia.uni.fmi.mjt.password.vault.exception.PasswordApiClientException;
import bg.sofia.uni.fmi.mjt.password.vault.rest.SecurePasswordChecker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class PasswordVaultServer {

    private static final int BUFFER_SIZE = 512;

    private final int port;

    private final ByteBuffer buffer;
    private final CommandExecutor commandExecutor;
    private boolean serverIsWorking;
    private Selector selector;


    public PasswordVaultServer(int port) {
        this.port = port;
        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        this.commandExecutor = new CommandExecutor(new SecurePasswordChecker(HttpClient.newHttpClient()));
    }

    public void start() throws IOException {
        serverIsWorking = true;
        SocketChannel clientChannel = null;
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
                    clientChannel = (SocketChannel) key.channel();
                    if (key.isReadable()) {
                        String clientMessage = readClientMessage(clientChannel);
                        if (clientMessage == null) {
                            continue;
                        }

                        processMessage(key, clientMessage);

                    } else if (key.isAcceptable()) {
                        accept(selector, key);
                    }
                    iterator.remove();
                }
            }
        } catch (PasswordApiClientException e) {
            writeMessageToChannel(clientChannel, "There is a problem with this functionality,please try again later!");
        } catch (InvalidEncryptionException | InvalidDecryptionException e) {
            writeMessageToChannel(clientChannel, "There is currently a problem with this service. Please try again later!");
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
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);

        return new String(byteArray, StandardCharsets.UTF_8);
    }

    private void processMessage(SelectionKey clientKey, String clientMessage) throws PasswordApiClientException, IOException, InvalidEncryptionException, InvalidDecryptionException {
        Command clientCommand = CommandCreator.create(clientMessage, (String) clientKey.attachment());
        Response response = commandExecutor.execute(clientCommand);
        loginUsername(response, clientKey);
        writeMessageToChannel((SocketChannel) clientKey.channel(), response.message());
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = serverSocketChannel.accept();
        accept.configureBlocking(false);
        SelectionKey clientKey = accept.register(selector, SelectionKey.OP_READ);
        clientKey.attach("Guest");
    }

    private void writeMessageToChannel(SocketChannel clientChannel, String message) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void loginUsername(Response response, SelectionKey clientKey) {
        if (response.type() == CommandType.LOGIN) {
            String[] messageParts = response.message().split(" ");
            int expectedMessageLength = 5;
            if (messageParts.length == expectedMessageLength && "successfully".equals(messageParts[2])) {
                clientKey.attach(messageParts[1]);
            }
        }
    }

    public static void main(String[] args) {
        PasswordVaultServer server = new PasswordVaultServer(8888);
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("An error occurred when try to send a message to the client channel!");
            e.printStackTrace();
        }
    }
}