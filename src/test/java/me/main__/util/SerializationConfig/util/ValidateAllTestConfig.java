package me.main__.util.SerializationConfig.util;

import java.util.Map;

import me.main__.util.SerializationConfig.*;

@ValidateAllWith(TestValidator.class)
public class ValidateAllTestConfig extends SerializationConfig {
    public static final class OtherValidator implements Validator<Object> {
        /**
         * {@inheritDoc}
         */
        @Override
        public Object validateChange(String property, Object newValue, Object oldValue)
                throws ChangeDeniedException {
            return newValue;
        }
    }

    @Property
    public String propWithInheritedValidator;
    @Property(validator = OtherValidator.class)
    public String propWithOverriddenValidator;

    public ValidateAllTestConfig() {
        super();
    }

    public ValidateAllTestConfig(Map<String, Object> values) {
        super(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDefaults() {
        propWithInheritedValidator = "propWithInheritedValidator";
        propWithOverriddenValidator = "propWithOverriddenValidator";
    }
}
