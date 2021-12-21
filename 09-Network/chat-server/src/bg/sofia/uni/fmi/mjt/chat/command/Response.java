package bg.sofia.uni.fmi.mjt.chat.command;

public record Response(CommandType type, String message, String recipient) {
}
