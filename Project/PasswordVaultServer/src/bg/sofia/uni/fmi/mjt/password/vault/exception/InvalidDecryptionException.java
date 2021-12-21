package bg.sofia.uni.fmi.mjt.password.vault.exception;

public class InvalidDecryptionException extends Exception {
    public InvalidDecryptionException(String message) {
        super(message);
    }

    public InvalidDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
