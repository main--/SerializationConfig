package me.main__.util.SerializationConfig.util;

import java.util.Map;

import me.main__.util.SerializationConfig.*;

public class TestSubConfig extends SerializationConfig {

    @Property
    public String val;

    public TestSubConfig() {
        super();
    }

    public TestSubConfig(Map<String, Object> values) {
        super(values);
    }

    @Override
    public void setDefaults() {
        val = "subTest";
    }
}
