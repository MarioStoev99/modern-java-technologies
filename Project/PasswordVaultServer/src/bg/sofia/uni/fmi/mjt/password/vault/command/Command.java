package bg.sofia.uni.fmi.mjt.password.vault.command;

public record Command(CommandType type,String arguments,String username) {
}
