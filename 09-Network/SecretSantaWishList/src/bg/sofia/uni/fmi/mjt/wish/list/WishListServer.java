package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishListServer {

    private static final int BUFFER_SIZE = 1024;
    private InetSocketAddress listenAddress;
    private Map<String,Set<String>> datas;
    private ByteBuffer byteBuffer;

    public WishListServer(int port) {
        listenAddress = new InetSocketAddress(port);
        datas = new HashMap<>();
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    private void pushData(SocketChannel socketChannel,String message) throws IOException {
        byteBuffer.clear();
        byteBuffer.put(message.getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }

    private String rebuildPresent(String[] words) {
        String present = new String();
        for(int i = 2;i < words.length;++i) {
            present += words[i];
            if(i != words.length - 1) {
                present += " ";
            }
        }
        return present;
    }

    private Runnable getTask(SelectionKey selectionKey) {
       return () -> {
            try {
                read(selectionKey);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private void read(SelectionKey selectionKey) throws IOException, IllegalArgumentException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        byteBuffer.clear();
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        byte[] byteArray = new byte[byteBuffer.remaining()];
        byteBuffer.get(byteArray);
        String clientMessage = new String(byteArray, StandardCharsets.UTF_8);

        if(clientMessage.equals("disconnect")) {
            pushData(socketChannel,"[ Disconnected from server ]");
            socketChannel.close();
        } else if(clientMessage.equals("get-wish")) {
            if(datas.size() == 0) {
                pushData(socketChannel,"[ There are no students present in the wish list ]");
            } else {
                List<String> keys = new ArrayList<>(datas.keySet());
                Random randomKey = new Random();
                String key = keys.get(randomKey.nextInt(keys.size()));
                Set<String> presents = datas.remove(key);
                String result = "[ " + key + ": "+ presents + " ]";
                pushData(socketChannel,result);
            }
        } else {
            String[] words = clientMessage.split(" ");
            if(words.length < 3) {
                throw new IllegalArgumentException("Invalid message!");
            }
            String command = words[0];
            String name = words[1];
            String present = rebuildPresent(words);
            if(!command.equals("post-wish")) {
                pushData(socketChannel,"[ Unknown command ]");
            } else {
                if(datas.containsKey(name)) {
                    boolean presentExist = datas.get(name).contains(present);
                    if(presentExist) {
                        pushData(socketChannel,"The same gift for student " + name + " was already submitted");
                    } else {
                        Set<String> presents = datas.get(name);
                        presents.add(present);
                        datas.put(name,presents);
                        pushData(socketChannel,"[ Gift " + present + " for student " + name + " submitted successfully ]");
                    }
                } else {
                    Set<String> presents = new HashSet<>();
                    presents.add(present);
                    datas.put(name,presents);
                    pushData(socketChannel,"[ Gift "+ present + " for student " + name + " submitted successfully ]");
                }
            }
        }
    }
    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(listenAddress);
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                System.out.println("I'm waiting for new connections :)");
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isReadable()) {
                        /*
                        Runnable task = getTask(selectionKey);
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(task);
                        */
                        read(selectionKey);
                    } else if(selectionKey.isAcceptable()) {
                        ServerSocketChannel socketChannel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel accept = socketChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector,SelectionKey.OP_READ);
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        WishListServer wishListServer = new WishListServer(7777);
        wishListServer.start();
    }
}
