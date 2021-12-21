package bg.sofia.uni.fmi.mjt.chat.command;

import java.util.List;

public class CommandCreator {

    public static Command create(String clientInput, String origin) {
        List<String> tokens = List.of(clientInput.split(" ", 2));
        String message = checkFirstArgument(tokens);
        CommandType type = CommandType.fromString(tokens.get(0));
        Command command;
        switch (type) {
            case SEND -> {
                List<String> senderAndMessage = List.of(message.split(" ", 2));
                command = new Command(CommandType.fromString(tokens.get(0)), message, origin,senderAndMessage.size());
            }
            case SEND_ALL -> command = new Command(CommandType.fromString(tokens.get(0)), message, origin, tokens.size() - 1);
            case NICK -> command = new Command(CommandType.fromString(tokens.get(0)), "", message, tokens.size() - 1);
            case LIST_USERS, DISCONNECT -> command = new Command(CommandType.fromString(tokens.get(0)), "", origin, tokens.size() - 1);
            default -> command = new Command(CommandType.UNKNOWN, "", origin,0);
        }
        return command;
    }

    private static String checkFirstArgument(List<String> tokens) {
        if(tokens.size() != 2) {
            return "";
        }
        return tokens.get(1);
    }
}
