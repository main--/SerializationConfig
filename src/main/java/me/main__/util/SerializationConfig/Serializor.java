package me.main__.util.SerializationConfig;

import java.io.File;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * A helper class used to serialize objects.
 * <p>
 *
 * @param <T> The type of the object to be serialized.
 * @param <U> The type of the object in its serialized state.
 *            <p>
 *            U has to be a {@link ConfigurationSerializable}, a primitive-wrapper, a {@link String},
 *            a {@link File}, an {@link Enum} or an {@link Iterable}!
 */
public interface Serializor<T, U> {
    /**
     * Serializes the specified {@link T}.
     *
     * @param from The {@link T} to be serialized.
     * @return The serialized object.
     */
    U serialize(T from);

    /**
     * Deserializes a {@link U} to a {@link T}.
     *
     * @param serialized The {@link U} to be deserialized.
     * @param wanted The {@link Class} of the object that should be returned.
     * @return The deserialized object.
     */
    T deserialize(U serialized, Class<T> wanted);
}
