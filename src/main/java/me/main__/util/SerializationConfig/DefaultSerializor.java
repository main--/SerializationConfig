package me.main__.util.SerializationConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A default {@link Serializor}-implementation that tries to serialize an object to a string or a
 * {@code Map<String, Object>}, if the object is a {@link SerializationConfig}.
 * <p>
 * It fails silently.
 */
public final class DefaultSerializor<T> implements Serializor<T, Object> {
    /**
     * {@inheritDoc}
     */
    public Object serialize(Object object) {
        if (object instanceof SerializationConfig) {
            // this one is serializable itself
            return object;
        }
        return String.valueOf(object);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T deserialize(Object serialized, Class<T> anothertype) {
        try {
            if (String.class.isAssignableFrom(anothertype))
                return (T) serialized;

            if (SerializationConfig.class.isAssignableFrom(anothertype)) {
                // this one is serializable itself ==> serialized should be a string-object-map
                Map<String, Object> serMap = (Map<String, Object>) serialized;
                // now we need the deserialization-constructor
                Constructor<T> ctor = anothertype.getConstructor(Map.class);
                return ctor.newInstance(serMap);
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
            return null;
        }
    }

    /**
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
