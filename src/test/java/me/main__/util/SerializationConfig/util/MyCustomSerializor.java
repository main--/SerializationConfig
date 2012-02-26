package me.main__.util.SerializationConfig.util;

import me.main__.util.SerializationConfig.*;

public class MyCustomSerializor implements Serializor<MyCustomType, String> {
    @Override
    public String serialize(MyCustomType from) {
        return from.val;
    }

    @Override
    public MyCustomType deserialize(String serialized, Class<MyCustomType> wanted) {
        MyCustomType obj = new MyCustomType();
        obj.val = serialized;
        return obj;
    }
}
