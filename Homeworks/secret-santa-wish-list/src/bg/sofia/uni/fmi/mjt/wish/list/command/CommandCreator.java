package bg.sofia.uni.fmi.mjt.wish.list.command;

import java.util.List;

public class CommandCreator {
    public static Command create(String clientInput, String username) {
        List<String> tokens = List.of(clientInput.split(" ", 2));
        String arguments = "";

        boolean commandHasArguments = tokens.size() == 2;
        if (commandHasArguments) {
            arguments = tokens.get(1);
        }
        int actualArgumentsLength = arguments.split(" ").length;
        int argumentsCount = arguments.isEmpty() ? 0 : actualArgumentsLength;

        return new Command(CommandType.fromString(tokens.get(0)), arguments, username, argumentsCount);
    }
}
