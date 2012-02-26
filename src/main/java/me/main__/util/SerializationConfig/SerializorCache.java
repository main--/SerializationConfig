package me.main__.util.SerializationConfig;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class SerializorCache {
    private static final Map<Class<? extends Serializor<?, ?>>, Serializor<?, ?>> serializorCache
            = new HashMap<Class<? extends Serializor<?, ?>>, Serializor<?, ?>>();

    public static void cacheSerializor(Serializor<?, ?> serializor) {
        serializorCache.put((Class<? extends Serializor<?, ?>>) serializor.getClass(), serializor);
    }

    public static Serializor<?, ?> getSerializor(Class<? extends Serializor<?, ?>> clazz) {
        Serializor<?, ?> serializor = serializorCache.get(clazz);
        if (serializor == null) {
            // create a new one
            try {
                Constructor<? extends Serializor<?, ?>> ctor = clazz.getConstructor();
                serializor = ctor.newInstance();
                cacheSerializor(serializor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return serializor;
    }
}
