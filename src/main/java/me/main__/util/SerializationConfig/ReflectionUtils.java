package me.main__.util.SerializationConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {
    protected ReflectionUtils() {
        throw new UnsupportedOperationException();
    }

    public static final <T> T safelyInstantiate(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return safelyInstantiate(clazz, null);
    }

    public static final <T> T safelyInstantiate(Class<T> clazz, Object instantiator) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        boolean needsInstance = false;
        Constructor<T> ctor;
        try {
            ctor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            try {
                if (instantiator == null)
                    throw new NoSuchMethodException();
                ctor = clazz.getDeclaredConstructor(instantiator.getClass());
                needsInstance = true;
            } catch (NoSuchMethodException e1) {
                throw new InstantiationException("Couldn't instantiate " + clazz + "!");
            }
        }
        try {
            ctor.setAccessible(true);
            if (!needsInstance)
                return ctor.newInstance();
            else
                return ctor.newInstance(instantiator);
        } finally {
            ctor.setAccessible(false);
        }
    }
}
