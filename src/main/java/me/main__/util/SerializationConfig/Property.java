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

    /**
     * If this property is a {@link VirtualProperty}{@code <TYPE>} you have
     * to set this to {@code TYPE.class} because Java generics somehow suck.
     * @return The type of this virtual property.
     */
    Class<?> virtualType() default Object.class;

    /**
     * If this property is a {@link VirtualProperty} you can set
     * this to {@code true} if you want the property to be saved.
     * @return Whether this property should be saved if it's virtual.
     */
    boolean persistVirtual() default false;

    /**
     * A description for this property.
     * @return A description for this property.
     */
    String description() default "<no description set>";
}
