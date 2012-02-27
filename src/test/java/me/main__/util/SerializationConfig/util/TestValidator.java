package me.main__.util.SerializationConfig.util;

import me.main__.util.SerializationConfig.*;

public class TestValidator extends ObjectUsingValidator<Object, Object> {
    /*
     * Slightly complicated setup here:
     * The tests will inject the "notification"-object here.
     * That way, we can later verify calls to this validator.
     */
    public static interface Notification {
        void call(String name, Object caller);
    }

    public static Notification notification = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object validateChange(String property, Object newValue, Object oldValue, Object caller)
            throws ChangeDeniedException {
        if (notification != null)
            notification.call(property, caller);
        return newValue;
    }
}
