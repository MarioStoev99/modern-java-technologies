package bg.sofia.uni.fmi.mjt.netflix.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
