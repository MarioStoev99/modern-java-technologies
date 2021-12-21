package bg.sofia.uni.fmi.mjt.chat.server;

public class Main1 {
    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8888);
        client.start();
    }
}
