package bg.sofia.uni.fmi.mjt.warehouse.exception;

public class CapacityExceededException extends Exception {
    public CapacityExceededException(String message) {
        super(message);
    }
}
