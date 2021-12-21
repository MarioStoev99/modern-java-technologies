package bg.sofia.uni.fmi.mjt.password.vault.command;

public enum CommandType {

    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout"),
    GENERATE_PASSWORD("generate-password"),
    RETRIEVE_CREDENTIALS("retrieve-credentials"),
    REMOVE_PASSWORD("remove-password"),
    ADD_PASSWORD("add-password"),
    DISCONNECT("disconnect"),
    UNKNOWN("unknown-command");

    private final String name;

    CommandType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CommandType fromString(String name) {
        for (CommandType type : CommandType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
