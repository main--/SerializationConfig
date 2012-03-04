package me.main__.util.SerializationConfig;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * A default {@link Serializor}-implementation that tries to serialize an object to a string or a
 * {@code Map<String, Object>}, if the object is a {@link SerializationConfig}.
 * <p>
 * It fails silently.
 * @param <T> Generic type argument that does some magic. You usually won't touch this class, so just ignore it.
 */
final class DefaultSerializor<T> implements Serializor<T, Object> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Object serialize(Object object) {
        if ((object instanceof ConfigurationSerializable) || (object instanceof Iterable)) {
            // The YAML-Parser/Bukkit will handle it
            return object;
        }
        return String.valueOf(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(Object serialized, Class<T> anothertype) throws IllegalPropertyValueException {
        try {
            if (String.class.isAssignableFrom(anothertype))
                return (T) serialized;

            if (ConfigurationSerializable.class.isAssignableFrom(anothertype)) {
                // has bukkit already deserialized it?
                if (serialized instanceof ConfigurationSerializable)
                    return (T) serialized;
                else
                    return (T) ConfigurationSerialization.deserializeObject((Map<String, Object>) serialized);
            } else if (Iterable.class.isAssignableFrom(anothertype) && (serialized instanceof Iterable)) {
                return (T) serialized;
            }

            Class<?> type;
            if (primitiveToWrapperMap.containsKey(anothertype))
                type = primitiveToWrapperMap.get(anothertype);
            else
                type = anothertype;

            // try valueOf
            Method valueOf = type.getMethod("valueOf", String.class);
            return (T) valueOf.invoke(null, serialized);
        } catch (Exception e) {
            throw new IllegalPropertyValueException(e);
        }
    }

    /*
     * Somebody might want this one... for whatever reasons.
     */
    public static final Map<Class<?>, Class<?>> getPrimitiveToWrapperMap() {
        return Collections.unmodifiableMap(primitiveToWrapperMap);
    }

    private static final Map<Class<?>, Class<?>> primitiveToWrapperMap = new HashMap<Class<?>, Class<?>>();
    static {
        primitiveToWrapperMap.put(int.class, Integer.class);
        primitiveToWrapperMap.put(boolean.class, Boolean.class);
        primitiveToWrapperMap.put(long.class, Long.class);
        primitiveToWrapperMap.put(double.class, Double.class);
        primitiveToWrapperMap.put(float.class, Float.class);
        primitiveToWrapperMap.put(byte.class, Byte.class);
        primitiveToWrapperMap.put(short.class, Short.class);
    }
}
