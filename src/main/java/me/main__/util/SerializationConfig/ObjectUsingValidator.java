package me.main__.util.SerializationConfig;

/**
 * Implemented by helper classes that are used to validate property-changes.
 * <p>
 * Extend this instead of implementing {@link Validator} if
 * you need a reference to the object the property belongs to.
 *
 * @param <T> The type of the property whose changes should be validated.
 * @param <U> The type of the object the property belongs to.
 * @see Validator
 */
public abstract class ObjectUsingValidator<T, U> implements Validator<T> {
    /**
     * {@inheritDoc}
     * @deprecated Removed.
     * @throws UnsupportedOperationException <b><u>Always!</b></u>
     */
    @Override
    @Deprecated
    public final T validateChange(String property, T newValue, T oldValue)
            throws ChangeDeniedException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Called when a property-change should be validated.
     *
     * @param property The name of the property.
     * @param newValue The new value.
     * @param oldValue The old value.
     * @param object The object the property belongs to.
     * @return The value the property should be set to.
     * @throws ChangeDeniedException When the property-change was denied.
     */
    public abstract T validateChange(String property, T newValue, T oldValue, U object) throws ChangeDeniedException;
}
