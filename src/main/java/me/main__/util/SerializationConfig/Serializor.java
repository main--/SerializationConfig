package me.main__.util.SerializationConfig;

import java.io.File;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Implemented by helper classes that are used to serialize objects.
 * <p>
 *
 * @param <T> The type of the object to be serialized.
 * @param <U> The type of the object in its serialized state.
 *            <p>
 *            {@code U} has to be a {@link ConfigurationSerializable}, a primitive-wrapper, a {@link String},
 *            a {@link File}, an {@link Enum} or an {@link Iterable}!
 */
public interface Serializor<T, U> {
    /**
     * Serializes the specified {@code T}.
     *
     * @param from The {@code T} to be serialized.
     * @return The serialized object.
     */
    U serialize(T from);

    /**
     * Deserializes a {@code U} to a {@code T}.
     *
     * @param serialized The {@code U} to be deserialized.
     * @param wanted The {@link Class} of the object that should be returned.
     * @return The deserialized object.
     * @throws IllegalPropertyValueException When the serialized value is invalid and cannot be deserialized.
     */
    T deserialize(U serialized, Class<T> wanted) throws IllegalPropertyValueException;
}
