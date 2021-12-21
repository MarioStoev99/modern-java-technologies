package bg.sofia.uni.fmi.mjt.wish.list.command;

public enum CommandType {

    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout"),
    POST_WISH("post-wish"),
    GET_WISH("get-wish"),
    DISCONNECT("disconnect"),
    UNKNOWN("unknown");

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
