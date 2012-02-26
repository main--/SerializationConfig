package me.main__.util.SerializationConfig;

/**
 * Thrown when a property was not found.
 */
public class NoSuchPropertyException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoSuchPropertyException() {
        super();
    }

    public NoSuchPropertyException(MissingAnnotationException cause) {
        super("Missing Property-annotation", cause);
    }

    public NoSuchPropertyException(NoSuchFieldException cause) {
        super(cause.getMessage(), cause);
    }

    public NoSuchPropertyException(Throwable cause) {
        super(cause);
    }
}
