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
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class SerializationConfig implements ConfigurationSerializable {
    private static final InstanceCache<Serializor<?, ?>> serializorCache = new InstanceCache<Serializor<?,?>>();
    private static final InstanceCache<Validator<?>> validatorCache = new InstanceCache<Validator<?>>();

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
     * @param values The map bukkit passes to us.
     */
    public SerializationConfig(Map<String, Object> values) {
        this();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Property.class) && !VirtualProperty.class.isAssignableFrom(f.getType())) {
                try {
                    // yay, this field is a property :D
                    // let's continue and try to serialize it
                    Property propertyInfo = f.getAnnotation(Property.class);
                    Class<? extends Serializor<?, ?>> serializorClass = (Class<? extends Serializor<?, ?>>) propertyInfo.serializor();
                    // get the serializor from SerializorCache
                    Serializor serializor = serializorCache.getInstance(serializorClass, this);
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
    @Override
    public final Map<String, Object> serialize() {
        Field[] fields = this.getClass().getDeclaredFields();
        Map<String, Object> ret = new LinkedHashMap<String, Object>();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Property.class) && !VirtualProperty.class.isAssignableFrom(f.getType())) {
                try {
                    // yay, this field is a property :D
                    // let's continue and try to serialize it
                    Property propertyInfo = f.getAnnotation(Property.class);
                    Class<Serializor<?, ?>> serializorClass = (Class<Serializor<?, ?>>) propertyInfo.serializor();
                    // get the serializor from SerializorCache
                    Serializor serializor = serializorCache.getInstance(serializorClass, this);
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
     * Sets a property.
     *
     * @param property The name of the property. You can specify paths to subconfigs with '.'. Example: 'childconfig.value'
     * @param value The new value for the property. It will be automatically casted.
     * @return True at success, false if the operation failed.
     * @throws ClassCastException When the property is unable to hold {@code value}.
     */
    public final boolean setPropertyValue(String property, Object value) throws ClassCastException {
        return setPropertyValue(property, value, false);
    }

    /**
     * Sets a property.
     *
     * @param property The name of the property. You can specify paths to subconfigs with '.'. Example: 'childconfig.value'
     * @param value The new value for the property. It will be automatically casted.
     * @param ignoreCase Whether we should ignore case while searching.
     * @return True at success, false if the operation failed.
     * @throws ClassCastException When the property is unable to hold {@code value}.
     */
    public final boolean setPropertyValue(String property, Object value, boolean ignoreCase) throws ClassCastException {
        try {
            String[] nodes = property.split("\\."); // this is a regex so we have to escape the '.'
            if (nodes.length == 1) {
                Field field = null;
                try {
                    field = ReflectionUtils.getField(nodes[0], this.getClass(), ignoreCase);
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Property.class)) {
                        if (!field.getType().isAssignableFrom(value.getClass()) && !field.getType().isPrimitive())
                            throw new ClassCastException(value.getClass().toString() + " cannot be cast to " + field.getType().toString());

                        Property propertyInfo = field.getAnnotation(Property.class);
                        if (VirtualProperty.class.isAssignableFrom(field.getType())) {
                            // validate
                            try {
                                value = validate(field, propertyInfo, value);
                            } catch (ChangeDeniedException e) {
                                return false;
                            }

                            // it's virtual!
                            VirtualProperty<Object> vProp = (VirtualProperty<Object>) field.get(this);
                            // auto-cast FTW :D
                            vProp.set(value);
                            return true;
                        } else {
                            return validateAndDoChange(field, value);
                        }
                    } else {
                        throw new MissingAnnotationException("Property");
                    }
                } catch (NoSuchPropertyException e) {
                    throw e;
                } catch (MissingAnnotationException e) {
                    throw new NoSuchPropertyException(e);
                } catch (NoSuchFieldException e) {
                    throw new NoSuchPropertyException(e);
                } catch (ClassCastException e) {
                    throw e;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                } finally {
                    if (field != null)
                        field.setAccessible(false);
                }
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
            return child.setPropertyValue(sb.toString(), value);
        } catch (ClassCastException e) {
            throw e;
        } catch (Exception e) {
            // we fail sliently
        }
        return false;
    }

    /**
     * Sets a property using a {@link String}.
     *
     * @param property The name of the property. You can specify paths to subconfigs with '.'. Example: 'childconfig.value'
     * @param value The new value for the property. Only works if the {@link Serializor} supports deserialization from a {@link String}.
     * @return True at success, false if the operation failed.
     */
    public final boolean setProperty(String property, String value) {
        return setProperty(property, value, false);
    }

    /**
     * Sets a property using a {@link String}.
     *
     * @param property The name of the property. You can specify paths to subconfigs with '.'. Example: 'childconfig.value'
     * @param value The new value for the property. Only works if the {@link Serializor} supports deserialization from a {@link String}.
     * @param ignoreCase Whether we should ignore case while searching.
     * @return True at success, false if the operation failed.
     */
    public final boolean setProperty(String property, String value, boolean ignoreCase) {
        try {
            String[] nodes = property.split("\\."); // this is a regex so we have to escape the '.'
            if (nodes.length == 1) {
                Field field = null;
                try {
                    field = ReflectionUtils.getField(nodes[0], this.getClass(), ignoreCase);
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Property.class)) {
                        Property propertyInfo = field.getAnnotation(Property.class);
                        Class<? extends Serializor<?, ?>> serializorClass = (Class<? extends Serializor<?, ?>>) propertyInfo.serializor();
                        Serializor serializor = serializorCache.getInstance(serializorClass, this);
                        Object oVal;
                        try {
                            oVal = serializor.deserialize(value, field.getType());
                        } catch (IllegalPropertyValueException e) {
                            return false;
                        } catch (RuntimeException e) {
                            // throw new IllegalPropertyValueException(e);
                            return false;
                        }
                        if (VirtualProperty.class.isAssignableFrom(field.getType())) {
                            // validate
                            try {
                                oVal = validate(field, propertyInfo, oVal);
                            } catch (ChangeDeniedException e) {
                                return false;
                            }

                            // it's virtual!
                            VirtualProperty<Object> vProp = (VirtualProperty<Object>) field.get(this);
                            // auto-cast FTW :D
                            vProp.set(oVal);
                            return true;
                        } else {
                            return validateAndDoChange(field, oVal);
                        }
                    } else {
                        throw new MissingAnnotationException("Property");
                    }
                } catch (NoSuchPropertyException e) {
                    throw e;
                } catch (MissingAnnotationException e) {
                    throw new NoSuchPropertyException(e);
                } catch (NoSuchFieldException e) {
                    throw new NoSuchPropertyException(e);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                } finally {
                    if (field != null)
                        field.setAccessible(false);
                }
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

    private Object validate(Field field, Property propertyInfo, Object newVal) throws IllegalAccessException, ChangeDeniedException {
        Validator<Object> validator = null;
        if (propertyInfo.validator() != Validator.class) { // only if a validator was set
            validator = validatorCache.getInstance(propertyInfo.validator(), this);
        } else if (this.getClass().isAnnotationPresent(ValidateAllWith.class)) {
            ValidateAllWith validAll = this.getClass().getAnnotation(ValidateAllWith.class);
            validator = validatorCache.getInstance(validAll.value(), this);
        }
        if (validator != null) {
            try {
                newVal = validator.validateChange(field.getName(), newVal, field.get(this));
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Illegal validator!", e);
            }
        }
        return newVal;
    }

    private <T> boolean validateAndDoChange(Field field, T newVal) throws Exception {
        if (!field.getType().isAssignableFrom(newVal.getClass()) && !field.getType().isPrimitive()) // types don't match
            return false;
        Property propInfo = field.getAnnotation(Property.class);
        try {
            newVal = (T) validate(field, propInfo, newVal);
        } catch (ChangeDeniedException e) {
            return false;
        }

        field.set(this, newVal);

        return true;
    }

    /**
     * This method sets properties in this object to their default-values.
     * <p>
     * <b>IMPORTANT: All properties have to be initialized HERE, never in/before the constructor!</b>
     */
    public abstract void setDefaults();
}
