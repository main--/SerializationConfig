package me.main__.util.SerializationConfig;

/**
 * Thrown by the {@link Serializor} when the deserialization fails.
 */
public class IllegalPropertyValueException extends Exception {
    private static final long serialVersionUID = 1L;

    public IllegalPropertyValueException() {
        super();
    }

    public IllegalPropertyValueException(String message) {
        super(message);
    }

    public IllegalPropertyValueException(Throwable cause) {
        super(cause);
    }

    public IllegalPropertyValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
