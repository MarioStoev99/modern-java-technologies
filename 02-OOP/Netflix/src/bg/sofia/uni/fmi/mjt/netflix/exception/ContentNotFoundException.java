package bg.sofia.uni.fmi.mjt.netflix.exception;

public class ContentNotFoundException extends RuntimeException{
    public ContentNotFoundException(String message) {
        super(message);
    }
}
