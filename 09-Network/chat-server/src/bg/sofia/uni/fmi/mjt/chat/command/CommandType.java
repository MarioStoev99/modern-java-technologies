package bg.sofia.uni.fmi.mjt.chat.command;

public enum CommandType {
    NICK("nick"),
    SEND("send"),
    SEND_ALL("send-all"),
    LIST_USERS("list-users"),
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
        CommandType[] types = CommandType.values();
        for (CommandType type : types) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
