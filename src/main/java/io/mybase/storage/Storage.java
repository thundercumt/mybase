package io.mybase.storage;

public interface Storage {
    boolean put(byte[] key, byte[] value);

    byte[] get(byte[] key);

}
