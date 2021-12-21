package bg.sofia.uni.fmi.mjt.password.vault.command;

import bg.sofia.uni.fmi.mjt.password.vault.algorithm.Aes;
import bg.sofia.uni.fmi.mjt.password.vault.algorithm.PasswordGenerator;
import bg.sofia.uni.fmi.mjt.password.vault.algorithm.Sha256;
import bg.sofia.uni.fmi.mjt.password.vault.exception.InvalidDecryptionException;
import bg.sofia.uni.fmi.mjt.password.vault.exception.InvalidEncryptionException;
import bg.sofia.uni.fmi.mjt.password.vault.exception.PasswordApiClientException;
import bg.sofia.uni.fmi.mjt.password.vault.rest.SecurePasswordChecker;
import bg.sofia.uni.fmi.mjt.password.vault.user.data.UserData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CommandExecutor {

    public static String PROPERTY_KEY = "directory";

    private static final String INVALID_ARGS_COUNT_MESSAGE = "Invalid arguments!";
    private final Map<String, UserData> users;
    private final SecurePasswordChecker securePasswordChecker;

    CommandExecutor(SecurePasswordChecker securePasswordChecker, Map<String, UserData> users) {
        this.securePasswordChecker = securePasswordChecker;
        this.users = users;
    }

    public CommandExecutor(SecurePasswordChecker securePasswordChecker) {
        this(securePasswordChecker, new HashMap<>());
        loadUsers();
    }

    static {
        getDirectory();
    }

    public Response execute(Command command) throws PasswordApiClientException, InvalidEncryptionException, InvalidDecryptionException {
        Response response;
        switch (command.type()) {
            case REGISTER -> response = register(command);
            case LOGIN -> response = login(command);
            case LOGOUT -> response = logout(command);
            case DISCONNECT -> response = disconnect(command);
            case GENERATE_PASSWORD -> response = generatePassword(command);
            case RETRIEVE_CREDENTIALS -> response = retrieveCredentials(command);
            case REMOVE_PASSWORD -> response = removePassword(command);
            case ADD_PASSWORD -> response = addPassword(command);
            default -> response = new Response(CommandType.UNKNOWN, "Unknown command!");
        }
        return response;
    }

    private Response register(Command command) {
        String[] arguments = splitArguments(command);
        if (argumentsAreNotValid(arguments.length, 3)) {
            return new Response(CommandType.REGISTER, INVALID_ARGS_COUNT_MESSAGE);
        }

        String message;
        String username = arguments[0];
        String password = arguments[1];
        String repeatedPassword = arguments[2];

        if (users.containsKey(username)) {
            message = "Username " + username + " is already taken, select another one!";
        } else if (!password.equals(repeatedPassword)) {
            message = "Both passwords are not equals!";
        } else {
            UserData userData = new UserData(username);
            userData.addCredentials(Sha256.getSha256(password));
            users.put(username, userData);
            message = "Username " + username + " successfully registered";
        }

        return new Response(CommandType.REGISTER, message);
    }

    private Response login(Command command) {
        String[] arguments = splitArguments(command);
        if (argumentsAreNotValid(arguments.length, 2)) {
            return new Response(CommandType.LOGIN, INVALID_ARGS_COUNT_MESSAGE);
        }

        String username = arguments[0];
        String password = arguments[1];
        UserData userData = users.get(username);
        String message;
        if (userData == null) {
            message = "User with nickname " + username + " is not registered!";
        } else if (userData.isLoggedIn()) {
            message = "Username " + username + " has already logged in!";
        } else {
            String hashedPassword = Sha256.getSha256(password);

            if (!userData.verifyUserPassword(hashedPassword)) {
                message = "Invalid password!";
            } else {
                userData.login();
                message = "User " + username + " successfully logged in!";
            }
        }
        return new Response(CommandType.LOGIN, message);
    }

    private Response logout(Command command) {
        String message;
        int actualArguments = command.arguments().isEmpty() ? 0 : command.arguments().split(" ").length;
        String commandUsername = command.username();
        UserData userData = users.get(commandUsername);
        if (argumentsAreNotValid(actualArguments, 0)) {
            message = INVALID_ARGS_COUNT_MESSAGE;
        } else if (!users.containsKey(commandUsername)) {
            message = "You are not register!";
        } else if (!userData.isLoggedIn()) {
            message = "You are not logged in or the session has already expired!"; // нов месидж
        } else {
            userData.logout();
            message = "Successfully logged out!";
        }

        return new Response(CommandType.LOGOUT, message);
    }

    private Response disconnect(Command command) {
        String message;
        int actualArguments = command.arguments().isEmpty() ? 0 : command.arguments().split(" ").length;

        if (argumentsAreNotValid(actualArguments, 0)) {
            message = INVALID_ARGS_COUNT_MESSAGE;
        } else {
            String commandUsername = command.username();
            UserData userData = users.get(commandUsername);

            boolean registered = users.containsKey(commandUsername);
            if (registered && userData.isLoggedIn()) {
                userData.logout();
            }
            message = "Disconnected from server!";
        }
        return new Response(CommandType.DISCONNECT, message);
    }

    private Response generatePassword(Command command) throws InvalidEncryptionException {
        String[] arguments = splitArguments(command);
        String validityResponseMessage = getValidityResponse(command, arguments, 2);
        if (validityResponseMessage != null) {
            return new Response(CommandType.GENERATE_PASSWORD, validityResponseMessage);
        }
        String message;
        String website = arguments[0];
        String username = arguments[1];
        UserData userData = users.get(username);
        if (userData.getWebsitePassword(website) != null) {
            message = "The provided website is existing and has a password!";
        } else {
            String password = PasswordGenerator.generate();
            String encryptedPassword = Aes.encrypt(password, userData.getUserPassword());
            userData.addWebsitePassword(encryptedPassword, website);
            message = password;
        }
        return new Response(CommandType.GENERATE_PASSWORD, message);
    }

    private Response retrieveCredentials(Command command) throws InvalidDecryptionException {
        String[] arguments = splitArguments(command);
        String validityResponseMessage = getValidityResponse(command, arguments, 2);
        if (validityResponseMessage != null) {
            return new Response(CommandType.RETRIEVE_CREDENTIALS, validityResponseMessage);
        }

        String message;
        String website = arguments[0];
        String username = arguments[1];
        UserData userData = users.get(username);
        String websitePassword = userData.getWebsitePassword(website);
        if (websitePassword == null) {
            message = "The provided website does not exist!";
        } else {
            message = Aes.decrypt(websitePassword, userData.getUserPassword());
        }

        return new Response(CommandType.RETRIEVE_CREDENTIALS, message);
    }

    private Response removePassword(Command command) {
        String[] arguments = splitArguments(command);
        String validityResponseMessage = getValidityResponse(command, arguments, 2);
        if (validityResponseMessage != null) {
            return new Response(CommandType.REMOVE_PASSWORD, validityResponseMessage);
        }

        String message;
        String website = arguments[0];
        String username = arguments[1];
        UserData userData = users.get(username);
        String websitePassword = userData.getWebsitePassword(website);
        if (websitePassword == null) {
            message = "The provided website does not exist!";
        } else {
            userData.removeWebsitePassword(website);
            message = "Password was successfully removed!";
        }

        return new Response(CommandType.REMOVE_PASSWORD, message);
    }

    private Response addPassword(Command command) throws PasswordApiClientException, InvalidEncryptionException {
        String[] arguments = splitArguments(command);
        String validityResponseMessage = getValidityResponse(command, arguments, 3);
        if (validityResponseMessage != null) {
            return new Response(CommandType.ADD_PASSWORD, validityResponseMessage);
        }

        String message;
        String website = arguments[0];
        String username = arguments[1];
        String password = arguments[2];
        UserData userData = users.get(username);
        String websitePassword = userData.getWebsitePassword(website);
        if (websitePassword != null) {
            message = "The provided website is existing and has a password!";
        } else if (!securePasswordChecker.isSecure(password)) {
            message = "The provided password is compromised!";
        } else {
            String encryptedPassword = Aes.encrypt(password, userData.getUserPassword());
            userData.addWebsitePassword(encryptedPassword, website);
            message = "The provided password is being added for the given website!";
        }

        return new Response(CommandType.ADD_PASSWORD, message);
    }

    private String getValidityResponse(Command command, String[] arguments, int expectedArguments) {
        if (argumentsAreNotValid(arguments.length, expectedArguments)) {
            return INVALID_ARGS_COUNT_MESSAGE;
        }

        String message = null;
        String username = arguments[1];
        UserData userData = users.get(username);
        if (userData == null) {
            message = "User with nickname " + username + " is not registered!";
        } else if (!userData.isLoggedIn()) {
            message = "You are not logged in!";
        } else if (!username.equals(command.username())) {
            message = "This is not your account!";
        }
        return message;
    }

    private void loadUsers() {
        try (var reader = new BufferedReader(new FileReader(System.getProperty(PROPERTY_KEY) + File.separator + "serverPasswords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(" ");
                String username = credentials[0];
                users.put(username, new UserData(username));
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred when try to open user's passwords!", e);
        }
    }

    private static void getDirectory() {
        try (BufferedReader reader = new BufferedReader(new FileReader("resource" + File.separator + "directory.txt"))) {
            String line = reader.readLine();
            System.setProperty(PROPERTY_KEY, line);
        } catch (IOException e) {
            System.err.println("An error occurred when try opening file");
            e.printStackTrace();
        }
    }

    private boolean argumentsAreNotValid(int currentCommandArguments, int allowedArgumentsNumber) {
        return currentCommandArguments != allowedArgumentsNumber;
    }

    private String[] splitArguments(Command command) {
        return command.arguments().split(" ");
    }

}
