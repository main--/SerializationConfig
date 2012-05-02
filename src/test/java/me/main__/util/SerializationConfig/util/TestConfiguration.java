package me.main__.util.SerializationConfig.util;

import java.util.ArrayList;
import java.util.HashMap;
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

    private final class ParentValidator implements Validator<TestSubConfig> {
        @Override
        public TestSubConfig validateChange(String property, TestSubConfig newValue,
                TestSubConfig oldValue) throws ChangeDeniedException {
            subChange = true;
            return newValue;
        }
    }

    @Property(description = "test1-description")
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
    @Property
    public int integer;

    // Iterables work
    @Property
    public List<String> stringList;

    // custom serializors work
    @Property(serializor = MyCustomSerializor.class)
    public MyCustomType custom;

    // the default serializor supports SerializationConfigs
    @Property(validator = ParentValidator.class)
    public TestSubConfig subConfig;
    public boolean subChange = false;

    // ConfigurationSerializables work
    @Property
    public MyConfigurationSerializable configurationSerializable;

    // VirtualProperty
    @Property(virtualType = String.class)
    public VirtualProperty<String> vString = new VirtualProperty<String>() {
        @Override
        public void set(String newValue) {
            subvString = newValue;
        }

        @Override
        public String get() {
            return subvString;
        }
    };
    public String subvString = "def";

    @Property(virtualType = String.class, persistVirtual = true)
    public VirtualProperty<String> persistString = new VirtualProperty<String>() {
        @Override
        public void set(String newValue) {
            subpersString = newValue;
        }

        @Override
        public String get() {
            return subpersString;
        }
    };
    public String subpersString = "def";

    public TestConfiguration() {
        super();
    }

    public TestConfiguration(Map<String, Object> values) {
        super(values);
    }

    @Override
    protected void setDefaults() {
        test1 = "test1";
        test2 = "test2";

        validatorTest1 = "validatorTest1";
        validatorTest2 = "validatorTest2";

        bool = false;
        integer = 0;

        stringList = new ArrayList<String>();
        stringList.add("defaultEntry");

        custom = new MyCustomType();
        custom.val = "awesome";

        subConfig = new TestSubConfig();

        configurationSerializable = new MyConfigurationSerializable();
    }

    @Override
    public void flushPendingVPropChanges() {
        super.flushPendingVPropChanges();
    }

    protected static Map<String, String> getAliases() {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("firstTest", "test1");
        return aliases;
    }
}
