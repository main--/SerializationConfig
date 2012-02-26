package me.main__.util.SerializationConfig.util;

public class MyCustomType {
    public String val = "test";

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (!(other instanceof MyCustomType))
            return false;

        MyCustomType mother = (MyCustomType) other;
        return mother.val.equals(val);
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }
}
