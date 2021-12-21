package bg.sofia.uni.fmi.mjt.chat.command;

public record Command(CommandType type, String message, String origin,int argumentsCount) {
}
