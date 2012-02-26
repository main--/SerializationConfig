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
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    /**
     * The {@link Serializor} used to (de)serialize this property.
     * @return The {@link Serializor} used to (de)serialize this property.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Serializor> serializor() default DefaultSerializor.class;

    /**
     * The {@link Validator} used to validate this property.
     * @return The {@link Validator} used to validate this property.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Validator> validator() default Validator.class;
}
