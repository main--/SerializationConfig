package me.main__.util.SerializationConfig.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class MyConfigurationSerializable implements ConfigurationSerializable {
    public String val = "default";

    public static MyConfigurationSerializable deserialize(Map<String, Object> serialized) {
        MyConfigurationSerializable obj = new MyConfigurationSerializable();
        obj.val = (String) serialized.get("val");
        return obj;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("val", val);
        return ret;
    }
}
