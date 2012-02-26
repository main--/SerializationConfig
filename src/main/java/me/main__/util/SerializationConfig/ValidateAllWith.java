package me.main__.util.SerializationConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Informs {@link SerializationConfig} that all properties in this class
 * should be validated using this {@link Validator} by default.
 * <p>
 * Remember, if the class is not a {@link SerializationConfig},
 * this annotation won't change anything!
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public abstract @interface ValidateAllWith {
    /**
     * The {@link Validator}.
     * @return The {@link Validator}.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Validator> value();
}
