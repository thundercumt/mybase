package io.mybase.storage;

import java.io.File;

public abstract class HeapFileStorage implements Storage {

    private File folder;

    public HeapFileStorage(File folder) {
        if (!folder.isDirectory()) {
            throw new StorageException("expecting a folder");
        }
        this.folder = folder;
    }

    public File getWorkFolder() {
        return folder;
    }

    public abstract void open();

    public abstract void close();

    @Override
    public boolean put(byte[] key, byte[] value) {
        return false;
    }

    @Override
    public byte[] get(byte[] key) {
        return null;
    }

}
