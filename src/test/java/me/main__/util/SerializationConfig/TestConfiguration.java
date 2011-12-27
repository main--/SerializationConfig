package me.main__.util.SerializationConfig;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class TestConfiguration extends SerializationConfig {

    @Property
    public String test1;
    public String test2;

    @Property
    public boolean bool;

    @Property(MyCustomSerializor.class)
    public MyCustomType custom;

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
    }

    public static void register() {
        ConfigurationSerialization.registerClass(TestConfiguration.class);
    }
}
