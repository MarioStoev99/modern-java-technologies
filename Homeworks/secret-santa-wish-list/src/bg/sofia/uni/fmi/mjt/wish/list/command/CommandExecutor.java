package bg.sofia.uni.fmi.mjt.wish.list.command;

import bg.sofia.uni.fmi.mjt.wish.list.storage.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class CommandExecutor {

    private static final String INVALID_ARGUMENTS_COUNT = "[ Invalid arguments count. Check the template and try again! ]";
    private static final String ALPHANUMERIC_REGEX = "^[a-zA-Z0-9_.-]*$";

    private final Map<String, UserData> usersData;

    public CommandExecutor() {
        this(new HashMap<>());
    }

    public CommandExecutor(Map<String, UserData> usersData) {
        this.usersData = usersData;
    }

    public Response execute(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null!");
        }

        Response response;
        switch (command.command()) {
            case REGISTER -> response = register(command);
            case LOGIN -> response = login(command);
            case POST_WISH -> response = postWish(command);
            case DISCONNECT -> response = disconnect(command);
            case GET_WISH -> response = getWish(command);
            case LOGOUT -> response = logout(command);
            default -> response = new Response(CommandType.UNKNOWN, "Unknown command!");
        }

        return response;
    }

    private Response register(Command command) {
        if (command.argumentsCount() != 2) {
            return new Response(CommandType.REGISTER, INVALID_ARGUMENTS_COUNT);
        }

        String message;
        String[] arguments = command.message().split(" ");
        String username = arguments[0];
        String password = arguments[1];
        if (!username.matches(ALPHANUMERIC_REGEX)) {
            message = "[ Username " + username + " is invalid, select a valid one ]";
        } else if (usersData.containsKey(username)) {
            message = "[ Username " + username + " is already taken, select another one ]";
        } else {
            message = "[ Username " + username + " successfully registered ]";
            UserData data = new UserData(password);
            data.login();
            usersData.put(username, data);
        }

        return new Response(CommandType.REGISTER, message);
    }

    private Response login(Command command) {
        if (command.argumentsCount() != 2) {
            return new Response(CommandType.LOGIN, INVALID_ARGUMENTS_COUNT);
        }

        String message;
        String[] arguments = command.message().split(" ");
        String username = arguments[0];
        String password = arguments[1];
        if (!usersData.containsKey(username)) {
            message = "[ Student with nickname " + username + " is not registered ]";
        } else if (usersData.get(username).isLoggedIn()) {
            message = "[ User " + username + " has already logged in ]";
        } else {
            if (!usersData.get(username).verifyUserPassword(password)) {
                message = "[ Invalid password ]";
            } else {
                message = "[ User " + username + " successfully logged in ]";
                usersData.get(username).login();
            }
        }
        return new Response(CommandType.LOGIN, message);
    }

    private Response postWish(Command command) {
        if (command.argumentsCount() < 2) {
            return new Response(CommandType.POST_WISH, INVALID_ARGUMENTS_COUNT);
        }

        String message;
        String[] arguments = command.message().split(" ");
        String username = arguments[0];
        String present = arguments[1];
        if (!usersData.containsKey(username)) {
            message = "[ Student with nickname " + username + " is not registered ]";
        } else if (!usersData.containsKey(username)) {
            message = "[ The provided user is not registered! ]";
        } else if (!usersData.get(username).isLoggedIn()) {
            message = "[ You are not logged in ]";
        } else {
            message = addPresentsForUser(username, present);
        }
        return new Response(CommandType.POST_WISH, message);
    }

    private Response getWish(Command command) {
        String message;
        if (command.argumentsCount() != 0) {
            message = INVALID_ARGUMENTS_COUNT;
        } else if (!usersData.containsKey(command.username())) {
            message = "[ You are not registered in ]";
        } else if (!usersData.get(command.username()).isLoggedIn()) {
            message = "[ You are not logged in ]";
        } else {
            Map<String, Set<String>> presents = getAllPresents();
            if (presents.size() == 0) {
                message = "[ There are no presents in the wish list! ]";
            } else if (presents.size() == 1 && presents.containsKey(command.username())) {
                message = "[ There is only one present in the wish list and it is for you! ]";
            } else {
                String randomPerson = getRandomPerson(command.username());
                message = "[ " + randomPerson + ": " + usersData.get(randomPerson).getPresents() + " ]";
                UserData data = usersData.get(command.username());
                data.getPresents().clear();
            }
        }
        return new Response(CommandType.GET_WISH, message);
    }

    private Response disconnect(Command command) {
        String message;
        if (command.argumentsCount() != 0) {
            message = INVALID_ARGUMENTS_COUNT;
        } else {
            boolean register = usersData.containsKey(command.username());
            if (register && usersData.get(command.username()).isLoggedIn()) {
                usersData.get(command.username()).logout();
            }
            message = "[ Disconnected from server ]";
        }
        return new Response(CommandType.DISCONNECT, message);
    }

    private Response logout(Command command) {
        String message;
        if (command.argumentsCount() != 0) {
            message = INVALID_ARGUMENTS_COUNT;
        } else if (!usersData.get(command.username()).isLoggedIn()) {
            message = "[ You are not logged in ]";
        } else {
            message = "[ Successfully logged out ]";
            usersData.get(command.username()).logout();
        }
        return new Response(CommandType.LOGOUT, message);
    }

    private String addPresentsForUser(String username, String present) {
        String message;
        Set<String> presents = usersData.get(username).getPresents();
        if (presents.contains(present)) {
            message = "[ The same gift for student " + username + " was already submitted ]";
        } else {
            presents.add(present);
            message = "[ Gift " + present + " for student " + username + " submitted successfully ]";
        }
        return message;
    }

    private String getRandomPerson(String username) {
        String randomKey;
        do {
            Random generator = new Random();
            String[] keys = usersData.keySet().toArray(new String[0]);
            randomKey = keys[generator.nextInt(keys.length)];
        } while (randomKey.equals(username));
        return randomKey;
    }

    private Map<String, Set<String>> getAllPresents() {
        Map<String, Set<String>> userPresents = new HashMap<>();
        for (Map.Entry<String, UserData> entry : usersData.entrySet()) {
            if (!entry.getValue().getPresents().isEmpty()) {
                System.out.println("aaaaaa");
                userPresents.put(entry.getKey(), entry.getValue().getPresents());
            }
        }
        return userPresents;
    }
}
