package bg.sofia.uni.fmi.mjt.chat.command;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CommandExecutor {

    private static final String INVALID_ARGS_COUNT_MESSAGE = "Invalid arguments count!";
    private static final String ORIGIN_NOT_REGISTERED = "The author of the command is not registered with nickname!";
    private static final String RECIPIENT_NOT_REGISTERED = "The recipient is not registered with nickname!";

    private final Set<String> userStorage;

    public CommandExecutor() {
        this.userStorage = new HashSet<>();
    }

    public Response execute(Command command) {
        Response response;
        switch (command.type()) {
            case NICK -> response = nick(command);
            case SEND -> response = send(command);
            case SEND_ALL -> response = sendAll(command);
            case LIST_USERS -> response = listUsers(command);
            case DISCONNECT -> response = disconnect(command);
            default -> response = new Response(CommandType.UNKNOWN, "Unknown command!", command.origin());
        }
        return response;
    }

    private Response nick(Command command) {
        String message;
        int numberOfArguments = 1;
        if (command.argumentsCount() != numberOfArguments) {
            message = INVALID_ARGS_COUNT_MESSAGE;
        } else if (userStorage.contains(command.origin())) {
            message = "The provided nickname has been already added!";
        } else {
            userStorage.add(command.origin());
            message = "User " + command.origin() + " was added successfully!";
        }

        return new Response(CommandType.NICK, message, command.origin());
    }

    private Response send(Command command) {
        String[] dividedRecipientAndMessage = command.message().split(" ", 2);
        String recipient = dividedRecipientAndMessage[0];
        String message;
        int numberOfArguments = 2;
        if (command.argumentsCount() != numberOfArguments) {
            message = INVALID_ARGS_COUNT_MESSAGE;
            recipient = command.origin();
        } else if (!userStorage.contains(command.origin())) {
            message = ORIGIN_NOT_REGISTERED;
            recipient = command.origin();
        } else if (!userStorage.contains(recipient)) {
            message = RECIPIENT_NOT_REGISTERED;
            recipient = command.origin();
        } else {
            message = "[" + LocalDateTime.now() + "] " + command.origin() + ": " + dividedRecipientAndMessage[1];
            recipient = dividedRecipientAndMessage[0];
        }

        return new Response(CommandType.SEND, message, recipient);
    }

    private Response sendAll(Command command) {
        String message;
        String recipient;
        int numberOfArguments = 1;
        if (command.argumentsCount() != numberOfArguments) {
            message = INVALID_ARGS_COUNT_MESSAGE;
            recipient = command.origin();
        } else if (!userStorage.contains(command.origin())) {
            message = ORIGIN_NOT_REGISTERED;
            recipient = command.origin();
        } else {
            message = "[" + LocalDateTime.now() + "] " + command.origin() + ": " + command.message();
            recipient = "send-all";
        }
        return new Response(CommandType.SEND_ALL, message, recipient);
    }

    private Response listUsers(Command command) {
        String message;
        int numberOfArguments = 0;
        if (command.argumentsCount() != numberOfArguments) {
            message = INVALID_ARGS_COUNT_MESSAGE;
        } else if (!userStorage.contains(command.origin())) {
            message = ORIGIN_NOT_REGISTERED;
        } else {
            message = userStorage.toString();
        }
        return new Response(CommandType.LIST_USERS, message, command.origin());
    }

    private Response disconnect(Command command) {
        String message;
        int numberOfArguments = 0;
        if (command.argumentsCount() != numberOfArguments) {
            message = INVALID_ARGS_COUNT_MESSAGE;
        } else if (!userStorage.contains(command.origin())) {
            message = ORIGIN_NOT_REGISTERED;
        } else {
            message = "Disconnected from server!";
        }
        userStorage.remove(command.origin());
        return new Response(CommandType.DISCONNECT, message, command.origin());
    }

}
