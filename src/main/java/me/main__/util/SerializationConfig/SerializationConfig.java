package me.main__.util.SerializationConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * An abstract class that can be extended to create a type that can easily be stored in the Bukkit config.
 * <p>
 * <b>IMPORTANT: Before custom types can be loaded/deserialized by Bukkit, they have to be registered
 * with {@link ConfigurationSerialization#registerClass(Class)}!
 */
public abstract class SerializationConfig implements ConfigurationSerializable {

    /**
     * This constructor does nothing (okay, it sets the defaults...), it's just here for
     * you to provide a super constructor that can be overridden and called.
     */
    public SerializationConfig() {
        setDefaults();
    }

    /**
     * This is the constructor used by Bukkit to deserialize the object.
     * Yep, this does the actual deserialization-work so make sure to have a constructor
     * that takes a {@code Map<String, Object>} and passes it to this super implementation.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SerializationConfig(Map<String, Object> values) {
        this();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Property.class)) {
                try {
                    // yay, this field is a property :D
                    // let's continue and try to serialize it
                    Property propertyInfo = f.getAnnotation(Property.class);
                    Class<Serializor<?, ?>> serializorClass = (Class<Serializor<?, ?>>) propertyInfo.value();
                    // try to create a new serializor
                    Constructor<Serializor<?, ?>> ctor = serializorClass.getConstructor();
                    Serializor serializor = ctor.newInstance();
                    // deserialize it and set the field
                    Object value = serializor.deserialize(values.get(f.getName()), f.getType());
                    if (value != null)
                        f.set(this, value);
                } catch (Exception e) {
                }
            }
            else
                f.setAccessible(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final Map<String, Object> serialize() {
        Field[] fields = this.getClass().getDeclaredFields();
        Map<String, Object> ret = new LinkedHashMap<String, Object>();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Property.class)) {
                try {
                    // yay, this field is a property :D
                    // let's continue and try to serialize it
                    Property propertyInfo = f.getAnnotation(Property.class);
                    Class<Serializor<?, ?>> serializorClass = (Class<Serializor<?, ?>>) propertyInfo.value();
                    // try to create a new serializor
                    Constructor<Serializor<?, ?>> ctor = serializorClass.getConstructor();
                    Serializor serializor = ctor.newInstance();
                    // serialize it and put it into the output-map
                    ret.put(f.getName(), serializor.serialize(f.get(this)));
                } catch (Exception e) {
                }
            }
            f.setAccessible(false);
        }

        return ret;
    }

    /**
     * This method sets properties in this object to their default-values.
     * <p>
     * <b>IMPORTANT: All properties have to be initialized HERE, never in/before the constructor!</b>
     */
    public abstract void setDefaults();
}
