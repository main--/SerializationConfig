package me.main__.util.SerializationConfig;

import java.util.HashMap;
import java.util.Map;

public class InstanceCache<T> {
    private final Map<Class<? extends T>, T> instances;

    public InstanceCache() {
        instances = new HashMap<Class<? extends T>, T>();
    }

    public void cacheInstance(T instance) {
        instances.put((Class<? extends T>) instance.getClass(), instance);
    }

    public <U extends T> U getInstance(Class<U> clazz) {
        return getInstance(clazz, null);
    }

    public <U extends T> U getInstance(Class<U> clazz, Object instantiator) {
        U u = null;
        try {
            u = (U) instances.get(clazz);
        } catch (Exception e) {
        }
        if (u == null) {
            // create a new one
            try {
                u = ReflectionUtils.safelyInstantiate(clazz, instantiator);
            } catch (ReflectiveOperationException e) {
                // failed? sorry, u stays null then.
            }
        }
        return u;
    }
}
