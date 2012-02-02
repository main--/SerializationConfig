package me.main__.util.SerializationConfig;

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
     * Registers the specified Class with Bukkit.
     * @param clazz The class.
     */
    public static void registerAll(Class<? extends SerializationConfig> clazz) {
        ConfigurationSerialization.registerClass(clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Property.class)) {
                Class<?> fieldclazz = f.getType();
                if (ConfigurationSerializable.class.isAssignableFrom(fieldclazz)) {
                    Class<? extends ConfigurationSerializable> subclass = fieldclazz.asSubclass(ConfigurationSerializable.class);
                    ConfigurationSerialization.registerClass(subclass);
                }
            }
            f.setAccessible(false);
        }
    }

    /**
     * Unregisters the specified Class from Bukkit.
     * @param clazz The class.
     */
    public static void unregisterAll(Class<? extends SerializationConfig> clazz) {
        ConfigurationSerialization.unregisterClass(clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Property.class)) {
                Class<?> fieldclazz = f.getType();
                if (ConfigurationSerializable.class.isAssignableFrom(fieldclazz)) {
                    Class<? extends ConfigurationSerializable> subclass = fieldclazz.asSubclass(ConfigurationSerializable.class);
                    ConfigurationSerialization.unregisterClass(subclass);
                }
            }
            f.setAccessible(false);
        }
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
                    Class<? extends Serializor<?, ?>> serializorClass = (Class<? extends Serializor<?, ?>>) propertyInfo.value();
                    // get the serializor from SerializorCache
                    Serializor serializor = SerializorCache.getSerializor(serializorClass);
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
                    // get the serializor from SerializorCache
                    Serializor serializor = SerializorCache.getSerializor(serializorClass);
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
     * Sets a property using a {@link String}.
     *
     * @param property The name of the property. You can specify paths to subconfigs with '.'. Example: 'childconfig.value'
     * @param value The new value for the property. Only works if the {@link Serializor} supports deserialization from a {@link String}.
     * @return True at success, false if the operation failed.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final boolean setProperty(String property, String value) {
        try {
            String[] nodes = property.split("\\.");
            if (nodes.length == 1) {
                Field theField = this.getClass().getDeclaredField(nodes[0]);
                theField.setAccessible(true);
                if (!theField.isAnnotationPresent(Property.class))
                    throw new Exception();
                Property propertyInfo = theField.getAnnotation(Property.class);
                Class<Serializor<?, ?>> serializorClass = (Class<Serializor<?, ?>>) propertyInfo.value();
                Serializor serializor = SerializorCache.getSerializor(serializorClass);
                theField.set(this, serializor.deserialize(value, theField.getType()));
                theField.setAccessible(false);
                return true;
            }
            // recursion...
            String nextNode = nodes[0];
            Field nodeField = this.getClass().getDeclaredField(nextNode);
            nodeField.setAccessible(true);
            if (!nodeField.isAnnotationPresent(Property.class))
                throw new Exception();
            SerializationConfig child = (SerializationConfig) nodeField.get(this);
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < nodes.length; i++) {
                sb.append(nodes[i]).append('.');
            }
            sb.deleteCharAt(sb.length() - 1);
            return child.setProperty(sb.toString(), value);
        } catch (Exception e) {
            // we fail sliently
        }
        return false;
    }

    /**
     * This method sets properties in this object to their default-values.
     * <p>
     * <b>IMPORTANT: All properties have to be initialized HERE, never in/before the constructor!</b>
     */
    public abstract void setDefaults();
}
