package me.main__.util.SerializationConfig.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.main__.util.SerializationConfig.*;

public class TestConfiguration extends SerializationConfig {
    public static final class Validator1 implements Validator<String> {
        /**
         * {@inheritDoc}
         */
        @Override
        public String validateChange(String property, String newValue, String oldValue)
                throws ChangeDeniedException {
            if (newValue.contains("deny"))
                throw new ChangeDeniedException();
            else if (newValue.contains("strange"))
                return "strangeValue";
            else if (newValue.contains("silent"))
                return oldValue;
            return newValue;
        }
    }

    // problem here: it's static and private!
    private final class Validator2 implements Validator<String> {
        /**
         * {@inheritDoc}
         */
        @Override
        public String validateChange(String property, String newValue, String oldValue)
                throws ChangeDeniedException {
            return newValue;
        }
    }

    @Property
    public String test1;
    // values without the annotation won't be serialized
    public String test2;

    @Property(validator = Validator1.class)
    public String validatorTest1;
    @Property(validator = Validator2.class)
    public String validatorTest2;

    // primitives can be serialized
    @Property
    public boolean bool;

    // Iterables work
    @Property
    public List<String> stringList;

    // custom serializors work
    @Property(serializor = MyCustomSerializor.class)
    public MyCustomType custom;

    // the default serializor supports SerializationConfigs
    @Property
    public TestSubConfig subConfig;

    public TestConfiguration() {
        super();
    }

    public TestConfiguration(Map<String, Object> values) {
        super(values);
    }

    @Override
    public void setDefaults() {
        test1 = "test1";
        test2 = "test2";

        validatorTest1 = "validatorTest1";
        validatorTest2 = "validatorTest2";

        bool = false;

        stringList = new ArrayList<String>();
        stringList.add("defaultEntry");

        custom = new MyCustomType();
        custom.val = "awesome";

        subConfig = new TestSubConfig();
    }
}
