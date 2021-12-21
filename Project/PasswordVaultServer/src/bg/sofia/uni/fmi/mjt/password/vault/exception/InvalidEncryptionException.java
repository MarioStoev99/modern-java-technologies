package bg.sofia.uni.fmi.mjt.password.vault.exception;

public class InvalidEncryptionException extends Exception {
    public InvalidEncryptionException(String message) {
        super(message);
    }

    public InvalidEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
