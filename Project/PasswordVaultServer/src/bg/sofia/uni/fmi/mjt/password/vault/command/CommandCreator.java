package bg.sofia.uni.fmi.mjt.password.vault.command;

import java.util.List;

public class CommandCreator {
    public static Command create(String clientInput, String username) {
        List<String> tokens = List.of(clientInput.split(" ", 2));

        String arguments = "";

        boolean commandHasArguments = tokens.size() == 2;
        if (commandHasArguments) {
            arguments = tokens.get(1);
        }

        return new Command(CommandType.fromString(tokens.get(0)), arguments, username);
    }
}
