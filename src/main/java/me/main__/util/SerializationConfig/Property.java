package me.main__.util.SerializationConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields with this annotation are (de)serialized with the SerializationConfig.
 * <p>
 * Remember, if the class containing the field is not a {@link SerializationConfig},
 * this annotation won't change anything!
 * <p>
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    /**
     * The {@link Serializor} used to (de)serialize this field.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Serializor> value() default DefaultSerializor.class;
}
