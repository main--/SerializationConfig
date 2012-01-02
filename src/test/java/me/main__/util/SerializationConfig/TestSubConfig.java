package me.main__.util.SerializationConfig;

import java.util.Map;

public class TestSubConfig extends SerializationConfig {

    @Property
    public String val;

    public TestSubConfig() {
        super();
    }

    public TestSubConfig(Map<String, Object> values) {
        super(values);
    }

    public void setDefaults() {
        val = "subTest";
    }

}
