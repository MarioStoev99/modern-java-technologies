package bg.sofia.uni.fmi.mjt.password.vault.exception;

public class PasswordApiClientException extends Exception {

    public PasswordApiClientException(String message) {
        super(message);
    }

    public PasswordApiClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
