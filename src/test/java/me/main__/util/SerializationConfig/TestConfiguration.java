package me.main__.util.SerializationConfig;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class TestConfiguration extends SerializationConfig {

    @Property
    public String test1;
    // values without the annotation won't be serialized
    public String test2;

    // primitives can be serialized
    @Property
    public boolean bool;

    // custom serializors work
    @Property(MyCustomSerializor.class)
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

        bool = false;

        custom = new MyCustomType();
        custom.val = "awesome";

        subConfig = new TestSubConfig();
    }

    public static void register() {
        ConfigurationSerialization.registerClass(TestConfiguration.class);
    }
}
